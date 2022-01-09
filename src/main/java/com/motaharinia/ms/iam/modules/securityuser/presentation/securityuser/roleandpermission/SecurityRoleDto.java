package com.motaharinia.ms.iam.modules.securityuser.presentation.securityuser.roleandpermission;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * مدل نقش کاربری
 */
@Data
@NoArgsConstructor
@AllArgsConstructor

public class SecurityRoleDto implements Serializable {

    /**
     * شناسه
     */
    private Long id;
    /**
     * نام نقش کاربری سکیوریتی
     */
    private String title;
    /**
     * فعال و غیرفعال بودن نقش
     */
    private Boolean invalid;
}
