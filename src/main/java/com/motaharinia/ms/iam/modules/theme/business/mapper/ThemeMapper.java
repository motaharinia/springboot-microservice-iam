package com.motaharinia.ms.iam.modules.theme.business.mapper;

import com.motaharinia.ms.iam.modules.theme.persistence.odm.ThemeDocument;
import com.motaharinia.ms.iam.modules.theme.presentation.dto.ThemeCreateRequestDto;
import com.motaharinia.ms.iam.modules.theme.presentation.dto.ThemeReadMinimalResponseDto;
import com.motaharinia.ms.iam.modules.theme.presentation.dto.ThemeReadResponseDto;
import com.motaharinia.ms.iam.modules.theme.presentation.dto.ThemeUpdateRequestDto;
import com.motaharinia.msutility.custom.custommapper.CustomMapper;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

/**
 * کلاس مبدل انتیتی و مدل تم
 */
@Mapper(componentModel = "spring")
public interface  ThemeMapper extends CustomMapper {

    ThemeDocument toEntity(ThemeCreateRequestDto dto);

    void toEntity(ThemeUpdateRequestDto dto, @MappingTarget ThemeDocument entity);

    ThemeReadResponseDto toDto(ThemeDocument entity);

    ThemeReadMinimalResponseDto toThemeReadMinimalResponseDto(ThemeDocument entity);
}
