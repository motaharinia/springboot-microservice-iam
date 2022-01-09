package com.motaharinia.ms.iam.external.userpanel.presentation.dto;

import com.motaharinia.msutility.custom.customvalidation.required.Required;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * کلاس مدل داشبورد
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DashboardDto {
    /**
     * شناسه
     */
    private Long id;
    /**
     * کلید رکورد
     */
    @Required
    private String key;
    /**
     * مقدار رکورد
     */
    @Required
    private String value;
}
