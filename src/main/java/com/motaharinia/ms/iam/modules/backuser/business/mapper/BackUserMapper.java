package com.motaharinia.ms.iam.modules.backuser.business.mapper;

import com.motaharinia.ms.iam.config.security.oauth2.dto.BackUserDto;
import com.motaharinia.ms.iam.modules.backuser.persistence.orm.BackUser;
import com.motaharinia.ms.iam.modules.backuser.presentation.dto.BackUserCreateRequestDto;
import com.motaharinia.ms.iam.modules.backuser.presentation.dto.BackUserMinimalReadResponseDto;
import com.motaharinia.ms.iam.modules.backuser.presentation.dto.BackUserReadResponseDto;
import com.motaharinia.ms.iam.modules.backuser.presentation.dto.BackUserUpdateRequestDto;
import com.motaharinia.msutility.custom.customjson.serializer.CustomEnum;
import com.motaharinia.msutility.custom.custommapper.CustomMapper;
import com.motaharinia.msutility.tools.string.StringTools;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;

/**
 * کلاس مبدل انتیتی و مدل کاربر بک
 */
@Mapper(componentModel = "spring")
public abstract class   BackUserMapper implements CustomMapper {

    @Autowired
    MessageSource messageSource;

    public abstract BackUserDto toDto(BackUser entity);
    public abstract BackUserReadResponseDto toBackUserReadResponseDto(BackUser entity);
    public abstract BackUserMinimalReadResponseDto toBackUserMinimalReadResponseDto(BackUser entity);

    @Mapping(source = "backUserDto.firstName", target = "firstName")
    @Mapping(source = "backUserDto.lastName", target = "lastName")
    @Mapping(source = "backUserDto.mobileNo", target = "mobileNo")
    @Mapping(source = "backUserDto.emailAddress", target = "emailAddress")
    @Mapping(source = "backUserDto.gender", target = "gender")
    @Mapping(source = "username", target = "nationalCode")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "invalid", ignore = true)
    @Mapping(target = "createAt", ignore = true)
    public abstract BackUser toEntity(BackUserCreateRequestDto dto);

    @Mapping(source = "backUserDto.firstName", target = "firstName")
    @Mapping(source = "backUserDto.lastName", target = "lastName")
    @Mapping(source = "backUserDto.mobileNo", target = "mobileNo")
    @Mapping(source = "backUserDto.emailAddress", target = "emailAddress")
    @Mapping(source = "backUserDto.gender", target = "gender")
    @Mapping(source = "username", target = "nationalCode")
    @Mapping(target = "invalid", ignore = true)
    @Mapping(target = "createAt", ignore = true)
    public abstract void toEntity(BackUserUpdateRequestDto dto, @MappingTarget BackUser entity);

    /**
     * ترجمه اینام
     * @param customEnum اینام
     * @return String ترجمه اینام
     */
    public String translateEnum(CustomEnum customEnum) {
        return StringTools.translateCustomMessage(messageSource, customEnum.getValue());
    }


}
