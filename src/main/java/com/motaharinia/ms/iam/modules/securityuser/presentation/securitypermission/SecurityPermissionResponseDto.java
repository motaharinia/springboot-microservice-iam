package com.motaharinia.ms.iam.modules.securityuser.presentation.securitypermission;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SecurityPermissionResponseDto implements Serializable {
    /**
     * شناسه
     */
    private Long id;
}
