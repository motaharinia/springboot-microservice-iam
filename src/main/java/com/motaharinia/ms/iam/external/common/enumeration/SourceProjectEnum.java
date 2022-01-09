package com.motaharinia.ms.iam.external.common.enumeration;

/**
 * @author eng.motahari@gmail.com<br>
 * مقادیر ثابت پروژه درخواست دهنده
 */
public enum SourceProjectEnum {
    /**
     *پروژه باشگاه مشتریان
     */
    MS_IAM("MS_IAM"),
    /**
     *ارسال دستی پیامک خارج از پروژه های استاندارد
     */
    MANUAL("MANUAL"),
    ;

    private final String value;

    SourceProjectEnum(String value) {
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
