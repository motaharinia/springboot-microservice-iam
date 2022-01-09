package com.motaharinia.ms.iam.modules.appuser.business.exception;


import com.motaharinia.msutility.custom.customexception.business.BusinessException;
import org.jetbrains.annotations.NotNull;

/**
 * کلاس خطا کاربرqrCode
 */
public class AppUserQrCodeException extends BusinessException {

    public AppUserQrCodeException(@NotNull String exceptionClassId, @NotNull String appUserQrCodeExceptionEnum, @NotNull String exceptionDescription) {
        super(AppUserException.class, exceptionClassId, appUserQrCodeExceptionEnum, exceptionDescription);
    }

}
