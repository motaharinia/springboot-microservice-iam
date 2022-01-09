package com.motaharinia.ms.iam.modules.appuser.business.batch.reader;

import com.motaharinia.ms.iam.config.security.oauth2.enumeration.GenderEnum;
import com.motaharinia.ms.iam.modules.appuser.presentation.dto.create.AppUserCreateExcelDto;
import com.motaharinia.ms.iam.modules.appuser.presentation.dto.create.CustomBatchItemDto;
import org.springframework.batch.item.excel.RowMapper;
import org.springframework.batch.item.excel.support.rowset.RowSet;
import org.springframework.util.ObjectUtils;

/**
 * کلاس مبدل اکسل به dto
 */
public class AppUserExcelRowMapper implements RowMapper<CustomBatchItemDto<AppUserCreateExcelDto>> {

    @Override
    public CustomBatchItemDto<AppUserCreateExcelDto> mapRow(RowSet rowSet) throws Exception {
        try {
            AppUserCreateExcelDto dto = new AppUserCreateExcelDto();
            dto.setFirstName(rowSet.getColumnValue(0));
            dto.setLastName(rowSet.getColumnValue(1));
            dto.setUsername(rowSet.getColumnValue(2));
            dto.setMobileNo(rowSet.getColumnValue(3));
            dto.setEmailAddress(rowSet.getColumnValue(4));
            dto.setDateOfBirth(rowSet.getColumnValue(5));
            dto.setPostalCode(rowSet.getColumnValue(6));
            dto.setAddress(rowSet.getColumnValue(7));
            dto.setGeoCityId(ObjectUtils.isEmpty(rowSet.getColumnValue(8)) ? null : Double.valueOf(rowSet.getColumnValue(8)).longValue());
            dto.setGender(ObjectUtils.isEmpty(rowSet.getColumnValue(9)) ? null : GenderEnum.valueOf(rowSet.getColumnValue(9)));
            dto.setPassword(rowSet.getColumnValue(10));
            dto.setPasswordRepeat(rowSet.getColumnValue(11));
            return new CustomBatchItemDto<>(String.valueOf(rowSet.getCurrentRowIndex()), dto);
        } catch (Exception exception) {
            return new CustomBatchItemDto(String.valueOf(rowSet.getCurrentRowIndex()), exception);
        }

    }
}

