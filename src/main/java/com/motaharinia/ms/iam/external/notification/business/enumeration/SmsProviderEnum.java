package com.motaharinia.ms.iam.external.notification.business.enumeration;

/**
 * @author eng.motahari@gmail.com<br>
 * مقادیر ثابت سرویس ارسال کننده پیامک
 */
public enum SmsProviderEnum {
    /**
     *سرویس ارسال پیامک آتیه
     */
    ATIEH("ATIEH"),
    /**
     *سرویس ارسال پیامک رهیاب
     */
    RAHYAB("RAHYAB"),
    /**
     *سرویس ارسال پیامک راهکار
     */
    RAHKAR("RAHKAR"),
    ;

    private final String value;

    SmsProviderEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
