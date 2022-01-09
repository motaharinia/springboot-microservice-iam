package com.motaharinia.ms.iam.modules.backuser.business.exception;

import com.motaharinia.msutility.custom.customexception.business.BusinessException;
import org.jetbrains.annotations.NotNull;

/**
 * کلاس خطا کاربر برنامه لک
 */
public class BackUserException extends BusinessException {

    public BackUserException(@NotNull String exceptionClassId, @NotNull String backUserExceptionEnum, @NotNull String exceptionDescription) {
        super(BackUserException.class, exceptionClassId, backUserExceptionEnum, exceptionDescription);
    }
}
