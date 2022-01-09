package com.motaharinia.ms.iam.modules.securityclient.business.mapper;

import com.motaharinia.ms.iam.modules.securityclient.persistence.orm.SecurityClientToken;
import com.motaharinia.ms.iam.modules.securityclient.presentation.securityclienttoken.SecurityClientTokenDto;
import com.motaharinia.msutility.custom.custommapper.CustomMapper;
import org.mapstruct.Mapper;

/**
 * کلاس مبدل انتیتی و مدل توکن امنیت
 */
@Mapper(componentModel = "spring")
public interface SecurityClientTokenMapper extends CustomMapper {
    SecurityClientTokenDto toDto(SecurityClientToken entity);
}
