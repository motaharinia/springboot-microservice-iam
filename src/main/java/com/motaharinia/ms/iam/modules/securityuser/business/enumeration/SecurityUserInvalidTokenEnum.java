package com.motaharinia.ms.iam.modules.securityuser.business.enumeration;

/**
 *  کلاس مقادیر ثابت برای غیرفعال کردن توکن کاربر بک یا فرانت
 */
public enum SecurityUserInvalidTokenEnum {

    /**
     * فقط توکن کاربر فرانت غیرفعال شود
     */
    JUST_FRONT("JUST_FRONT"),

    /**
     * فقط توکن کاربر بک غیرفعال شود
     */
    JUST_BACK("JUST_BACK"),

    /**
     *  توکن کاربر فرانت و بک هردو غیرفعال شود
     */
    BOTH("BOTH"),
    ;

    private final String value;

    SecurityUserInvalidTokenEnum(String value) {
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
