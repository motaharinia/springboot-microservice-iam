package com.motaharinia.ms.iam.modules.appuserchangelog.business.exception;


import com.motaharinia.msutility.custom.customexception.business.BusinessException;
import org.jetbrains.annotations.NotNull;

/**
 * کلاس خطا  لاگ تغییرات اطلاعات کاربر برنامه فرانت
 */
public class AppUserChangeLogException extends BusinessException {

    public AppUserChangeLogException(@NotNull String exceptionClassId, @NotNull String appUserExceptionEnum, @NotNull String exceptionDescription) {
        super(AppUserChangeLogException.class, exceptionClassId, appUserExceptionEnum, exceptionDescription);
    }

}
