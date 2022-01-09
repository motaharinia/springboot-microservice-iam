package com.motaharinia.ms.iam.modules.securityuser.presentation.securityuser;

import com.motaharinia.msutility.custom.customvalidation.required.Required;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * مدل پاسخ کاربر امنیت
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SecurityUserResponseDto implements Serializable {
    @Required
    private Long id;
}
