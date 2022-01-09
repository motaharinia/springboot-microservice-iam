package com.motaharinia.ms.iam.modules.appuser.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * کلاس مدل امتیاز سالانه برای کاربران برنامه فرانت
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppUserAnnualPointDto implements Serializable {
    /**
     * شناسه
     */
    private Long id;
    /**
     * شناسه ملی
     */
    private String nationalCode;
    /**
     * شماره تلفن همراه
     */
    private String mobileNo;
}
