package com.motaharinia.ms.iam.modules.securityuser.business.mapper;

import com.motaharinia.ms.iam.modules.securityuser.persistence.orm.SecurityUserToken;
import com.motaharinia.ms.iam.modules.securityuser.presentation.securityusertoken.SecurityUserTokenDto;
import com.motaharinia.msutility.custom.custommapper.CustomMapper;
import org.mapstruct.Mapper;

/**
 * کلاس مبدل انتیتی و مدل توکن امنیت
 */
@Mapper(componentModel = "spring")
public interface SecurityUserTokenMapper extends CustomMapper {
    SecurityUserTokenDto toDto(SecurityUserToken entity);
}
