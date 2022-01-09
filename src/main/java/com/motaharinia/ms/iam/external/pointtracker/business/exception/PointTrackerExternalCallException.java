package com.motaharinia.ms.iam.external.pointtracker.business.exception;

import com.motaharinia.msutility.custom.customexception.externalcall.ExternalCallException;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpMethod;

public class PointTrackerExternalCallException extends ExternalCallException {
    public PointTrackerExternalCallException(@NotNull String requestUrl, @NotNull HttpMethod requestMethod, @NotNull String requestCode, @NotNull String responseCode, @NotNull String responseCustomError, @NotNull Exception responseException) {
        super(PointTrackerExternalCallException.class, requestUrl, requestMethod, requestCode, responseCode, responseCustomError, responseException);
    }
}
