package com.motaharinia.ms.iam.external.captchaotp.business.service;

import com.motaharinia.ms.iam.external.captchaotp.business.exception.CaptchaOtpClientException;
import com.motaharinia.ms.iam.external.captchaotp.presentation.OtpDto;
import com.motaharinia.ms.iam.external.common.enumeration.SourceProjectEnum;
import com.motaharinia.msutility.custom.customdto.ClientResponseDto;
import com.motaharinia.msutility.tools.rest.RestTools;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

/**
 * @author eng.motahari@gmail.com<br>
 * کلاس پیاده سازی سرویس بیرونی کپچا حامی
 */
@Slf4j
@Service
public class CaptchaOtpExternalServiceImpl implements CaptchaOtpExternalService {


    private final Environment environment;
    private final RestTemplate restTemplate;
    private final String baseUrl;
    private final boolean testActivated;

    //پیامهای خطای بیزینسی
    private static final String BUSINESS_EXCEPTION_EXTERNAL_CAPTCHA_REQUIRED = "BUSINESS_EXCEPTION.EXTERNAL_CAPTCHA_REQUIRED";

    //مقادیر ثابت
    private static final String REPLACE_SOURCE_PROJECT = "%SOURCE_PROJECT%";


    public CaptchaOtpExternalServiceImpl(Environment environment, @LoadBalanced RestTemplate restTemplate) {
        this.environment = environment;
        this.restTemplate = restTemplate;
        this.baseUrl = environment.getRequiredProperty("app.ms-captcha-otp.base-url");
        this.testActivated = environment.getRequiredProperty("app.ms-captcha-otp.test-activated", Boolean.class);
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
     * متد تولیدکننده کلید و مقدار کد کپچا (به همراه تصویر) بر اساس کلید کپچا دلخواه
     *
     * @param sourceProjectEnum پروژه درخواست دهنده
     * @param key               کلید کد کپچا
     * @param captchaLength           طول کپچا
     * @param captchaTtl   طول عمر کپچا
     * @return خروجی: تصویر کپچا
     */
    @Override
    public byte[] captchaCreate(@NotNull SourceProjectEnum sourceProjectEnum, @NotNull String key, @NotNull Integer captchaLength, @NotNull Long captchaTtl) {

        String requestUrl = this.baseUrl + environment.getRequiredProperty("app.ms-captcha-otp.captcha-create-api").replace(REPLACE_SOURCE_PROJECT, sourceProjectEnum.getValue()).replace("%CAPTCHA_KEY%", key).replace("%CAPTCHA_LENGTH%", captchaLength.toString()).replace("%CAPTCHA_TTL%", captchaTtl.toString());

        //فراخوانی سرویس
        ClientResponseDto<byte[]> response = RestTools.call(restTemplate, HttpMethod.GET, requestUrl, "REQ-2001", this.getHeaders(), null,  new ParameterizedTypeReference<>() {
        }, CaptchaOtpExternalServiceImpl.class);
        return response.getData();
    }

    /**
     * متد بررسی کپچا
     *
     * @param sourceProjectEnum پروژه درخواست دهنده
     * @param key               کلید کد کپچا
     * @param value             مقدار کد کپچا
     * @param methodName        نام متد
     * @param username          نام کاربری
     * @param tryCount          تعداد تلاش
     * @param tryTtlInMinutes   مدت زمان فاصله ی بین هر تلاش برای فراخوانی هر متد
     * @param banTtlInMinutes   مدت زمان محدود شدن کاربر بلاک شده روی متد
     */
    @Override
    public void captchaCheck(@NotNull SourceProjectEnum sourceProjectEnum, @NotNull String key, @NotNull String value, @NotNull String methodName, @NotNull String username, @NotNull Integer tryCount, @NotNull Integer tryTtlInMinutes, @NotNull Integer banTtlInMinutes) {
        //بررسی غیرفعال بودن کپچا در تنظیمات
        //if (!testActivated) {
            if (ObjectUtils.isEmpty(key) || ObjectUtils.isEmpty(value)) {
                throw new CaptchaOtpClientException(key, BUSINESS_EXCEPTION_EXTERNAL_CAPTCHA_REQUIRED, "");
            }

            String requestUrl =  this.baseUrl + environment.getRequiredProperty("app.ms-captcha-otp.captcha-check-api").replace(REPLACE_SOURCE_PROJECT, sourceProjectEnum.getValue())
                    .replace("%CAPTCHA_KEY%", key)
                    .replace("%CAPTCHA_VALUE%", value)
                    .replace("%METHOD_NAME%", methodName)
                    .replace("%USERNAME%", username)
                    .replace("%TRY_COUNT%", tryCount.toString())
                    .replace("%TRY_TTL_IN_MINUTES%", tryTtlInMinutes.toString())
                    .replace("%BAN_TTL_IN_MINUTES%", banTtlInMinutes.toString());
            //فراخوانی سرویس
            RestTools.call(restTemplate, HttpMethod.GET, requestUrl, "REQ-2002", this.getHeaders(), null,  new ParameterizedTypeReference<>() {
            }, CaptchaOtpExternalServiceImpl.class);

        //}
    }


    /**
     * متد تولیدکننده رمز یکبار مصرف بر اساس کلید رمز دلخواه
     *
     * @param sourceProjectEnum پروژه درخواست دهنده
     * @param key               کلید کد رمز یکبار مصرف
     * @param otpLength         طول رمز یکبار مصرف
     * @param otpTtl            طول عمر رمز یکبار مصرف
     * @return خروجی: مدل رمز یکبار مصرف
     */
    @Override
    public @NotNull OtpDto otpCreate(@NotNull SourceProjectEnum sourceProjectEnum, @NotNull String key, @NotNull Integer otpLength, @NotNull Long otpTtl) {

        String requestUrl =  this.baseUrl + environment.getRequiredProperty("app.ms-captcha-otp.otp-create-api").replace(REPLACE_SOURCE_PROJECT, sourceProjectEnum.getValue()).replace("%OTP_KEY%", key).replace("%OTP_LENGTH%", otpLength.toString()).replace("%OTP_TTL%", otpTtl.toString());
        //فراخوانی سرویس
        ClientResponseDto<OtpDto> response = RestTools.call(restTemplate, HttpMethod.GET, requestUrl, "REQ-2003", this.getHeaders(), null,  new ParameterizedTypeReference<>() {
        }, CaptchaOtpExternalServiceImpl.class);

        return response.getData();
    }

    /**
     * متد بررسی رمز یکبار مصرف
     *
     * @param sourceProjectEnum پروژه درخواست دهنده
     * @param key               کلید رمز یکبار مصرف
     * @param value             رمز یکبار مصرف
     * @param methodName        نام متد
     * @param username          نام کاربری
     * @param tryCount          تعداد تلاش
     * @param tryTtlInMinutes   مدت زمان فاصله ی بین هر تلاش برای فراخوانی هر متد
     * @param banTtlInMinutes   مدت زمان محدود شدن کاربر بلاک شده روی متد
     */
    @Override
    public void otpCheck(@NotNull SourceProjectEnum sourceProjectEnum, @NotNull String key, @NotNull String value, @NotNull String methodName, @NotNull String username, @NotNull Integer tryCount, @NotNull Integer tryTtlInMinutes, @NotNull Integer banTtlInMinutes) {
        String requestUrl =  this.baseUrl + environment.getRequiredProperty("app.ms-captcha-otp.otp-check-api").replace(REPLACE_SOURCE_PROJECT, sourceProjectEnum.getValue()).replace("%OTP_KEY%", key).replace("%OTP_VALUE%", value)
                .replace("%METHOD_NAME%", methodName)
                .replace("%USERNAME%", username)
                .replace("%TRY_COUNT%", tryCount.toString())
                .replace("%TRY_TTL_IN_MINUTES%", tryTtlInMinutes.toString())
                .replace("%BAN_TTL_IN_MINUTES%", banTtlInMinutes.toString())
                ;
        //فراخوانی سرویس
        RestTools.call(restTemplate, HttpMethod.GET, requestUrl, "REQ-2004", this.getHeaders(), null,  new ParameterizedTypeReference<>() {
        }, CaptchaOtpExternalServiceImpl.class);


    }
}
