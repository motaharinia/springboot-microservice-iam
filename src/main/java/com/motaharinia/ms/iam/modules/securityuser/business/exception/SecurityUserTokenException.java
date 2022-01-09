package com.motaharinia.ms.iam.modules.securityuser.business.exception;

import com.motaharinia.msutility.custom.customexception.business.BusinessException;
import org.jetbrains.annotations.NotNull;

/**
 * @author eng.motahari@gmail.com<br>
 * کلاس خطا توکن امنیت
 */
public class SecurityUserTokenException extends BusinessException {

    public SecurityUserTokenException(@NotNull String exceptionClassId, @NotNull String securityTokenExceptionEnum, @NotNull String exceptionDescription) {
        super(SecurityUserException.class, exceptionClassId, securityTokenExceptionEnum, exceptionDescription);
    }
}
