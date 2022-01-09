package com.motaharinia.ms.iam.modules.geo.Business.exception;

import com.motaharinia.msutility.custom.customexception.business.BusinessException;
import org.jetbrains.annotations.NotNull;

public class GeoException extends BusinessException {
    public GeoException( @NotNull String dataId, @NotNull String message, String description) {
        super(GeoException.class, dataId, message, description);
    }
}
