package com.motaharinia.ms.iam.modules.securityuser.presentation.securityuser.roleandpermission;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * مدل دسترسی
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SecurityPermissionDto implements Serializable {
    /**
     * شناسه
     */
    private Long id;
    /**
     * نام دسترسی
     */
    private String title;
    /**
     * فعال و غیرفعال بودن دسترسی
     */
    private Boolean invalid;
}
