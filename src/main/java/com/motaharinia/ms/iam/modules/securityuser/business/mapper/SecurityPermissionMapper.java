package com.motaharinia.ms.iam.modules.securityuser.business.mapper;

import com.motaharinia.ms.iam.modules.securityuser.persistence.orm.SecurityPermission;
import com.motaharinia.ms.iam.modules.securityuser.presentation.securitypermission.SecurityPermissionCreateRequestDto;
import com.motaharinia.ms.iam.modules.securityuser.presentation.securitypermission.SecurityPermissionReadResponseDto;
import com.motaharinia.ms.iam.modules.securityuser.presentation.securityuser.roleandpermission.SecurityPermissionDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SecurityPermissionMapper {
    @Mapping(target = "id" , ignore = true)
    SecurityPermission toEntity(SecurityPermissionReadResponseDto dto);
    SecurityPermissionReadResponseDto toDto(SecurityPermission entity);
    SecurityPermissionDto toSecurityPermissionDto(SecurityPermission entity);

    SecurityPermission toEntity(SecurityPermissionCreateRequestDto dto);


}
