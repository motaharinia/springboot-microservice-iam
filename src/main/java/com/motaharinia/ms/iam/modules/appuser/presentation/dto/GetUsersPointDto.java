package com.motaharinia.ms.iam.modules.appuser.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * مدل گرفتن اطلاعات امتیاز و نوع سطح هر کاربر برنامه فرانت
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetUsersPointDto {

    /**
     * شناسه ملی
     */
    private String nationalCode;
    /**
     * امتیاز بالانس شده
     */
    private Long balance;
    /**
     * عنوان انگلیسی
     */
    private String enTitle;
    /**
     * عنوان فارسی
     */
    private String faTitle;
}
