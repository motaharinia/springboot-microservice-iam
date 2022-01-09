package com.motaharinia.ms.iam.modules.securityuser.presentation.securityuser.roleandpermission;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoleAndPermissionReadResponseDto implements Serializable {
    /**
     * لیست آیدی های نقش کاربری که باید برای کاربر فرانت یا بک برنامه ست شود
     */
    private Set<SecurityRoleDto> securityRoleIdSet = new HashSet<>();
    /**
     * لیست آیدی های دسترسی که باید برای کاربر فرانت یا بک برنامه ست شود
     */
    private Set<SecurityPermissionDto> securityPermissionIncludeIdSet = new HashSet<>();
    /**
     * لیست آیدی های نقش کاربری که باید از کاربر فرانت یا بک برنامه حذف شود
     */
    @JsonIgnore //فعلا در پروژه اکسکلود نداریم
    private Set<SecurityPermissionDto> securityPermissionExcludeIdSet = new HashSet<>();



}
