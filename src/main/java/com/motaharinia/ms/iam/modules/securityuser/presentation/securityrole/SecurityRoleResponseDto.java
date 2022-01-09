package com.motaharinia.ms.iam.modules.securityuser.presentation.securityrole;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SecurityRoleResponseDto implements Serializable {
    /**
     * شناسه
     */
    private Long id;
}
