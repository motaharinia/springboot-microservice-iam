package com.motaharinia.ms.iam.external.captchaotp.business.exception;


import com.motaharinia.msutility.custom.customexception.externalcall.ExternalCallException;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpMethod;

/**
 * @author eng.motahari@gmail.com<br>
 * کلاس خطای فراخوانی سرویس کپچا
 */
public class CaptchaOtpClientExternalCallException extends ExternalCallException {

    public CaptchaOtpClientExternalCallException(@NotNull String requestUrl, @NotNull HttpMethod requestMethod, @NotNull String requestCode, @NotNull String responseCode, @NotNull String responseCustomError, @NotNull Exception responseException) {
        super(CaptchaOtpClientExternalCallException.class, requestUrl, requestMethod, requestCode, responseCode, responseCustomError, responseException);
    }

}
