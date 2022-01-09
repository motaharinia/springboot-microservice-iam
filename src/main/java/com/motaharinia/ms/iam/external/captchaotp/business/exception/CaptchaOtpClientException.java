package com.motaharinia.ms.iam.external.captchaotp.business.exception;


import com.motaharinia.msutility.custom.customexception.business.BusinessException;
import org.jetbrains.annotations.NotNull;

/**
 * @author eng.motahari@gmail.com<br>
 * کلاس خطا کپچا
 */
public class CaptchaOtpClientException extends BusinessException {
    public CaptchaOtpClientException(@NotNull String exceptionClassId, @NotNull String exceptionEnumString, @NotNull String exceptionDescription) {
        super(CaptchaOtpClientException.class, exceptionClassId, exceptionEnumString, exceptionDescription);
    }
}
