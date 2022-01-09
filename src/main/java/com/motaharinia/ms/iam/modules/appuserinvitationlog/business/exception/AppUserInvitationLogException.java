package com.motaharinia.ms.iam.modules.appuserinvitationlog.business.exception;


import com.motaharinia.msutility.custom.customexception.business.BusinessException;
import org.jetbrains.annotations.NotNull;

/**
 * کلاس خطا  لاگ کد معرف کاربر برنامه فرانت
 */
public class AppUserInvitationLogException extends BusinessException {

    public AppUserInvitationLogException(@NotNull String exceptionClassId, @NotNull String appUserExceptionEnum, @NotNull String exceptionDescription) {
        super(AppUserInvitationLogException.class, exceptionClassId, appUserExceptionEnum, exceptionDescription);
    }

}
