package com.motaharinia.ms.iam.modules.securityclient.presentation.securityclienttoken;

/**
 * مدل ثبت توکن
 */

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SecurityClientTokenCreateDto {

    /**
     * کلمه کاربری
     */
    private String username;

    /**
     * توکن دسترسی
     */
    private String accessToken;

    /**
     * تاریخ صدور توکن
     */
    private LocalDateTime issuedAt;

    /**
     * تاریخ انقضا توکن
     */
    private LocalDateTime expiredAt;

    /**
     * رفرش توکن
     */
    private String refreshToken;

    /**
     * تاریخ انقضای رفرش توکن
     */
    private LocalDateTime refreshTokenExpiredAt;
}
