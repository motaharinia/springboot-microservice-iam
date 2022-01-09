package com.motaharinia.ms.iam.external;


import com.motaharinia.ms.iam.external.common.state.enumeration.StateProcess;

/**
 * مقادیر ثابت برای فرآیند هایی که نیاز به بیش از یک مرحله دارند
 */
public enum StateProcessEnum implements StateProcess {
    /**
     *KEY: فرآیند ثبت نام
     *VALUE:  مدت زمان لازم برای نگه داری در ردیس به دقیقه
     */
    SIGNUP(15),
    /**
     *KEY:  فرآیند لاگین
     *VALUE:  مدت زمان لازم برای نگه داری در ردیس به دقیقه
     */
    SIGNIN(15),
    /**
     *KEY:  فرآیند فراموشی رمز عبور
     *VALUE:  مدت زمان لازم برای نگه داری در ردیس به دقیقه
     */
    FORGET_PASSWORD(15),
    ;

    private final Integer value;

    StateProcessEnum(Integer value) {
        this.value = value;
    }

    @Override
    public Integer getValue() {
        return value;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
