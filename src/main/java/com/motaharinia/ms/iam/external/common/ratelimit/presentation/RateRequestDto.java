package com.motaharinia.ms.iam.external.common.ratelimit.presentation;

import com.motaharinia.msutility.custom.customvalidation.required.Required;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author eng.motahari@gmail.com
 * کلاس مدل درخواست محدودیت بازدید
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RateRequestDto {
    /**
     * کلمه کاربری
     */
    @Required
    private String username;
}
