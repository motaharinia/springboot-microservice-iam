package com.motaharinia.ms.iam.modules.securityuser.business.enumeration;

/**
 *  کلاس مقادیر ثابت نحوه ی غیرفعال شدن توکن
 */
public enum SecurityTokenInvalidTypeEnum {

    //خروج از سامانه
    LOGOUT("LOGOUT"),

    //حذف سشن
    REFRESH_TOKEN_TERMINATE("REFRESH_TOKEN_TERMINATE"),

    //تولید رفرش توکن جدید
    REFRESH_TOKEN_RENEW("REFRESH_TOKEN_RENEW"),

    //انقضای رفرش توکن
    REFRESH_TOKEN_EXPIRATION("REFRESH_TOKEN_EXPIRATION"),
    /**
     * کاربر غیرفعال شده است
     */
    SECURITY_USER_INVALID("SECURITY_USER_INVALID"),
    /**
     * کاربر ویرایش شده است
     */
    SECURITY_USER_UPDATE("SECURITY_USER_UPDATE"),
    /**
     * نقش غیرفعال شده است
     */
    SECURITY_ROLE_INVALID("SECURITY_ROLE_INVALID"),
    /**
     * نقش ویرایش شده است
     */
    SECURITY_ROLE_UPDATE("SECURITY_ROLE_UPDATE"),
    ;

    private final String value;

    SecurityTokenInvalidTypeEnum(String value) {
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
