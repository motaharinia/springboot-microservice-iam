package com.motaharinia.ms.iam.modules.appuser.presentation.dto.create;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author m.azish
 * کلاس آیتم های batch
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomBatchItemDto<T> {
    /**
     *  داده ی خوانده شده و پردازش شده
     */
    private T data;
    /**
     * شماره سطر اکسل
     */
    private String rowNumber;
    /**
     * خطای اتفاق افتاده در سطر موردنظر
     */
    private Exception exception;

    public CustomBatchItemDto(String rowNumber,T data) {
        this.rowNumber = rowNumber;
        this.data = data;
    }

    public CustomBatchItemDto(String rowNumber, Exception exception) {
        this.rowNumber = rowNumber;
        this.exception = exception;
    }
}
