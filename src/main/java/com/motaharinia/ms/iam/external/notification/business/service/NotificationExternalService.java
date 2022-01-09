package com.motaharinia.ms.iam.external.notification.business.service;

import com.motaharinia.ms.iam.external.common.enumeration.SourceProjectEnum;
import org.jetbrains.annotations.NotNull;

/**
 * @author eng.motahari@gmail.com<br>
 * کلاس اینترفیس سرویس بیرونی ناتیفیکیشن حامی
 */
public interface NotificationExternalService {

    /**
     * متد ارسال پیامک
     *
     * @param sourceProjectEnum پروژه درخواست دهنده ارسال پیامک
     * @param mobileNo          شماره تلفن همراه دریافت کننده پیامک
     * @param messageEnumString محتوای متن پیامک
     * @return خروجی: وضعیت تایید
     */
    String send(@NotNull SourceProjectEnum sourceProjectEnum, @NotNull String mobileNo, @NotNull String messageEnumString);

}
