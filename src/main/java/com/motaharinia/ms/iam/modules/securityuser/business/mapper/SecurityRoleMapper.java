package com.motaharinia.ms.iam.modules.securityuser.business.mapper;

import com.motaharinia.ms.iam.modules.securityuser.persistence.orm.SecurityRole;
import com.motaharinia.ms.iam.modules.securityuser.presentation.securityrole.SecurityRoleCreateRequestDto;
import com.motaharinia.ms.iam.modules.securityuser.presentation.securityrole.SecurityRoleReadResponseDto;
import com.motaharinia.ms.iam.modules.securityuser.presentation.securityuser.roleandpermission.SecurityRoleDto;
import com.motaharinia.msutility.custom.custommapper.CustomMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


/**
 * @author eng.motahari@gmail.com<br>
 * کلاس مبدل انتیتی و مدل نقش کاربری
 */
@Mapper(componentModel = "spring")
public interface SecurityRoleMapper extends CustomMapper {

    @Mapping(target = "id", ignore = true)
    SecurityRole toEntity(SecurityRoleReadResponseDto dto);
    SecurityRole toEntity(SecurityRoleCreateRequestDto dto);

    SecurityRoleReadResponseDto toDto(SecurityRole entity);
    SecurityRoleDto toSecurityRoleDto(SecurityRole entity);

    default SecurityRole fromId(Long id) {
        if (id == null) {
            return null;
        }
        SecurityRole securityRole = new SecurityRole();
        securityRole.setId(id);
        return securityRole;
    }
}
