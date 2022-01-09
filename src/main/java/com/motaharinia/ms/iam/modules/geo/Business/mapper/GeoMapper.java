package com.motaharinia.ms.iam.modules.geo.Business.mapper;


import com.motaharinia.ms.iam.modules.geo.persistence.orm.GeoCity;
import com.motaharinia.ms.iam.modules.geo.presentation.GeoCityReadDto;
import com.motaharinia.msutility.custom.custommapper.CustomMapper;
import org.mapstruct.Mapper;


/**
 * کلاس مبدل انتیتی و مدل جغرافیا
 */
@Mapper(componentModel = "spring")
public interface GeoMapper extends CustomMapper {

    GeoCityReadDto toDto(GeoCity entity);

}
