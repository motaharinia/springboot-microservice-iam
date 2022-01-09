package com.motaharinia.ms.iam.modules.appuser.presentation.changepassword;


import com.motaharinia.msutility.custom.customvalidation.password.Password;
import com.motaharinia.msutility.custom.customvalidation.required.Required;
import com.motaharinia.msutility.custom.customvalidation.stringlength.StringLength;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author eng.motahari@gmail.com<br>
 * کلاس مدل درخواست تغییر رمز عبور
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChangePasswordRequestDto implements Serializable {
    /**
     * رمز عبور فعلی
     */
    @Required
    private String currentPassword;

    /**
     * رمز عبور جدید
     */
    @Required
    @StringLength(min = 5, max = 30, message = "CUSTOM_VALIDATION.PASSWORD_LENGTH")
    @Password(min = 5, max = 30, complicated = true, complicatedSpecialChars = "@$%#*&!" )
    private String newPassword;

    /**
     * تکرار رمز عبور جدید
     */
    @Required
    @StringLength(min = 5, max = 30, message = "CUSTOM_VALIDATION.PASSWORD_LENGTH")
    @Password(min = 5, max = 30, complicated = true, complicatedSpecialChars = "@$%#*&!")
    private String newPasswordRepeat;
}
