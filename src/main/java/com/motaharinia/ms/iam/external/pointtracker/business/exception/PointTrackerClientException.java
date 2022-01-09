package com.motaharinia.ms.iam.external.pointtracker.business.exception;

import com.motaharinia.msutility.custom.customexception.business.BusinessException;
import org.jetbrains.annotations.NotNull;

public class PointTrackerClientException extends BusinessException {

    public PointTrackerClientException(@NotNull String dataId, @NotNull String message, String description) {
        super(PointTrackerClientException.class, dataId, message, description);
    }
}
