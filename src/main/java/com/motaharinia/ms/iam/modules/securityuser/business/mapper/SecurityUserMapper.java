package com.motaharinia.ms.iam.modules.securityuser.business.mapper;

import com.motaharinia.ms.iam.config.security.oauth2.dto.SecurityUserDto;
import com.motaharinia.ms.iam.modules.securityuser.persistence.orm.SecurityUser;
import com.motaharinia.ms.iam.modules.securityuser.presentation.securityuser.SecurityUserCreateRequestDto;
import com.motaharinia.ms.iam.modules.securityuser.presentation.securityuser.SecurityUserUpdateDto;
import com.motaharinia.msutility.custom.custommapper.CustomMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * @author eng.motahari@gmail.com<br>
 * کلاس مبدل انتیتی و مدل کاربر امنیت
 */
@Mapper(componentModel = "spring")
public interface SecurityUserMapper extends CustomMapper {

    SecurityUserDto toDTO(SecurityUser entity);

    @Mapping(target = "id", ignore = true)
    SecurityUser toEntity(SecurityUserDto dto);

    SecurityUser toEntity(SecurityUserCreateRequestDto dto);

    @Mapping(target = "id", ignore = true)
    SecurityUser toEntity(SecurityUserUpdateDto dto);
}
