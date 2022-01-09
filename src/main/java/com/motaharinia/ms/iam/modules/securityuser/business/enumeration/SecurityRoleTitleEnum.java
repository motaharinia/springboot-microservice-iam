package com.motaharinia.ms.iam.modules.securityuser.business.enumeration;

public enum SecurityRoleTitleEnum {

    APP_USER("APP_USER"),
    EMPLOYEE("EMPLOYEE"),

;

    private final String value;

    SecurityRoleTitleEnum(String value) {
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
