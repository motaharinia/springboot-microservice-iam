package com.motaharinia.ms.iam.modules.securityuser.business.exception;

import com.motaharinia.msutility.custom.customexception.business.BusinessException;
import org.jetbrains.annotations.NotNull;

/**
 * @author eng.motahari@gmail.com<br>
 * کلاس خطا کاربر امنیت
 */
public class SecurityUserException extends BusinessException {

    public SecurityUserException(@NotNull String exceptionClassId, @NotNull String securityUserExceptionEnum, @NotNull String exceptionDescription) {
        super(SecurityUserException.class, exceptionClassId, securityUserExceptionEnum, exceptionDescription);
    }
}
