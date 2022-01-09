package com.motaharinia.ms.iam.modules.theme.business.exception;

import com.motaharinia.msutility.custom.customexception.business.BusinessException;
import org.jetbrains.annotations.NotNull;

/**
 * کلاس خطا تم
 */
public class ThemeException extends BusinessException {

    public ThemeException(@NotNull String exceptionClassId, @NotNull String backUserExceptionEnum, @NotNull String exceptionDescription) {
        super(ThemeException.class, exceptionClassId, backUserExceptionEnum, exceptionDescription);
    }
}
