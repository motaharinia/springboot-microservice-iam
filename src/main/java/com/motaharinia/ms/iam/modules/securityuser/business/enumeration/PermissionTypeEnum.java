package com.motaharinia.ms.iam.modules.securityuser.business.enumeration;

public enum PermissionTypeEnum {
    //گزینه هایی که حالت بازشونده در منو دارند و دسترسی نیستند
    FOLDER("FOLDER"),
    //دسترسی هایی که در کنترلر با پری آدورایز چک میشوند و در منو نمایش داده میشوند
    AUTHORITY("AUTHORITY"),
    // دسترس هایی که در کنترلر با پری آدورایز چک میشوند و در منو نمایش داده نمیشوند
    HIDDEN_AUTHORITY("HIDDEN_AUTHORITY"),
    ;

    private final String value;

    PermissionTypeEnum(String value) {
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
