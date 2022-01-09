package com.motaharinia.ms.iam.external.notification.business.service;

import com.motaharinia.ms.iam.external.common.enumeration.SourceProjectEnum;
import com.motaharinia.ms.iam.external.notification.presentation.SmsNotificationSendDto;
import com.motaharinia.msutility.custom.customdto.ClientResponseDto;
import com.motaharinia.msutility.tools.rest.RestTools;
import com.motaharinia.msutility.tools.string.StringTools;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.Locale;

/**
 * @author eng.motahari@gmail.com<br>
 * کلاس پیاده سازی سرویس بیرونی ناتیفیکیشن حامی
 */
@Slf4j
@Service
public class NotificationExternalServiceImpl implements NotificationExternalService {

    private final Environment environment;
    private final RestTemplate restTemplate;
    private final MessageSource messageSource;
    private final String baseName;

    public NotificationExternalServiceImpl(Environment environment, @LoadBalanced RestTemplate restTemplate, MessageSource messageSource) {
        this.environment = environment;
        this.restTemplate = restTemplate;
        this.messageSource = messageSource;
        this.baseName = environment.getRequiredProperty("app.ms-notification.base-url");
    }

    /**
     * این متد هدر پیش فرض برای درخواست rest را تولید میکند
     *
     * @return خروجی: هدر پیش فرض برای درخواست rest
     */
    private HttpHeaders getHeaders() {
        //ساخت هدر درخواست
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        return httpHeaders;
    }


    /**
     * متد ارسال پیامک
     *
     * @param sourceProjectEnum پروژه درخواست دهنده ارسال پیامک
     * @param mobileNo          شماره تلفن همراه دریافت کننده پیامک
     * @param messageEnumString محتوای متن پیامک
     * @return خروجی: وضعیت تایید
     */
    @Override
    public String send(@NotNull SourceProjectEnum sourceProjectEnum, @NotNull String mobileNo, @NotNull String messageEnumString) {
        //تولید مدل و مسیر درخواست
        if (messageEnumString.contains("::")) {
            //فورس کردن زبان فارسی برای تنظیم ترجمه ها به صورت موقت
            LocaleContextHolder.setLocale(new Locale("fa", "IR"));
            messageEnumString = StringTools.translateCustomMessage(messageSource, messageEnumString);
        }
        log.info("NotificationServiceImpl.send mobileNo:{} , messageEnumString:{} LocaleContextHolder.getLocale():{}", mobileNo, messageEnumString, LocaleContextHolder.getLocale().getLanguage());
        SmsNotificationSendDto dto = new SmsNotificationSendDto(sourceProjectEnum, mobileNo, messageEnumString);

        String requestUrl =  this.baseName + environment.getRequiredProperty("app.ms-notification.send-api");
        //فراخوانی سرویس
        ClientResponseDto<String> response = RestTools.call(restTemplate, HttpMethod.POST, requestUrl, "REQ-1006", this.getHeaders(), dto,  new ParameterizedTypeReference<>() {
        }, NotificationExternalServiceImpl.class);
        return response.getData();
    }

}
