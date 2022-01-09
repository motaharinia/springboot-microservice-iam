package com.motaharinia.ms.iam.modules.securityuser.business.exception;


import com.motaharinia.msutility.custom.customexception.business.BusinessException;
import org.jetbrains.annotations.NotNull;

/**
 * @author eng.motahari@gmail.com<br>
 * کلاس خطا نقش کاربری
 */
public class SecurityRoleException extends BusinessException {

    public SecurityRoleException(@NotNull String exceptionClassId, @NotNull String securityPermissionExceptionEnum, @NotNull String exceptionDescription) {
        super(SecurityRoleException.class, exceptionClassId, securityPermissionExceptionEnum, exceptionDescription);
    }

}
