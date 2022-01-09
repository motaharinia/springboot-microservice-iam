package com.motaharinia.ms.iam.external.reporter.business.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.motaharinia.ms.iam.external.reporter.business.exception.ReporterClientException;
import com.motaharinia.ms.iam.external.reporter.business.exception.ReporterClientExternalCallException;
import com.motaharinia.ms.iam.external.reporter.presentation.AuditDto;
import com.motaharinia.msutility.custom.customdto.ClientResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.Environment;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

/**
 * @author eng.motahari@gmail.com<br>
 * کلاس پیاده سازی سرویس بیرونی گزارشات بازدید
 */
@Slf4j
@Service
public class ReporterExternalServiceImpl implements ReporterExternalService {

    private final ObjectMapper objectMapper;
    private final Environment environment;
    private final RestTemplate restTemplate;
    private final String baseUrl;

    //پیامهای خطای بیزینسی
    private static final String BUSINESS_EXCEPTION_EXTERNAL_RESPONSE_IS_EMPTY = "BUSINESS_EXCEPTION.EXTERNAL_RESPONSE_IS_EMPTY";

    public ReporterExternalServiceImpl(ObjectMapper objectMapper, Environment environment, RestTemplate restTemplate) {
        this.objectMapper = objectMapper;
        this.environment = environment;
        this.restTemplate = restTemplate;
        this.baseUrl = environment.getRequiredProperty("app.ms-reporter.base-url");
    }

    /**
     * این متد هدر پیش فرض برای درخواست rest را تولید میکند
     *
     * @return خروجی: هدر پیش فرض برای درخواست rest
     */
    private HttpHeaders getHeaders(boolean withToken) {
        //ساخت هدر درخواست
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        return httpHeaders;
    }

    /**
     * متد فراخوانی Rest با روش Post به سرویسهای بیرونی
     *
     * @param requestUrl            نشانی درخواست
     * @param requestCode           کد درخواست که در ترجمه و لاگ خطا از آن استفاده میشود
     * @param httpHeaders           هدرهای درخواست
     * @param body                  بدنه درخواست
     * @param responseNotEmptyCheck آیا خالی بودن پاسخ بررسی شود؟
     * @param responseType          پاسخ پارامتری درخواست
     * @param <T>                   نوع داده بدنه درخواست
     * @param <R>                   نوع داده پاسخ درخواست
     * @return خروجی: داده پاسخ درخواستی
     */
    private <T, R> R callPostRequest(@NotNull String requestUrl, @NotNull String requestCode, @NotNull HttpHeaders httpHeaders, T body, boolean responseNotEmptyCheck, @NotNull ParameterizedTypeReference<R> responseType) {
        //تولید مدل و مسیر درخواست
        ResponseEntity<R> responseEntity;

        try {
            //ارسال درخواست
            if (body != null) {
                responseEntity = this.restTemplate.exchange(requestUrl, HttpMethod.POST, new HttpEntity<>(body, httpHeaders), responseType);
            } else {
                responseEntity = this.restTemplate.exchange(requestUrl, HttpMethod.POST, new HttpEntity<>(httpHeaders), responseType);
            }
        } catch (HttpClientErrorException httpClientErrorException) {
            //خطاهای کلاینت مانند خطاهای عدم وجود نشانی 404 و خطای بیزینس 400
            log.error("ReporterExternalServiceImpl.callPostRequest httpClientErrorException on requestUrl:{} with statusCode:{} and statusText:{} and message:{}", requestUrl, httpClientErrorException.getStatusCode(), httpClientErrorException.getStatusText(), httpClientErrorException.getMessage());
            ClientResponseDto<String> responseDto;
            try {
                responseDto = this.objectMapper.readValue(httpClientErrorException.getResponseBodyAsString(), new TypeReference<ClientResponseDto<String>>() {
                });
            } catch (Exception exception) {
                throw new ReporterClientExternalCallException(requestUrl, HttpMethod.POST, requestCode, httpClientErrorException.getStatusCode().toString(), "", httpClientErrorException);
            }
            throw new ReporterClientExternalCallException(requestUrl, HttpMethod.POST, requestCode, httpClientErrorException.getStatusCode().toString(), responseDto.getException().getMessage(), httpClientErrorException);
        } catch (HttpServerErrorException httpServerErrorException) {
            //خطاهای سرور مانند خطای شبکه 503
            log.error("ReporterExternalServiceImpl.callPostRequest httpServerErrorException on requestUrl:{} with statusCode:{} and statusText:{} and message:{}", requestUrl, httpServerErrorException.getStatusCode(), httpServerErrorException.getStatusText(), httpServerErrorException.getMessage());
            throw new ReporterClientExternalCallException(requestUrl, HttpMethod.POST, requestCode, httpServerErrorException.getStatusCode().toString(), "", httpServerErrorException);
        } catch (ResourceAccessException resourceAccessException) {
            //خطای I/O درخواست
            log.error("ReporterExternalServiceImpl.callPostRequest resourceAccessException(I/O error. Connection refused: connect) on requestUrl:{}", requestUrl, resourceAccessException);
            throw new ReporterClientExternalCallException(requestUrl, HttpMethod.POST, requestCode, "", "I/O error. Connection refused: connect", resourceAccessException);
        } catch (Exception exception) {
            //خطای درخواست
            log.error("ReporterExternalServiceImpl.callPostRequest exception on requestUrl:{}", requestUrl, exception);
            throw new ReporterClientExternalCallException(requestUrl, HttpMethod.POST, requestCode, "", "", exception);
        }

        //اگر پاسخ خالی است
        if (responseNotEmptyCheck && responseEntity.getBody() == null) {
            throw new ReporterClientException(requestUrl, BUSINESS_EXCEPTION_EXTERNAL_RESPONSE_IS_EMPTY, "");
        }

        return responseEntity.getBody();
    }


    /**
     * متد ثبت گزارش به ازای بازدید از سایت
     *
     * @param auditDto مدل گزارش بازدید از api های سایت
     */
    @Override
    @Async
    public void auditCreate(AuditDto auditDto) {
        //تولید مدل و مسیر درخواست
        String requestUrl = this.baseUrl + environment.getRequiredProperty("app.ms-reporter.audit-create-api");

        //فراخوانی سرویس
         callPostRequest(requestUrl, "REQ-0301", this.getHeaders(true), auditDto, false, new ParameterizedTypeReference<>() {
        });
    }
}
