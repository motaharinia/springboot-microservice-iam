package com.motaharinia.ms.iam.modules.appuserchangelog.business.enumeration;

/**
 * اینام نوع لاگ تغییرات کاربر برنامه فرانت
 */
public enum AppUserChangeTypeEnum {
    //تغییر شماره همراه
    MOBILE_NO("MOBILE_NO"),
    //تغییر کد پستی
    POSTAL_CODE("POSTAL_CODE")
    ;

    private final String value;

    AppUserChangeTypeEnum(String value) {
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
