package com.motaharinia.ms.iam.modules.theme.business.enumeration;

public enum ThemeTypeEnum {
    //جستجو با شناسه ملی
    NATIONAL_CODE("NATIONAL_CODE"),
    //جستجو با نام خانوادگی
    LASTNAME("LASTNAME"),
    //جستجو با شماره تلفن همراه
    MOBILE_NO("MOBILE_NO"),
    ;

    private final String value;

    ThemeTypeEnum(String value) {
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
