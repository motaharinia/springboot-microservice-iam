package com.motaharinia.ms.iam.modules.securityclient.business.enumeration;

public enum GrantTypeEnum {

    CLIENT_CREDENTIAL("client_credentials");

    private final String value;

    GrantTypeEnum(String value) {
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
