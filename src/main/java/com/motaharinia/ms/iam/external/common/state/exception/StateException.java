package com.motaharinia.ms.iam.external.common.state.exception;


import com.motaharinia.msutility.custom.customexception.business.BusinessException;
import org.jetbrains.annotations.NotNull;

/**
 * @author eng.motahari@gmail.com<br>
 * کلاس خطا مدیریت وضعیت
 */
public class StateException extends BusinessException {

    public StateException(@NotNull String exceptionClassId, @NotNull String stateManagerExceptionEnum, @NotNull String exceptionDescription) {
        super(StateException.class, exceptionClassId, stateManagerExceptionEnum, exceptionDescription);
    }

}
