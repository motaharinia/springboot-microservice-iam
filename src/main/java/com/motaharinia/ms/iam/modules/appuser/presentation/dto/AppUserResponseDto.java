package com.motaharinia.ms.iam.modules.appuser.presentation.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


/**
 * کلاس ریسپانس کاربر برنامه فرانت
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppUserResponseDto implements Serializable {

    /**
     * شناسه
     */
    private Long id;
}
