package com.motaharinia.ms.iam.modules.fso;

import com.motaharinia.ms.iam.modules.fso.business.enumeration.FsoFileTypeEnum;
import com.motaharinia.ms.iam.modules.fso.business.enumeration.SubSystemEnum;
import com.motaharinia.ms.iam.modules.fso.presentation.validation.FsoSettingDto;

import java.util.Set;

/**
 * کلاس تعریف و تنظیمات فایلهای پروژه
 */
public class FsoSetting {

    private FsoSetting() {
    }

    /**
     * تنظیمات فایل اکسل ثبت جمعی کاربر برنامه فرانت که فقط بتوان حداکثر 1 فایل اکسل با حجم 300 مگابایت در آن آپلود کرد
     */
    public static final FsoSettingDto MS_IAM_APP_USER_CREATE_BATCH = new FsoSettingDto(SubSystemEnum.MS_IAM, "app_user", "create-batch", 1, Set.of(FsoFileTypeEnum.XLSX), 31457280L,null, null);

    /**
     * تنظیمات فایل تصویر پروفایل کاربر برنامه فرانت که فقط بتوان حداکثر 1 فایل تصویر با حجم 5 مگابایت در آن آپلود کرد
     * ex: /SHOP/member/120/profile-image
     */
    public static final FsoSettingDto MS_IAM_APP_USER_PROFILE_IMAGE = new FsoSettingDto(SubSystemEnum.MS_IAM, "app_user", "profile-image", 1, Set.of(FsoFileTypeEnum.IMAGE), 5120000L, null, null);

    /**
     * تنظیمات تصویر تم که فقط بتوان حداکثر 1000 فایل عکس و آیکن و وکتور با حجم 3 مگابایت در آن آپلود کرد
     */
    public static final FsoSettingDto MS_IAM_THEME_IMAGES = new FsoSettingDto(SubSystemEnum.MS_IAM, "theme", "images", 1000, Set.of(FsoFileTypeEnum.IMAGE,FsoFileTypeEnum.VECTOR), 3145728L, null, null);




}
