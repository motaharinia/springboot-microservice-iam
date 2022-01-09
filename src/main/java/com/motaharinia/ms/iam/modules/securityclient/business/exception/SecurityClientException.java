package com.motaharinia.ms.iam.modules.securityclient.business.exception;

import com.motaharinia.msutility.custom.customexception.business.BusinessException;
import org.jetbrains.annotations.NotNull;

/**
 * @author eng.motahari@gmail.com<br>
 * کلاس خطا کاربر کلاینت
 */
public class SecurityClientException extends BusinessException {

    public SecurityClientException(@NotNull String exceptionClassId, @NotNull String securityUserExceptionEnum, @NotNull String exceptionDescription) {
        super(SecurityClientException.class, exceptionClassId, securityUserExceptionEnum, exceptionDescription);
    }
}
