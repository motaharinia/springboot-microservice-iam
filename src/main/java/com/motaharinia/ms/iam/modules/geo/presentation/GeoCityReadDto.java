package com.motaharinia.ms.iam.modules.geo.presentation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * کلاس مدل مشاهده اطلاعات شهر
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GeoCityReadDto {
    /**
     * شناسه
     */
    private Long id;

    /**
     *عنوان
     */
    private String title;
}

