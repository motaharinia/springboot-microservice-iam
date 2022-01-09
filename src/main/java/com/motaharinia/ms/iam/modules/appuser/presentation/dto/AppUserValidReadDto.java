package com.motaharinia.ms.iam.modules.appuser.presentation.dto;


import com.motaharinia.ms.iam.config.security.oauth2.enumeration.GenderEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


/**
 * کلاس مدل کاربر فرانت برنامه
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppUserValidReadDto implements Serializable {
    /**
     * شناسه
     */
    private Long id;

    /**
     * نام
     */
    private String firstName;

    /**
     * نام خانوادگی
     */
    private String lastName;

    /**
     * شناسه ملی کاربر
     */
    private String nationalCode;

    /**
     * تلفن همراه
     */
    private String mobileNo;

    /**
     * پست الکترونیکی
     */
    private String emailAddress;

    /**
     * جنسیت
     */
    private GenderEnum gender;

    /**
     * ترجمه جنسیت
     */
    private String genderCaption;

    /**
     *تاریخ ایجاد
     */
    private Long createAt;

    /**
     *فعال غیرفعال
     */
    private Boolean invalid;

}
