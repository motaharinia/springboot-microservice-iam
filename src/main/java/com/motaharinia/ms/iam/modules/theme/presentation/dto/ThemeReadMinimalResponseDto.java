package com.motaharinia.ms.iam.modules.theme.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * مدل مشاهده اطلاعات تم
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ThemeReadMinimalResponseDto {

    /**
     * شناسه
     */
    private Long id;

    /**
     * عنوان
     */
    private String title;

    /**
     * آیا تم پیشفرض است
     */
    private Boolean isDefault;
}
