package com.motaharinia.ms.iam.modules.appuser.presentation.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * کلاس مدل پاسخ دریافت تعداد کل کابرها
 */
@Data
public class AppUserTotalCountResponseDto implements Serializable {
    private Integer totalCount;
}
