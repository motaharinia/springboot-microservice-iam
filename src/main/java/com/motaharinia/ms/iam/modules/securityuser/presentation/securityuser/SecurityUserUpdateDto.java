package com.motaharinia.ms.iam.modules.securityuser.presentation.securityuser;

import com.motaharinia.msutility.custom.customvalidation.required.Required;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * مدل ویرایش اطلاعات کاربر امنیت
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SecurityUserUpdateDto implements Serializable {
    /**
     * شماره تلفن همراه جهت بازیابی رمز عبور
     */
    @Required
    private String mobileNo;

    /**
    *کلمه کاربری
     */
    private String username;

    /**
     * رمز عبور
     */
    private String password;

    /**
     * نشانی پست الکترونیک جهت بازیابی رمز عبور
     */
    private String emailAddress;

    /**
     * آیدی کاربر برنامه فرانت
     */
    private Long appUserId;

    /**
     * آیدی کاربر برنامه بک
     */
    private Long backUserId;

    /**
     *  نقشهای کاربری
     */
    private Set<Long> securityRoleIdSet= new HashSet<>();
    /**
     *دسترسی های اضافه بر نقش های کاربری انتخاب شده
     */
    private Set<Long> securityPermissionIncludeIdSet = new HashSet<>();
}
