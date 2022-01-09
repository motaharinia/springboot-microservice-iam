package com.motaharinia.ms.iam.external.pointtracker.business.enumaration;


import com.motaharinia.msutility.custom.customjson.serializer.CustomEnum;

/**
 * مقادیر ثابت عملیات تراکنش امتیاز
 */
public enum OperationEnum implements CustomEnum {

    /**
     * افزودن امتیاز سالانه برای رو ثبت نام در باشگاه
     */
    ADD_ANNUAL_POINT_DATE_OF_SIGN_UP("COMBO_ITEM.ADD_POINT_DATE_OF_SIGN_UP"),
    /**
     * افزودن امتیاز سالانه برای روز تولد
     */
    ADD_ANNUAL_POINT_DATE_OF_BIRTH("COMBO_ITEM.ADD_POINT_DATE_OF_BIRTH"),
    /**
     * افزودن امتیاز برای معرف در زمان ثبت نام
     */
    ADD_POINT_FOR_INTRODUCER_IN_SIGN_UP("COMBO_ITEM.ADD_POINT_FOR_INTRODUCER_IN_SIGN_UP"),

    ;

    private final String value;

    OperationEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
