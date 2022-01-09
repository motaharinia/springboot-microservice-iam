package com.motaharinia.ms.iam.modules.appuser.presentation.dto.create;

import com.motaharinia.ms.iam.config.security.oauth2.dto.AppUserDto;
import com.motaharinia.msutility.custom.customvalidation.nationalcode.NationalCode;
import com.motaharinia.msutility.custom.customvalidation.password.Password;
import com.motaharinia.msutility.custom.customvalidation.postalcode.PostalCode;
import com.motaharinia.msutility.custom.customvalidation.required.Required;
import com.motaharinia.msutility.custom.customvalidation.stringlength.StringLength;
import com.motaharinia.msutility.tools.fso.view.FileViewDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * کلاس مدل درخواست ثبت کاربر برنامه فرانت
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppUserCreateRequestDto implements Serializable {
    /**
     * کلمه کاربری
     */
    @Required
    @StringLength(min = 10, max = 10 , message = "CUSTOM_VALIDATION.USERNAME_LENGTH")
    @NationalCode(message = "CUSTOM_VALIDATION.INVALID_USERNAME_OR_PASSWORD")
    private String username = "";
    /**
     * رمز عبور
     */
    @Required
    @StringLength(min = 5, max = 30, message = "CUSTOM_VALIDATION.PASSWORD_LENGTH")
    @Password(min = 5, max = 30, complicated = true, complicatedSpecialChars = "@$%#*&!")
    private String password = "";
    /**
     * تکرار رمز عبور
     */
    @Required
    @StringLength(min = 5, max = 30, message = "CUSTOM_VALIDATION.PASSWORD_LENGTH")
    @Password(min = 5, max = 30, complicated = true, complicatedSpecialChars = "@$%#*&!")
    private String passwordRepeat = "";

    /**
     * کاربر برنامه فرانت
     */
    @Valid
    @Required
    private AppUserDto appUserDto;

    /**
     *تاریخ تولد
     */
    private Long dateOfBirth;

    /**
     *کد پستی
     */
    @PostalCode
    private String postalCode;

    /**
     *نشانی منزل
     */
    @StringLength(max = 150)
    private String address;

    /**
     *شناسه شهر
     */
    private Long geoCityId;

    /**
     * تصاویر پروفایل
     */
    private ArrayList<FileViewDto> profileImageFileList = new ArrayList<>();
}