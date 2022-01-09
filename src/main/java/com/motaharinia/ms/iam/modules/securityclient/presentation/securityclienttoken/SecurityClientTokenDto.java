package com.motaharinia.ms.iam.modules.securityclient.presentation.securityclienttoken;

/**
 * مدل ثبت توکن
 */

import com.motaharinia.ms.iam.modules.securityuser.business.enumeration.SecurityTokenInvalidTypeEnum;
import lombok.Data;

import java.io.Serializable;

@Data
public class SecurityClientTokenDto implements Serializable {

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

}
