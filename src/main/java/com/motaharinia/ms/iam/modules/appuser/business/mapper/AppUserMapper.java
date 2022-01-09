package com.motaharinia.ms.iam.modules.appuser.business.mapper;


import com.motaharinia.ms.iam.config.security.oauth2.dto.AppUserDto;
import com.motaharinia.ms.iam.modules.appuser.persistence.orm.AppUser;
import com.motaharinia.ms.iam.modules.appuser.presentation.dto.*;
import com.motaharinia.ms.iam.modules.appuser.presentation.dto.create.AppUserCreateRequestDto;
import com.motaharinia.ms.iam.modules.appuser.presentation.signup.SignupCheckOtpRequestOtpDto;
import com.motaharinia.msutility.custom.customjson.serializer.CustomEnum;
import com.motaharinia.msutility.custom.custommapper.CustomMapper;
import com.motaharinia.msutility.tools.string.StringTools;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;

/**
 * کلاس مبدل انتیتی و مدل کاربر
 */
@Mapper(componentModel = "spring")
public abstract class AppUserMapper implements CustomMapper {

    @Autowired
    MessageSource messageSource;

    public abstract AppUserDto toDto(AppUser entity);

    @Mapping(source = "geoCity.id", target = "geoCityId")
    @Mapping(source = "geoCity.title", target = "geoCityTitle")
    @Mapping(source = "gender", target = "genderCaption")
    @Mapping(source = "gender", target = "gender")
    public abstract AppUserReadResponseDto toAppUserReadResponseDto(AppUser entity);

//    @Mapping(source = "gender", target = "genderCaption")
//    @Mapping(source = "gender", target = "gender")
//    public abstract Set<AppUserValidReadDto> toAppUserValidReadDtoSet(List<AppUser> entity);

    @Mapping(source = "gender", target = "genderCaption")
    @Mapping(source = "gender", target = "gender")
    public abstract AppUserValidReadDto toAppUserValidReadDto(AppUser entity);

    public abstract AppUserAnnualPointDto toAppUserAnnualPointDto(AppUser entity);

    public abstract AppUserUpdateRequestDto toAppUserUpdateRequestDto(AppUserUpdateProfileRequestDto dto);


    @Mapping(source = "username", target = "nationalCode")
    public abstract AppUser toEntity(SignupCheckOtpRequestOtpDto dto);

    @Mapping(source = "appUserDto.firstName", target = "firstName")
    @Mapping(source = "appUserDto.lastName", target = "lastName")
    @Mapping(source = "appUserDto.mobileNo", target = "mobileNo")
    @Mapping(source = "appUserDto.emailAddress", target = "emailAddress")
    @Mapping(source = "appUserDto.gender", target = "gender")
    @Mapping(source = "username", target = "nationalCode")
    public abstract AppUser toEntity(AppUserCreateRequestDto dto);

    @Mapping(source = "appUserDto.firstName", target = "firstName")
    @Mapping(source = "appUserDto.lastName", target = "lastName")
    @Mapping(source = "appUserDto.mobileNo", target = "mobileNo")
    @Mapping(source = "appUserDto.emailAddress", target = "emailAddress")
    @Mapping(source = "appUserDto.gender", target = "gender")
    @Mapping(source = "username", target = "nationalCode")
    @Mapping(target = "invalid", ignore = true)
    @Mapping(target = "createAt", ignore = true)
    public abstract void toEntity(AppUserUpdateRequestDto dto, @MappingTarget AppUser entity);

    @Mapping(source = "appUserDto.firstName", target = "firstName")
    @Mapping(source = "appUserDto.lastName", target = "lastName")
    @Mapping(source = "appUserDto.mobileNo", target = "mobileNo")
    @Mapping(source = "appUserDto.emailAddress", target = "emailAddress")
    @Mapping(source = "appUserDto.gender", target = "gender")
    @Mapping(target = "invalid", ignore = true)
    @Mapping(target = "createAt", ignore = true)
    public abstract void toEntity(AppUserUpdateProfileRequestDto dto, @MappingTarget AppUser entity);


    /**
     * ترجمه اینام
     * @param customEnum اینام
     * @return String ترجمه اینام
     */
    public String translateEnum(CustomEnum customEnum) {
        return StringTools.translateCustomMessage(messageSource, customEnum.getValue());
    }

}
