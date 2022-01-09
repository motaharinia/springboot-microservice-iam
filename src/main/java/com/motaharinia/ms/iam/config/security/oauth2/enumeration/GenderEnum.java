package com.motaharinia.ms.iam.config.security.oauth2.enumeration;

import com.motaharinia.msutility.custom.customjson.serializer.CustomEnum;

/**
 * مقادیر ثابت جنسیت کاربر برنامه
 */
public enum GenderEnum implements CustomEnum {
    MALE("COMBO_ITEM.GENDER_MALE"),
    FEMALE("COMBO_ITEM.GENDER_FEMALE");

    private final String value;

    GenderEnum(String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
