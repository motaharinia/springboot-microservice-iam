package com.motaharinia.ms.iam.modules.appuser.presentation.dto;

import com.motaharinia.ms.iam.config.security.oauth2.dto.AppUserDto;
import com.motaharinia.msutility.custom.customvalidation.postalcode.PostalCode;
import com.motaharinia.msutility.custom.customvalidation.required.Required;
import com.motaharinia.msutility.custom.customvalidation.stringlength.StringLength;
import com.motaharinia.msutility.tools.fso.view.FileViewDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import java.util.ArrayList;

/**
 * کلاس مدل ویرایش پروفایل توسط خود کاربر برنامه فرانت
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppUserUpdateProfileRequestDto {

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
