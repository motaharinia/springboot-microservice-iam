package com.motaharinia.ms.iam.modules.appuser.business.batch.reader;

import com.motaharinia.ms.iam.modules.appuser.presentation.dto.create.AppUserCreateExcelDto;
import com.motaharinia.ms.iam.modules.appuser.presentation.dto.create.CustomBatchItemDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.AfterStep;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.excel.poi.PoiItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

/**
 * کلاس reader اکسل  کاربر برنامه فرانت
 */

@Component
@StepScope
@Slf4j
public class AppUserItemReader implements ItemReader<CustomBatchItemDto<AppUserCreateExcelDto>> {

    private PoiItemReader<CustomBatchItemDto<AppUserCreateExcelDto>> itemReader = new PoiItemReader<>();

    public AppUserItemReader(@Value("#{jobParameters[excelFilePath]}") String excelFilePath) throws Exception {
        log.info("AppUserItemReader() readerSourceFile:" + excelFilePath);
        if (!ObjectUtils.isEmpty(excelFilePath)) {
            //itemReader.setResource(new ClassPathResource(excelFilePath));
            //itemReader.setResource(new InputStreamResource(new FileInputStream(excelFilePath)));
            itemReader.setResource(new FileSystemResource(excelFilePath));
            //خط اول اکسل را اسکیپ میکند
            itemReader.setLinesToSkip(1);
            //فقط تا 901 سطر از اکسل را میخواند
            //itemReader.setMaxItemCount(901);
            // اگر ریسورس وجود نداشته باشد متد open خطا خواهد داد
            itemReader.setStrict(true);
            itemReader.setRowMapper(new AppUserExcelRowMapper());
            itemReader.afterPropertiesSet();
            //باز کردن فایل
            itemReader.open(new ExecutionContext());
        }
    }

    @BeforeStep
    public void beforeStep(StepExecution stepExecution) {
        log.info("AppUserItemReader.beforeStep {}", stepExecution.toString());
    }

    @Override
    public CustomBatchItemDto<AppUserCreateExcelDto> read() throws Exception {
            return itemReader.read();
    }

    @AfterStep
    public void afterStep(StepExecution stepExecution) {
        log.info("AppUserItemReader.afterStep {}", stepExecution.toString());
        itemReader.close();
    }
}
