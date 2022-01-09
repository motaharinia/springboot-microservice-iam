package com.motaharinia.ms.iam.modules.fso.business.exception;


import com.motaharinia.msutility.custom.customexception.business.BusinessException;
import org.jetbrains.annotations.NotNull;

/**
 * @author eng.motahari@gmail.com<br>
 * کلاس خطا فایل
 */
public class FsoException extends BusinessException {

    public FsoException(@NotNull String exceptionClassId, @NotNull String exceptionEnumString, @NotNull String exceptionDescription) {
        super(FsoException.class, exceptionClassId, exceptionEnumString, exceptionDescription);
    }
}
