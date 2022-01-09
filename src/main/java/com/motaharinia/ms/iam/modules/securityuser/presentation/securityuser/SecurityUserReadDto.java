package com.motaharinia.ms.iam.modules.securityuser.presentation.securityuser;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SecurityUserReadDto {
    private Long securityUserId;
    private Long appUserId;
    private Long backUserId;
    private String mobileNo;
}
