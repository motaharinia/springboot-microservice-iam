package com.motaharinia.ms.iam.external.pointtracker.presentation.dto;

import com.motaharinia.ms.iam.external.pointtracker.business.enumaration.OperationEnum;
import com.motaharinia.msutility.custom.customvalidation.longrange.LongRange;
import com.motaharinia.msutility.custom.customvalidation.required.Required;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Map;

/**
 * کلاس مدل برای اضافه کردن امتیاز به کاربران
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddPointResponseDto implements Serializable {
    /**
     * لیست کد ملی و شناسه تراکنش
     */
    @Required
    private Map<Long, String> userIdTransactionIdMap;
    /**
     * امتیاز مورد نظر برای کاربران
     */
    @Required
    @LongRange(min = 1 , max = 10000)
    private Long point;
    /**
     * مقادیر ثابت عملیات تراکنش امتیاز
     */
    @Required
    private OperationEnum operationEnum;
    /**
     * عنوان نوع عملیات
     */
    private String operationEnumCaption;
}
