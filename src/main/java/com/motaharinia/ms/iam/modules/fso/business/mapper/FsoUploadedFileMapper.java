package com.motaharinia.ms.iam.modules.fso.business.mapper;


import com.motaharinia.ms.iam.modules.fso.persistence.orm.fso.FsoUploadedFile;
import com.motaharinia.ms.iam.modules.fso.presentation.FsoUploadedFileDto;
import com.motaharinia.msutility.custom.custommapper.CustomMapper;
import org.mapstruct.Mapper;

/**
 * @author eng.motahari@gmail.com<br>
 * کلاس مبدل انتیتی و مدل فایلهای آپلود شده
 */
@Mapper(componentModel = "spring")
public interface FsoUploadedFileMapper extends CustomMapper {
    FsoUploadedFileDto toDto(FsoUploadedFile entity);
    FsoUploadedFile toEntity(FsoUploadedFileDto dto);
}
