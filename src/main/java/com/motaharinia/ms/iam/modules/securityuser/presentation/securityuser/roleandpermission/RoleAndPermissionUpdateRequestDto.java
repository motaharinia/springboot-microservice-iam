package com.motaharinia.ms.iam.modules.securityuser.presentation.securityuser.roleandpermission;

import com.motaharinia.msutility.custom.customvalidation.required.Required;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
public class RoleAndPermissionUpdateRequestDto {
    /**
     * شناسه کاربر برنامه فرانت یا بک
     */
    @Required
    Long userId;
    //---------------------------for role
    /**
     * لیست آیدی های نقش کاربری که باید برای کاربر امنیت(باتوجه به بک یا فرانت بودن کاربر) برنامه اضافه شود
     */
    @Required
    private Set<Long> securityRoleIdAddSet = new HashSet<>();
    /**
     * لیست آیدی های نقش کاربری که باید از کاربر  امنیت(باتوجه به بک یا فرانت بودن کاربر) برنامه حذف شود
     */
    private Set<Long> securityRoleIdRemoveSet = new HashSet<>();
    //---------------------------for include permission
    /**
     * لیست آیدی های دسترسی اینکلود که باید برای کاربر  امنیت(باتوجه به بک یا فرانت بودن کاربر) برنامه اضافه شود
     */
    private Set<Long> securityPermissionIncludeIdAddSet = new HashSet<>();
    /**
     * لیست آیدی های دسترسی اینکلود که باید از کاربر  امنیت(باتوجه به بک یا فرانت بودن کاربر) برنامه خذف شود
     */
    private Set<Long> securityPermissionIncludeIdRemoveSet = new HashSet<>();
    //---------------------------for exclude permission
    /**
     * لیست آیدی های نقش کاربری اکسکلود که بایدبرای کاربر  امنیت(باتوجه به بک یا فرانت بودن کاربر) برنامه اضافه شود
     */
    private Set<Long> securityPermissionExcludeIdAddSet = new HashSet<>();
    /**
     * لیست آیدی های نقش کاربری اکسکلود که باید از کاربر  امنیت(باتوجه به بک یا فرانت بودن کاربر) برنامه حذف شود
     */
    private Set<Long> securityPermissionExcludeIdRemoveSet = new HashSet<>();

}
