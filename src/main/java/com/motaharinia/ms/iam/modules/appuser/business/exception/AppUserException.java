package com.motaharinia.ms.iam.modules.appuser.business.exception;


import com.motaharinia.msutility.custom.customexception.business.BusinessException;
import org.jetbrains.annotations.NotNull;

/**
 * @author eng.motahari@gmail.com<br>
 * کلاس خطا کاربر
 */
public class AppUserException extends BusinessException {

    public AppUserException(@NotNull String exceptionClassId, @NotNull String appUserExceptionEnum, @NotNull String exceptionDescription) {
        super(AppUserException.class, exceptionClassId, appUserExceptionEnum, exceptionDescription);
    }

}
