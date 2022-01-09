package com.motaharinia.ms.iam.modules.fso.presentation.validation;

import com.motaharinia.ms.iam.modules.fso.business.enumeration.FsoFileTypeEnum;
import com.motaharinia.ms.iam.modules.fso.business.enumeration.SubSystemEnum;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.Set;

/**
 * @author eng.motahari@gmail.com<br>
 * کلاس مدل تنظیمات فایل
 */
@Data
@AllArgsConstructor
public class FsoSettingDto implements Serializable {
    /**
     * زیرسیستم فایل
     * مثلا catalog
     */
    private SubSystemEnum subSystem;
    /**
     * انتیتی فایل
     * مثلا product
     */
    private String entity;
    /**
     * نوع فایل داخل انتیتی
     * مثلا coverImage
     */
    private String kind;
    /**
     * محدودیت تعداد فایل
     */
    private Integer count;
    /**
     * انواع فایل قابل قبول
     */
    private Set<FsoFileTypeEnum> typeSet;
    /**
     * محدودیت حجم فایل به بایت
     */
    private Long size;
    /**
     * اگر فایل تصویر است، عرض تصویر بندانگشتی
     */
    private Integer width;
    /**
     * اگر فایل تصویر است، طول تصویر بندانگشتی
     */
    private Integer height;


    private static final String ENTITY_ID = "%ENTITYID%";

    /**
     * متد تولید مسیر خام فایل
     * /SHOP/member/%ENTITYID%/profile-picture
     *
     * @return خروجی: مسیر خام فایل
     */
    public String getRawPath() {
        return "/" + subSystem.getValue() + "/" + entity + "/%ENTITYID%/" + kind;
    }

    /**
     * متد تولید مسیر دایرکتوری انتیتی
     * ex: /SHOP/member
     *
     * @return خروجی: مسیر دایرکتوری انتیتی
     */
    public String getEntityDirectoryPath() {
        return "/" + subSystem.getValue() + "/" + entity;
    }


    /**
     * متد تولید مسیر دایرکتوری نوع فایل
     * ex: /SHOP/member/120/profile-image
     *
     * @param entityId شناسه انتیتی
     * @return خروجی: مسیر دایرکتوری نوع فایل
     */
    public String getEntityKindDirectoryPath(Long entityId) {
        return "/" + subSystem.getValue() + "/" + entity + "/" + entityId + "/" + kind;
    }


    /**
     * متد تولید مسیر شناسه انتیتی و نوع فایل
     * ex: /120/profile-image
     *
     * @param entityId شناسه انتیتی
     * @return خروجی: مسیر شناسه انتیتی و نوع فایل
     */
    public String getKindDirectoryPath(Long entityId) {
        return "/" + entityId + "/" + kind;
    }
}
