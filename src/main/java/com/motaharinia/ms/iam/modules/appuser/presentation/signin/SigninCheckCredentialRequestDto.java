package com.motaharinia.ms.iam.modules.appuser.presentation.signin;


import com.motaharinia.msutility.custom.customvalidation.nationalcode.NationalCode;
import com.motaharinia.msutility.custom.customvalidation.required.Required;
import com.motaharinia.msutility.custom.customvalidation.stringlength.StringLength;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


/**
 * @author eng.motahari@gmail.com<br>
 * کلاس مدل درخواست گام اول احراز هویت(بررسی کلمه کاربری و رمز عبور)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SigninCheckCredentialRequestDto implements Serializable {
    /**
     *کلمه کاربری (کد ملی شخص حقیقی / شناسه ملی سازمان)
     */
    @Required
    @StringLength(min = 10, max = 10 , message = "CUSTOM_VALIDATION.USERNAME_LENGTH")
    @NationalCode(message = "CUSTOM_VALIDATION.INVALID_USERNAME_OR_PASSWORD")
    private String username = "";
    /**
     * رمز عبور
     */
    @Required
    private String password = "";
}
