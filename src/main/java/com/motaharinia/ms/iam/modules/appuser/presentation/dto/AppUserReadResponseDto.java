package com.motaharinia.ms.iam.modules.appuser.presentation.dto;


import com.motaharinia.ms.iam.config.security.oauth2.enumeration.GenderEnum;
import com.motaharinia.msutility.tools.fso.view.FileViewDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;


/**
 * کلاس مدل کاربر فرانت برنامه
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppUserReadResponseDto implements Serializable {
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
     * شناسه ملی
     */
    private String nationalCode;

    /**
     * تلفن همراه
     * اگر کاربر سازمان باشد تلفن همراه یکی از اعضای سازمان که سازمان خواسته ست شده است
     */
    private String mobileNo;

    /**
     * پست الکترونیکی
     * اگر کاربر سازمان باشد پست الکترونیک یکی از اعضای سازمان یا خود شرکت که سازمان خواسته ست شده است
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

    /**
     * کد دعوت شخصی
     */
    private String invitationCode;

    /**
     *کد پستی
     */
    private String postalCode;

    /**
     *نشانی منزل
     */
    private String address;

    /**
     *شهر
     */
    private Long geoCityId;

    /**
     *نام شهر
     */
    private String geoCityTitle;

    /**
     * تاریخ تولد
     */
    private Long dateOfBirth;

    /**
     * تصاویر پروفایل
     */
    private ArrayList<FileViewDto> profileImageFileList = new ArrayList<>();

    /**
     * مدل اطلاعات امتیاز و نوع سطح هر کاربر برنامه فرانت
     */
    private GetUsersPointDto getUsersPointDto;

}
