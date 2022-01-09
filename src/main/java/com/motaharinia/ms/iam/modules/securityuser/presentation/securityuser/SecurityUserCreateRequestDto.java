package com.motaharinia.ms.iam.modules.securityuser.presentation.securityuser;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * مدل ثبت کاربر امنیت
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SecurityUserCreateRequestDto implements Serializable {
    /**
     * کلمه کاربری
     */
    private String username;
    /**
     * رمز عبور
     */
     private String password;

    /**
     * شماره تلفن همراه جهت بازیابی رمز عبور
     */
    private String mobileNo;

    /**
     * نشانی پست الکترونیک جهت بازیابی رمز عبور
     */
    private String emailAddress;

    /**
     *آیدی کاربر برنامه فرانت
     */
    private Long appUserId;

    /**
     *آیدی کاربر برنامه بک
     */
    private Long backUserId;
}
