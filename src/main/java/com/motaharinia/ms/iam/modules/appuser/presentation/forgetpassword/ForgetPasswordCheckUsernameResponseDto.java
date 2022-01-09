package com.motaharinia.ms.iam.modules.appuser.presentation.forgetpassword;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author eng.motahari@gmail.com<br>
 * کلاس مدل پاسخ فراموشی رمز عبور (بررسی کلمه کاربری)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ForgetPasswordCheckUsernameResponseDto implements Serializable {
    /**
     *  کد تایید داخلی (برای زمان تست که توسعه دهندگان نیاز به بررسی پیامک نداشته باشند)
     */
    private String testOtp = "";
}
