package com.motaharinia.ms.iam.modules.securityuser.business.enumeration;

public enum SecurityRoleGridSearchTypeEnum {
    //جستجو با عنوان
    TITLE("TITLE"),
    //جستجو با وضعیت فعال / غیرفعال
    INVALID("INVALID"),

    ;

    private final String value;

    SecurityRoleGridSearchTypeEnum(String value) {
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
