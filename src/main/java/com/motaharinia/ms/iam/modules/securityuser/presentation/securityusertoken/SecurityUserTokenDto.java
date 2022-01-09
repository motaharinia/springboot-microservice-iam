package com.motaharinia.ms.iam.modules.securityuser.presentation.securityusertoken;

/**
 * مدل ثبت توکن
 */

import com.motaharinia.ms.iam.modules.securityuser.business.enumeration.SecurityTokenInvalidTypeEnum;
import lombok.Data;

import java.io.Serializable;

@Data
public class SecurityUserTokenDto implements Serializable {

    /**
     * کلمه کاربری
     */
    private String username;

    /**
     * آدرس آی پی کاربر
     */
    private String ipAddress;

    /**
     * توکن دسترسی
     */
    private String accessToken;

    /**
     * تاریخ صدور توکن
     */
    private Long issuedAt;

    /**
     * تاریخ انقضا توکن
     */
    private Long expiredAt;

    /**
     *نام مرورگر
     */
    private String browser;

    /**
     *نسخه مرورگر
     */
    private String browserVersion;

    /**
     * سیستم عامل
     */
    private String operatingSystem;

    /**
     * نوع دستگاه
     */
    private String deviceType;


    /**
     * رفرش توکن
     */
    private String refreshToken;

    /**
     * تاریخ انقضای رفرش توکن
     */
    private Long refreshTokenExpiredAt;

    /**
     * تاریخ غیرفعال شدن
     */
    private Long invalidDate;

    /**
     * دلیل غیرفعال شدن
     */
    SecurityTokenInvalidTypeEnum invalidEnum;

    /**
     *آیا توکن برای فرانت است؟
     */
    private Boolean isFront;
}
