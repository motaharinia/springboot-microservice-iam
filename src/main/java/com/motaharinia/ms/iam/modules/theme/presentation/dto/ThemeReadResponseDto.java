package com.motaharinia.ms.iam.modules.theme.presentation.dto;

import com.motaharinia.msutility.tools.fso.view.FileViewDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * مدل مشاهده اطلاعات تم
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ThemeReadResponseDto {

    /**
     * شناسه
     */
    private Long id;

    /**
     * عنوان
     */
    private String title;

    /**
     * هش مپ تنظیمات تم
     */
    private HashMap<String,String> settingHashMap = new HashMap<>();

    /**
     * عکس های مربوط به تم
     */
    private ArrayList<FileViewDto> imageList = new ArrayList<>();

    /**
     * هشمپ تصاویر تم
     */
    private HashMap<String,String> imageHashMap = new HashMap<>();

    /**
     * آیا تم پیشفرض است
     */
    private Boolean isDefault;
}
