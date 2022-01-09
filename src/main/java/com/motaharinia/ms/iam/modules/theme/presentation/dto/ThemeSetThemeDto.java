package com.motaharinia.ms.iam.modules.theme.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * کلاس مدل درخواست ثبت تم
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ThemeSetThemeDto implements Serializable {
    /**
     * شناسه تم
     */
    @NotNull
    private Long id;

}