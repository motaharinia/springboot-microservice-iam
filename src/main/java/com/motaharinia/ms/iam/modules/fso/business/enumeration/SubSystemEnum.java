package com.motaharinia.ms.iam.modules.fso.business.enumeration;


/**
 * @author eng.motahari@gmail.com<br>
 * مقادیر ثابت زیر سیستمهای پروژه
 */
public enum SubSystemEnum {

    /**
     * زیرسیستم iam
     */
    MS_IAM("MS_IAM");

    private final String value;

    SubSystemEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
