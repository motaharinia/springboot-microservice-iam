package com.motaharinia.ms.iam.config.batch;

/**
 * کلید هش مپ برای ایمپورت اکسل
 */
public enum BatchKeyConstant {

    /**
     * کلید هش مپ خطا برای ایمپورت اکسل(Batch)
     */
    EXCEPTION_LOG("EXCEPTION_LOG");

    private final String value;

    BatchKeyConstant(String value) {
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
