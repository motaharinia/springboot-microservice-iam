package com.motaharinia.ms.iam.modules.securityuser.business.exception;

import com.motaharinia.msutility.custom.customexception.business.BusinessException;
import org.jetbrains.annotations.NotNull;

/**
 * @author eng.motahari@gmail.com<br>
 * کلاس خطا کاربر امنیت
 */
public class SecurityPermissionException extends BusinessException {

    public SecurityPermissionException(@NotNull String exceptionClassId, @NotNull String securityPermissionExceptionEnum, @NotNull String exceptionDescription) {
        super(SecurityPermissionException.class, exceptionClassId, securityPermissionExceptionEnum, exceptionDescription);
    }
}
