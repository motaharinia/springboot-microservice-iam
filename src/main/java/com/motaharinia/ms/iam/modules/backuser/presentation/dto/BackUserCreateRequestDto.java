package com.motaharinia.ms.iam.modules.backuser.presentation.dto;

import com.motaharinia.ms.iam.config.security.oauth2.dto.BackUserDto;
import com.motaharinia.msutility.custom.customvalidation.nationalcode.NationalCode;
import com.motaharinia.msutility.custom.customvalidation.password.Password;
import com.motaharinia.msutility.custom.customvalidation.required.Required;
import com.motaharinia.msutility.custom.customvalidation.stringlength.StringLength;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * کلاس مدل درخواست ثبت کاربر برنامه بک
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BackUserCreateRequestDto implements Serializable {
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
     * کاربر برنامه بک
     */
    @Valid
    @Required
    private BackUserDto backUserDto;

    /**
     * شناسه نقش کاربری
     */
    @Required
    private Set<Long> securityRoleIdSet = new HashSet<>();

    /**
     *دسترسی های اضافه بر نقش های کاربری انتخاب شده
     */
    private Set<Long> securityPermissionIncludeIdSet = new HashSet<>();


}