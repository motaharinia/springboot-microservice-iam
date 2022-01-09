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
 * کلاس مدل کاربر فرانت برنامه
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppUserDto implements Serializable {
    /**
     * شناسه
     */
    private Long id;

    /**
     * نام
     */
    //@Required
    @StringLength(min = 2 , max = 30, message = "CUSTOM_VALIDATION.STRING_LENGTH" )
    private String firstName;

    /**
     * نام خانوادگی
     */
    //@Required
    @StringLength(min = 2 , max = 30, message = "CUSTOM_VALIDATION.STRING_LENGTH")
    private String lastName;

    /**
     * تلفن همراه
     */
    @StringLength(min = 11 , max = 11, message = "CUSTOM_VALIDATION.MOBILE_LENGTH")
    @Required
    @Mobile
    private String mobileNo;

    /**
     * پست الکترونیکی
     * اگر کاربر سازمان باشد پست الکترونیک یکی از اعضای سازمان یا خود شرکت که سازمان خواسته ست شده است
     */
    @StringLength(min = 5 , max = 50, message = "CUSTOM_VALIDATION.MOBILE_LENGTH")
    @Email
    private String emailAddress;

    /**
     * جنسیت
     */
    private GenderEnum gender;

    /**
     * فیلدهای id و  createAt و invalid و nationalCode جهت مشاهده و ایجاد توکن استفاده میشوند و نباید پاک شوند
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
