package com.motaharinia.ms.iam.modules.appuser.presentation.signup;

import com.motaharinia.msutility.custom.customvalidation.required.Required;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SignupCheckOtpRequestOtpDto extends SignupCheckCredentialRequestDto {
    /**
     * کد تایید
     */
    @Required
    private String otp;
    /**
     * مرا به خاطر بسپار
     */
    private Boolean rememberMe = false;
    /**
     * کد معرف کاربر برنامه فرانت جهت ثبت نام در سایت(کد معرفی است که دیگران به ثبت نام کننده میدهند)
     */
    private String invitationCode;

    public SignupCheckOtpRequestOtpDto(String username, String password, String passwordRepeat, String mobileNo, String otp, Boolean rememberMe, String invitationCode) {
        super(username, password, passwordRepeat, mobileNo);
        this.otp = otp;
        this.rememberMe = rememberMe;
        this.invitationCode = invitationCode;
    }

}