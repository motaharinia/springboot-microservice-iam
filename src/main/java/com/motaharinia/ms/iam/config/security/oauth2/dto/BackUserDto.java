package com.motaharinia.ms.iam.config.security.oauth2.dto;


import com.motaharinia.ms.iam.config.security.oauth2.enumeration.GenderEnum;
import com.motaharinia.msutility.custom.customvalidation.mobile.Mobile;
import com.motaharinia.msutility.custom.customvalidation.required.Required;
import com.motaharinia.msutility.custom.customvalidation.stringlength.StringLength;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import java.io.Serializable;


/**
 * کلاس مدل کاربر بک برنامه
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BackUserDto implements Serializable {
    /**
     * شناسه
     */
    private Long id;

    /**
     * نام
     */
    @Required
    @StringLength(min = 2 , max = 30 , message = "CUSTOM_VALIDATION.STRING_LENGTH")
    private String firstName;

    /**
     * نام خانوادگی
     */
    @Required
    @StringLength(min = 2 , max = 30, message = "CUSTOM_VALIDATION.STRING_LENGTH")
    private String lastName;


    /**
     * تلفن همراه
     * اگر کاربر سازمان باشد تلفن همراه یکی از اعضای سازمان که سازمان خواسته ست شده است
     */
    @Required
    @Mobile
    private String mobileNo;

    /**
     * جنسیت
     */
    @Required
    private GenderEnum gender;

    /**
     * پست الکترونیکی
     * اگر کاربر سازمان باشد پست الکترونیک یکی از اعضای سازمان یا خود شرکت که سازمان خواسته ست شده است
     */
    @Email
    private String emailAddress;

    /**
     * فیلدهای id و  createAt و invalid و nationalCode جهت مشاهده و ایجاد توکن استفاده میشوند
     */
    /**
     * شناسه ملی
     */
    private String nationalCode;
    /**
     *تاریخ ایجاد
     */
    private Long createAt;
    /**
     *فعال / غیرفعال
     */
    private Boolean invalid;

}
