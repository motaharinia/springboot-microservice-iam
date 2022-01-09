package com.motaharinia.ms.iam.external.reporter.business.exception;


import com.motaharinia.msutility.custom.customexception.business.BusinessException;
import org.jetbrains.annotations.NotNull;

/**
 * @author eng.motahari@gmail.com<br>
 * کلاس خطا ناتیفیکیشن
 */
public class ReporterClientException extends BusinessException {

    public ReporterClientException(@NotNull String exceptionClassId, @NotNull String exceptionEnumString, @NotNull String exceptionDescription) {
        super(ReporterClientException.class, exceptionClassId, exceptionEnumString, exceptionDescription);
    }

}
