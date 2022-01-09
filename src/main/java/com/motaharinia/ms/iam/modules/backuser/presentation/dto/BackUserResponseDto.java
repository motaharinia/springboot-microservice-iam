package com.motaharinia.ms.iam.modules.backuser.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


/**
 * کلاس ریسپانس کاربر برنامه بک
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BackUserResponseDto implements Serializable {
    /**
     * شناسه
     */
    private Long id;

}
