package com.motaharinia.ms.iam.modules.backuser.presentation.signin;

import com.motaharinia.msutility.custom.customvalidation.nationalcode.NationalCode;
import com.motaharinia.msutility.custom.customvalidation.required.Required;
import com.motaharinia.msutility.custom.customvalidation.stringlength.StringLength;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


/**
 * @author eng.motahari@gmail.com<br>
 * کلاس مدل درخواست گام دوم احراز هویت(بررسی کد تایید داخلی)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SigninCheckOtpRequestDto implements Serializable {
    /**
     * کلمه کاربری (کد ملی)
     */
    @Required
    @StringLength(min = 10, max = 10 , message = "CUSTOM_VALIDATION.USERNAME_LENGTH")
    @NationalCode(message = "CUSTOM_VALIDATION.INVALID_USERNAME_OR_PASSWORD")
    private String username;
    /**
     * رمز عبور
     */
    @Required
    private String password;
    /**
     *کد تایید داخلی
     */
    @Required
    private String otp;
    /**
     * مرا به خاطر بسپار
     */
    private Boolean rememberMe = false;
}
