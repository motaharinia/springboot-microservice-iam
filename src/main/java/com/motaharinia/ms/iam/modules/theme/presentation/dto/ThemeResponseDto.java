package com.motaharinia.ms.iam.modules.theme.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


/**
 * کلاس ریسپانس تم
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ThemeResponseDto implements Serializable {
    /**
     * شناسه
     */
    private Long id;

}
