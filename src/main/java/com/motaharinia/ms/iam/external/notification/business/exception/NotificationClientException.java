package com.motaharinia.ms.iam.external.notification.business.exception;


import com.motaharinia.msutility.custom.customexception.business.BusinessException;
import org.jetbrains.annotations.NotNull;

/**
 * @author eng.motahari@gmail.com<br>
 * کلاس خطا ناتیفیکیشن
 */
public class NotificationClientException extends BusinessException {

    public NotificationClientException(@NotNull String exceptionClassId, @NotNull String exceptionEnumString, @NotNull String exceptionDescription) {
        super(NotificationClientException.class, exceptionClassId, exceptionEnumString, exceptionDescription);
    }

}
