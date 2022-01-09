package com.motaharinia.ms.iam.external.reporter.business.exception;


import com.motaharinia.msutility.custom.customexception.externalcall.ExternalCallException;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpMethod;

/**
 * @author eng.motahari@gmail.com<br>
 * کلاس خطای فراخوانی سرویس ناتیفیکیشن
 */
public class ReporterClientExternalCallException extends ExternalCallException {

    public ReporterClientExternalCallException(@NotNull String requestUrl, @NotNull HttpMethod requestMethod, @NotNull String requestCode, @NotNull String responseCode, @NotNull String responseCustomError, @NotNull Exception responseException) {
        super(ReporterClientExternalCallException.class, requestUrl, requestMethod, requestCode, responseCode, responseCustomError, responseException);
    }

}
