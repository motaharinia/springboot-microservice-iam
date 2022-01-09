package com.motaharinia.ms.iam.modules.appuser.business.enumeration;

public enum AppUserGridSearchTypeEnum {
    //جستجو با نام
    FIRSTNAME("FIRSTNAME"),
    //جستجو با نام خانوادگی
    LASTNAME("LASTNAME"),
    //جستجو با شناسه ملی
    NATIONAL_CODE("NATIONAL_CODE"),
    ;

    private final String value;

    AppUserGridSearchTypeEnum(String value) {
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
