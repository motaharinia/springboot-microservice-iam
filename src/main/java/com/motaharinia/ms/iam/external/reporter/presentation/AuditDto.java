package com.motaharinia.ms.iam.external.reporter.presentation;

import com.motaharinia.ms.iam.external.common.enumeration.SourceProjectEnum;
import com.motaharinia.msutility.custom.customvalidation.required.Required;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;

/**
 * کلاس مدل گزارشات بازدید از api های سایت
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuditDto {
    /**
     *پروژه درخواست دهنده
     */
    @Required
    private SourceProjectEnum sourceProjectEnum;
    /**
     * نام کلاس
     */
    @Required
    private String className;
    /**
     * نام متد
     */
    @Required
    private String methodName;
    /**
     * نشانی بازدید شده
     */
    @Required
    private String apiAddress;
    /**
     * هش مپ هدرهای درخواست
     */
    private HashMap<String, String> apiRequestHeaderMap = new HashMap<>();
    /**
     * هش مپ هدرهای پاسخ
     */
    private HashMap<String, String> apiResponseHeaderMap = new HashMap<>();
    /**
     * کد وضعیت پاسخ
     */
    @Required
    private int apiResponseStatusCode;
    /**
     * مدت زمان اجرا
     */
    @Required
    private Long executeDuration;
    /**
     * کلمه کاربری کاربر لاگین شده
     */
    private String username;
    /**
     * آدرس آی پی کاربر
     */
    @Required
    private String ipAddress;
}
