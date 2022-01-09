package com.motaharinia.ms.iam.modules.appuser.presentation.signup;

import com.motaharinia.msutility.custom.customvalidation.mobile.Mobile;
import com.motaharinia.msutility.custom.customvalidation.nationalcode.NationalCode;
import com.motaharinia.msutility.custom.customvalidation.password.Password;
import com.motaharinia.msutility.custom.customvalidation.required.Required;
import com.motaharinia.msutility.custom.customvalidation.stringlength.StringLength;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * کلاس مدل درخواست گام اول ثبت نام(بررسی کلمه کاربری و  رمز عبور)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SignupCheckCredentialRequestDto implements Serializable {
    /**
     * کلمه کاربری
     */
    @Required
    @StringLength(min = 10, max = 10 , message = "CUSTOM_VALIDATION.USERNAME_LENGTH")
    @NationalCode(message = "CUSTOM_VALIDATION.INVALID_USERNAME_OR_PASSWORD")
    private String username = "";
    /**
     * رمز عبور
     */
    @Required
    @StringLength(min = 5, max = 30, message = "CUSTOM_VALIDATION.PASSWORD_LENGTH")
    @Password(min = 5, max = 30, complicated = true, complicatedSpecialChars = "@$%#*&!")
    private String password = "";
    /**
     * تکرار رمز عبور
     */
    @Required
    @StringLength(min = 5, max = 30, message = "CUSTOM_VALIDATION.PASSWORD_LENGTH")
    @Password(min = 5, max = 30, complicated = true, complicatedSpecialChars = "@$%#*&!")
    private String passwordRepeat = "";

    /**
     * تلفن همراه
     */
    @StringLength(min = 11 , max = 11, message = "CUSTOM_VALIDATION.MOBILE_LENGTH")
    @Required
    @Mobile
    private String mobileNo;
}
