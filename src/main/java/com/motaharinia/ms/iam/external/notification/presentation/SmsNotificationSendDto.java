package com.motaharinia.ms.iam.external.notification.presentation;

import com.motaharinia.ms.iam.external.common.enumeration.SourceProjectEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * کلاس مدل ارسال پیامک
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class SmsNotificationSendDto implements Serializable {
    /**
     * پروژه درخواست دهنده ارسال پیامک
     * چون برای هر پروژه یک provider دیفالت در تیبل notification ست شده است ، وقتی نام پروژه رو میفرستیم در سلکت ازش استفاده میکنیم که از کدام provider باید استفاده شود
     */
    private SourceProjectEnum sourceProjectEnum;
    /**
     * شماره تلفن همراه دریافت کننده پیامک
     */
    private String mobileNo;
    /**
     * محتوای متن پیامک
     */
    private String messageEnumString;
}
