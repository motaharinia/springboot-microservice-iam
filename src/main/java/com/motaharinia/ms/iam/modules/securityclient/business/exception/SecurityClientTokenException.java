package com.motaharinia.ms.iam.modules.securityclient.business.exception;

import com.motaharinia.ms.iam.modules.securityuser.business.exception.SecurityUserException;
import com.motaharinia.msutility.custom.customexception.business.BusinessException;
import org.jetbrains.annotations.NotNull;

/**
 * @author eng.motahari@gmail.com<br>
 * کلاس خطا توکن امنیت
 */
public class SecurityClientTokenException extends BusinessException {

    public SecurityClientTokenException(@NotNull String exceptionClassId, @NotNull String securityTokenExceptionEnum, @NotNull String exceptionDescription) {
        super(SecurityUserException.class, exceptionClassId, securityTokenExceptionEnum, exceptionDescription);
    }
}
