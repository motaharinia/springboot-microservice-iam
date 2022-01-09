package com.motaharinia.ms.iam.modules.backuser.presentation.forgetpassword;

import com.motaharinia.ms.iam.config.security.oauth2.dto.BearerTokenDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author eng.motahari@gmail.com<br>
 * کلاس مدل پاسخ فراموشی رمز عبور (بررسی کد تایید)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ForgetPasswordCheckOtpResponseDto implements Serializable {
    /**
     * توکن احراز هویت
     */
    private BearerTokenDto bearerTokenDto;
}
