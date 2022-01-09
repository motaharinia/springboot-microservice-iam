package com.motaharinia.ms.iam.external.reporter.business.annotation;

import com.motaharinia.ms.iam.config.security.oauth2.dto.LoggedInUserDto;
import com.motaharinia.ms.iam.config.security.oauth2.resource.ResourceUserTokenProvider;
import com.motaharinia.ms.iam.external.common.enumeration.SourceProjectEnum;
import com.motaharinia.ms.iam.external.reporter.business.service.ReporterExternalService;
import com.motaharinia.ms.iam.external.reporter.presentation.AuditDto;
import com.motaharinia.msutility.tools.network.NetworkTools;
import com.motaharinia.msutility.tools.network.requestinfo.RequestInfoDto;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Optional;

/**
 * @author eng.motahari@gmail.com<br>
 * کلاس Aspect انوتیشن بازرسی بازدید متد یک Api
 */
@Aspect
@Component
@Slf4j
public class ReporterAuditAspect {

    /**
     * به ازای هر ریکوئست به متدهای انوتیت شده با @ReporterAudit این هش مپ قرار است قبل از فراخوانی آن متد مقدار دهی شود
     * key : هر ریکوئست از ابتدا تا انتها در یک ترد جداگانه اجرا میشود و از آیدی ترد به همراه نام متد برای کلید هش مپ استفاده شده است
     * value : مدل ثبت گزارش
     */
    HashMap<String, AuditDto> reporterHashMap = new HashMap<>();


    private final ReporterExternalService reporterExternalService;
    private final ResourceUserTokenProvider resourceUserTokenProvider;

    public ReporterAuditAspect(ReporterExternalService reporterExternalService, ResourceUserTokenProvider resourceUserTokenProvider) {
        this.reporterExternalService = reporterExternalService;
        this.resourceUserTokenProvider = resourceUserTokenProvider;
    }

    @Before("@annotation(com.motaharinia.ms.iam.external.reporter.business.annotation.ReporterAudit)")
    public void logBefore(JoinPoint joinPoint) {
        if (joinPoint == null) {
            return;
        }

        //به دست آوردن نام کلاس
        String className = joinPoint.getTarget().getClass().getName();
        //به دست آوردن نام متد
        String methodName = joinPoint.getSignature().getName();
        log.info("logAfter: DAO layer operating information: {} -> {}", className, methodName);

        //هدرهای درخواست
        HashMap<String, String> headerMap = new HashMap<>();

        HttpServletRequest httpServletRequest = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        if (!ObjectUtils.isEmpty(httpServletRequest)) {
            //به دست آوردن هدرهای درخواست
            Enumeration<String> headerNames = httpServletRequest.getHeaderNames();
            if (headerNames != null) {
                while (headerNames.hasMoreElements()) {
                    String headerName = headerNames.nextElement();
                    headerMap.put(headerName, httpServletRequest.getHeader(headerName));
                }
            }
        }

        String username = "";
        RequestInfoDto requestInfoDto = NetworkTools.readCurrentRequestInfo(false);
        Optional<LoggedInUserDto> loggedInUserDtoOptional = resourceUserTokenProvider.getLoggedInDto();
        if (loggedInUserDtoOptional.isPresent()) {
            username = loggedInUserDtoOptional.get().getUsername();
        }

        //ساختن آبجکت از Dto موردنظر و پر کردن فیلدهای آن
        AuditDto auditDto = new AuditDto(SourceProjectEnum.MS_IAM, className, methodName, requestInfoDto.getFullUrl(), headerMap, new HashMap<>(), 0, System.currentTimeMillis(), username, requestInfoDto.getIpAddress());

        log.info("logAfter: logBefore thread Id :  {}", Thread.currentThread().getId());
        //اضافه کردن Dto  در هش مپ - هر ریکوئست در یک ترد جداگانه اجرا میشود و از آیدی ترد به همراه نام متد برای کلید هش مپ استفاده شده است
        reporterHashMap.put(methodName + Thread.currentThread().getId(), auditDto);
    }

    @AfterThrowing(value = "@annotation(com.motaharinia.ms.iam.external.reporter.business.annotation.ReporterAudit)", throwing = "exception")
    public void logAfterThrowing(JoinPoint joinPoint, Exception exception) {
        if (joinPoint == null) {
            return;
        }
        String methodName = joinPoint.getSignature().getName();
        this.logAfter(methodName);
        log.error("logAfter: Target Method resulted into exception, message {}", exception.getMessage());
    }

    @AfterReturning("@annotation(com.motaharinia.ms.iam.external.reporter.business.annotation.ReporterAudit)")
    public void logAfterReturning(JoinPoint joinPoint) {
        if (joinPoint == null) {
            return;
        }
        String methodName = joinPoint.getSignature().getName();
        this.logAfter(methodName);
    }

    private void logAfter(String methodName) {
        //هدرهای پاسخ
        HashMap<String, String> headerMap = new HashMap<>();

        HttpServletResponse httpServletResponse = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getResponse();
        if (!ObjectUtils.isEmpty(httpServletResponse)) {
            //به دست آوردن هدرهای پاسخ
            Collection<String> headerNamesCollection = httpServletResponse.getHeaderNames();
            if (headerNamesCollection != null) {
                for (String headerName : headerNamesCollection) {
                    headerMap.put(headerName, httpServletResponse.getHeader(headerName));
                }
            }

            //به دست آوردن اطلاعات هر درخواست با توجه به کلید هش مپ
            String key = methodName + Thread.currentThread().getId();
            AuditDto auditDto = reporterHashMap.get(key);
            //محاسبه مدت زمان اجرای هر متد
            auditDto.setExecuteDuration(System.currentTimeMillis() - auditDto.getExecuteDuration());
            auditDto.setApiResponseStatusCode(httpServletResponse.getStatus());
            auditDto.setApiResponseHeaderMap(headerMap);

            //ثبت در دیتابیس
            reporterExternalService.auditCreate(auditDto);

            log.info("logAfter: reporterHashMapSize : {}", reporterHashMap.size());
            log.info("logAfter: reporterHashMap : {}", reporterHashMap);
            log.info("logAfter: thread Id :  {}\r\n\r\n", Thread.currentThread().getId());
            //بعد از اتمام کار باید کلید از هش مپ پاک شود
            reporterHashMap.remove(key);
        }
    }


}


