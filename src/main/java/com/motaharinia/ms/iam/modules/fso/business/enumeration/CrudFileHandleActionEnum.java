package com.motaharinia.ms.iam.modules.fso.business.enumeration;


/**
 * @author eng.motahari@gmail.com<br>
 * مقادیر ثابت فعالیت ماژول که روی فایل بعد از آپلود اتفاق می افتد
 */
public enum CrudFileHandleActionEnum {
    /**
     * ثبت فایل جدید برای ثبت انتیتی جدید
     */
    ENTITY_CREATE("ENTITY_CREATE"),
    /**
     * ویرایش فایلهای یک انتیتی
     */
    ENTITY_UPDATE("ENTITY_UPDATE"),
    /**
     * حذف فایلهای یک انتیتی
     */
    ENTITY_DELETE("ENTITY_DELETE");

    private final String value;

    CrudFileHandleActionEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
