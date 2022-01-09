package com.motaharinia.ms.iam.modules.securityuser.presentation.securityrole;

import com.motaharinia.msutility.custom.customvalidation.required.Required;
import com.motaharinia.msutility.custom.customvalidation.stringlength.StringLength;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SecurityRoleCreateRequestDto {
    /**
     * نام دسترسی
     */
    @Required
    @StringLength(min = 2 ,max = 30 , message = "CUSTOM_VALIDATION.STRING_LENGTH")
    private String title;
    /**
     * لیست آیدی های دسترسی
     */
    private Set<Long> securityPermissionSet =new HashSet<>();
}
