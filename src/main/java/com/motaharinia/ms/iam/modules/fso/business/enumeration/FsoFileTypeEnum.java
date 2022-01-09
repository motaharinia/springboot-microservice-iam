package com.motaharinia.ms.iam.modules.fso.business.enumeration;


/**
 * @author eng.motahari@gmail.com<br>
 * مقادیر ثابت انواع فایل
 */
public enum FsoFileTypeEnum {

    /**
     *تصاویر
     */
    IMAGE("JPG,JPEG,PNG,BMP,TIF,TIFF"),
    /**
     *وکتور
     */
    VECTOR("SVG,FCM,SVGZ"),
    /**
     *فایلهای اداری
     */
    OFFICE_DOCUMENTS("DOC,DOCX,XLS,XLSX,CSV,PPT,PPTX"),
    /**
     *پی دی اف
     */
    PDF("PDF"),
    /**
     *فایلهای فشرده
     */
    ARCHIVE("ZIP,RAR"),
    /**
     * اکسل
     */
    XLSX("XLSX"),
    ;

    private final String value;

    FsoFileTypeEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
