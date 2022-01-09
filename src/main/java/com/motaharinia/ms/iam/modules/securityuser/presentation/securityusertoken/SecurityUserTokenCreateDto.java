package com.motaharinia.ms.iam.modules.securityuser.presentation.securityusertoken;

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
public class SecurityUserTokenCreateDto {

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

    /**
     * آیا رمز عبور به خاطر سپرده شود
     */
    private Boolean rememberMe;

    /**
     * آیدی ارجاع به خودش
     * زمانی که توکن جدید تولید میشود اگر از توکن دیگری استفاده کرده باشد آیدی توکن استفاده شده در این فیلد قرار میگیرد
     */
    private Long referenceId;

    /**
     *آیا توکن برای فرانت است؟
     */
    private Boolean isFront = true;
}
