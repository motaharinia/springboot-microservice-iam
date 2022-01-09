package com.motaharinia.ms.iam.modules.securityuser.presentation.securityrole;

import com.motaharinia.msutility.custom.customvalidation.required.Required;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SecurityRoleUpdateRequestDto extends SecurityRoleCreateRequestDto {
    /**
     * شناسه
     */
    @Required
    private Long id;
}
