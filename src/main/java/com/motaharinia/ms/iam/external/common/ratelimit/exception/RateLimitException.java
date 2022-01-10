package com.motaharinia.ms.iam.external.common.ratelimit.exception;


import com.motaharinia.msutility.custom.customexception.business.BusinessException;
import org.jetbrains.annotations.NotNull;

/**
 * @author eng.motahari@gmail.com <br>
 * rateLimit کلاس خطا
 */
public class RateLimitException extends BusinessException {

    public RateLimitException(@NotNull String exceptionClassId, @NotNull String exceptionEnumString, @NotNull String exceptionDescription) {
        super(RateLimitException.class, exceptionClassId, exceptionEnumString, exceptionDescription);
    }

}
