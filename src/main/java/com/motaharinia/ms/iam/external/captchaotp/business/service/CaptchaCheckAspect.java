package com.motaharinia.ms.iam.external.captchaotp.business.service;

import com.motaharinia.ms.iam.config.security.oauth2.dto.LoggedInUserDto;
import com.motaharinia.ms.iam.config.security.oauth2.resource.ResourceUserTokenProvider;
import com.motaharinia.ms.iam.external.captchaotp.business.exception.CaptchaOtpClientException;
import com.motaharinia.ms.iam.external.captchaotp.business.exception.CaptchaOtpClientExternalCallException;
import com.motaharinia.ms.iam.external.captchaotp.presentation.AspectUsernameDto;
import com.motaharinia.ms.iam.external.common.enumeration.SourceProjectEnum;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.Optional;

@Aspect
@Component
@Slf4j
public class CaptchaCheckAspect {

    private final Environment environment;
    private final CaptchaOtpExternalService captchaOtpExternalService;
    private final ResourceUserTokenProvider resourceUserTokenProvider;

    public CaptchaCheckAspect(Environment environment, CaptchaOtpExternalService captchaOtpExternalService, ResourceUserTokenProvider resourceUserTokenProvider) {
        this.environment = environment;
        this.captchaOtpExternalService = captchaOtpExternalService;
        this.resourceUserTokenProvider = resourceUserTokenProvider;
    }

    private static final String BUSINESS_EXCEPTION_PARAMETER_NOT_FOUND = "BUSINESS_EXCEPTION.PARAMETER_NOT_FOUND";

    @Before("@annotation(com.motaharinia.ms.iam.external.captchaotp.business.service.CaptchaCheck)")
    public void validateCaptcha(JoinPoint joinPoint) {
        String captchaKey = "captcha-key";
        String captchaValue = "captcha-value";
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        if (!ObjectUtils.isEmpty(request)) {
            if (!ObjectUtils.isEmpty(environment.getRequiredProperty("app.ms-captcha-otp.header-key")) && !ObjectUtils.isEmpty(request.getHeader(environment.getRequiredProperty("app.ms-captcha-otp.header-key")))) {
                captchaKey = request.getHeader(environment.getRequiredProperty("app.ms-captcha-otp.header-key"));
            }
            if (!ObjectUtils.isEmpty(environment.getRequiredProperty("app.ms-captcha-otp.header-value")) && !ObjectUtils.isEmpty(request.getHeader(environment.getRequiredProperty("app.ms-captcha-otp.header-value")))) {
                captchaValue = request.getHeader(environment.getRequiredProperty("app.ms-captcha-otp.header-value"));
            }
        }
        SourceProjectEnum sourceProjectEnum = SourceProjectEnum.MANUAL;
        if (!ObjectUtils.isEmpty(environment.getRequiredProperty("app.ms-captcha-otp.source-project"))) {
            sourceProjectEnum = SourceProjectEnum.valueOf(environment.getRequiredProperty("app.ms-captcha-otp.source-project"));
        }

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        CaptchaCheck captchaCheckAnnotation = method.getAnnotation(CaptchaCheck.class);

        //ست کردن نام کاربری
        String username;
        //گرفتن نام کاربری از کانتکس سکیوریتی
        Optional<LoggedInUserDto> loggedInUserDtoOptional = resourceUserTokenProvider.getLoggedInDto();
        if (loggedInUserDtoOptional.isEmpty()) {
            //گرفتن نام کاربری از ورودی های متد
            username = this.getUsernameFromMethodParameter(joinPoint);
        } else {
            username = loggedInUserDtoOptional.get().getUsername();
        }

        try {
            captchaOtpExternalService.captchaCheck(sourceProjectEnum, captchaKey, captchaValue, method.getName(), username, captchaCheckAnnotation.tryCount(), captchaCheckAnnotation.tryTtlInMinutes(), captchaCheckAnnotation.banTtlInMinutes());
        } catch (CaptchaOtpClientExternalCallException captchaOtpClientExternalCallException) {
            if (captchaOtpClientExternalCallException.getResponseCode().equalsIgnoreCase("400")) {
                throw new CaptchaOtpClientException(captchaKey, captchaOtpClientExternalCallException.getResponseCustomError(), sourceProjectEnum + " key:" + captchaKey + " value:" + captchaValue + " method:" + method.getName() + " username:" + username);
            }
            throw captchaOtpClientExternalCallException;
        }
    }

    /**
     * متد دریافت کلمه کاربری از ورودی های متد
     */
    private String getUsernameFromMethodParameter(@NotNull JoinPoint joinPoint) {
        if (Objects.nonNull(joinPoint) && joinPoint.getSignature() instanceof MethodSignature) {
            MethodSignature method = (MethodSignature) joinPoint.getSignature();
            Class[] parameterClassArray = method.getParameterTypes();
            for (int t = 0; t < parameterClassArray.length; t++) {
                if (AspectUsernameDto.class.isAssignableFrom(parameterClassArray[t])) {
                    Object[] parameterObjectArray = joinPoint.getArgs();
                    AspectUsernameDto aspectUsernameDto = (AspectUsernameDto) parameterObjectArray[t];
                    if (!ObjectUtils.isEmpty(aspectUsernameDto))
                        return aspectUsernameDto.getAspectUsername();
                }
            }
        }
        throw new CaptchaOtpClientException("", BUSINESS_EXCEPTION_PARAMETER_NOT_FOUND, "");
    }
}
