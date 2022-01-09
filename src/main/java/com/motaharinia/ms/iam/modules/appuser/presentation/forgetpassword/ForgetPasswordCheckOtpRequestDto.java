package com.motaharinia.ms.iam.modules.appuser.presentation.forgetpassword;


import com.motaharinia.msutility.custom.customvalidation.nationalcode.NationalCode;
import com.motaharinia.msutility.custom.customvalidation.password.Password;
import com.motaharinia.msutility.custom.customvalidation.required.Required;
import com.motaharinia.msutility.custom.customvalidation.stringlength.StringLength;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author eng.motahari@gmail.com<br>
 * کلاس مدل درخواست فراموشی رمز عبور(بررسی کد تایید)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ForgetPasswordCheckOtpRequestDto implements Serializable {
    /**
     * کلمه کاربری
     */
    @Required
    @StringLength(min = 10, max = 10 , message = "CUSTOM_VALIDATION.USERNAME_LENGTH")
    @NationalCode(message = "CUSTOM_VALIDATION.INVALID_USERNAME_OR_PASSWORD")
    private String username;

    /**
     * رمز عبور جدید
     */
    @StringLength(min = 5, max = 30, message = "CUSTOM_VALIDATION.PASSWORD_LENGTH")
    @Password(min = 5, max = 30, complicated = true, complicatedSpecialChars = "@$%#*&!")
    @Required
    private String newPassword;

    /**
     * تکرار رمز عبور جدید
     */
    @StringLength(min = 5, max = 30, message = "CUSTOM_VALIDATION.PASSWORD_LENGTH")
    @Password(min = 5, max = 30, complicated = true, complicatedSpecialChars = "@$%#*&!")
    @Required
    private String newPasswordRepeat;

    /**
     * کد تایید
     */
    @Required
    private String otp;
    /**
     * مرا به خاطر بسپار
     */
    private Boolean rememberMe = false;
}
