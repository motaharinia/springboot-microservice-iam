package com.motaharinia.ms.iam.modules.securityuser.presentation.readToken;

import com.motaharinia.ms.iam.config.security.oauth2.dto.SecurityUserDto;
import lombok.Data;

@Data
public class ReadTokenDto {
    SecurityUserDto securityUserDto;
     String role;
}
