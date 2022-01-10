package com.motaharinia.ms.iam.modules.appuser.business.batch.processor;

import com.motaharinia.ms.iam.config.security.oauth2.dto.AppUserDto;
import com.motaharinia.ms.iam.modules.appuser.presentation.dto.create.AppUserCreateExcelDto;
import com.motaharinia.ms.iam.modules.appuser.presentation.dto.create.AppUserCreateRequestDto;
import com.motaharinia.ms.iam.modules.appuser.presentation.dto.create.CustomBatchItemDto;
import com.motaharinia.msutility.tools.calendar.CalendarTools;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.time.ZoneId;

/**
 * @author eng.motahari@gmail.com<br>
 */
@Component
@Slf4j
public class AppUserItemProcessor implements ItemProcessor<CustomBatchItemDto<AppUserCreateExcelDto>, CustomBatchItemDto<AppUserCreateRequestDto>> {

    @Override
    public CustomBatchItemDto<AppUserCreateRequestDto> process(final CustomBatchItemDto<AppUserCreateExcelDto> dto) {

        //اگر در  خواندن اطلاعات از اکسل خطا وجود دارد آن را پردازش نمیکنیم
        if (!ObjectUtils.isEmpty(dto.getException())) {
            return new CustomBatchItemDto(dto.getRowNumber(), dto.getException());
        }

        try {
            //تبدلAppUserCreateExcelRequestDto بهAppUserCreateRequestDto
            log.info("Processing appUser information: {}", dto);
            AppUserDto appUserDto = new AppUserDto();
            appUserDto.setFirstName(dto.getData().getFirstName());
            appUserDto.setLastName(dto.getData().getLastName());
            appUserDto.setNationalCode(dto.getData().getUsername());
            appUserDto.setMobileNo(dto.getData().getMobileNo());
            appUserDto.setEmailAddress(dto.getData().getEmailAddress());

            AppUserCreateRequestDto appUserCreateRequestDto = new AppUserCreateRequestDto();
            appUserCreateRequestDto.setUsername(dto.getData().getUsername());
            appUserCreateRequestDto.setPassword(dto.getData().getPassword());
            appUserCreateRequestDto.setPasswordRepeat(dto.getData().getPasswordRepeat());
            appUserCreateRequestDto.setDateOfBirth(CalendarTools.jalaliToGregorianInstant(dto.getData().getDateOfBirth(), "-", ZoneId.systemDefault()).toEpochMilli());
            appUserCreateRequestDto.setPostalCode(dto.getData().getPostalCode());
            appUserCreateRequestDto.setAddress(dto.getData().getAddress());
            appUserCreateRequestDto.setGeoCityId(dto.getData().getGeoCityId());
            appUserCreateRequestDto.setAppUserDto(appUserDto);

            return new CustomBatchItemDto(dto.getRowNumber(), appUserCreateRequestDto);

        } catch (Exception exception) {
            //اگر در  پردازش  اطلاعات خطا وجود دارد خطا را مدیریت میکنیم که سطر بعدی هم پردازش شود
            return new CustomBatchItemDto(dto.getRowNumber(), exception);
        }
    }

}
