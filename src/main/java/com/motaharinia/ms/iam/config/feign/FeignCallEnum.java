package com.motaharinia.ms.iam.config.feign;


import org.springframework.util.ObjectUtils;

import java.util.Arrays;

/**
 * @author eng.motahari@gmail.com<br>
 * مقادیر ثابت فراخوانی های بیرونی سرویسهای پروژه
 */

public enum FeignCallEnum {
    /**
     * دریافت تصویر کپچا
     */
    REQ_1001("CaptchaOtpConsumer#create(SourceProjectEnum,String,Integer,Long)"),
    /**
     * بررسی تصویر کپچا
     */
    REQ_1002("CaptchaOtpConsumer#check(SourceProjectEnum,String,String,String,String,Integer,Integer,Integer)");

    private final String value;

    FeignCallEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    static String getRequestNo(String value) {
        FeignCallEnum feignCallEnum = Arrays.stream(values()).filter(item -> item.getValue().equals(value)).findFirst().orElse(null);
        if (ObjectUtils.isEmpty(feignCallEnum)) {
            return "UNKNOWN-REQUEST";
        } else {
            return feignCallEnum.toString();
        }
    }
}
