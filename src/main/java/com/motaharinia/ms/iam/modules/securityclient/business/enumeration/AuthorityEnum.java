package com.motaharinia.ms.iam.modules.securityclient.business.enumeration;

public enum AuthorityEnum {

    READ_PROFILE("READ_PROFILE");

    private final String value;

    AuthorityEnum(String value) {
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
