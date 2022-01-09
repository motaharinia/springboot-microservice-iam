package com.motaharinia.ms.iam.modules.backuser.presentation.changepassword;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author eng.motahari@gmail.com<br>
 * کلاس مدل پاسخ تغییر رمز عبور
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChangePasswordResponseDto implements Serializable {

    /**
     * کلمه کاربری
     */
    private String username;

}
