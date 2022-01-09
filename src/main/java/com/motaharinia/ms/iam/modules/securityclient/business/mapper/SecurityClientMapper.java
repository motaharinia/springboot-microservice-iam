package com.motaharinia.ms.iam.modules.securityclient.business.mapper;

import com.motaharinia.ms.iam.modules.securityclient.persistence.orm.SecurityClient;
import com.motaharinia.ms.iam.modules.securityclient.presentation.SecurityClientRequestDto;
import com.motaharinia.ms.iam.modules.securityclient.presentation.SecurityClientResponseDto;
import org.mapstruct.Mapper;

/**
 * client کلاس مبدل انتیتی و مدل کاربر
 */
@Mapper(componentModel = "spring")
public interface SecurityClientMapper {
    SecurityClient toEntity(SecurityClientRequestDto dto);
    SecurityClientResponseDto toDto(SecurityClient entity);
}
