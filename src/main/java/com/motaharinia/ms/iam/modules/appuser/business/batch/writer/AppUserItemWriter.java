package com.motaharinia.ms.iam.modules.appuser.business.batch.writer;

import com.motaharinia.ms.iam.config.batch.BatchKeyConstant;
import com.motaharinia.ms.iam.modules.appuser.business.service.AppUserService;
import com.motaharinia.ms.iam.modules.appuser.business.service.AppUserServiceImpl;
import com.motaharinia.ms.iam.modules.appuser.presentation.dto.create.AppUserCreateRequestDto;
import com.motaharinia.ms.iam.modules.appuser.presentation.dto.create.CustomBatchItemDto;
import com.motaharinia.msutility.tools.string.StringTools;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * @author m.azish
 */
@Component
@Slf4j
public class AppUserItemWriter implements ItemWriter<CustomBatchItemDto<AppUserCreateRequestDto>> {

    private final AppUserService appUserService;
    private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = factory.getValidator();
    /**
     * مترجم پیامها
     */
    private final MessageSource messageSource;

    private HashMap<String, List<String>> exceptionHashMap;

    public AppUserItemWriter(AppUserService appUserService, MessageSource messageSource) {
        this.appUserService = appUserService;
        this.messageSource = messageSource;
    }

    @BeforeStep
    public void beforeStep(StepExecution stepExecution) {
        log.info("AppUserItemWriter.beforeStep {}", stepExecution.toString());

        //از هش مپی که در کلاس BatchJobExecutionListener تعریف کرده ایم در اینجا استفاده میکنیم تا خطاها را اضافه کنیم
        this.exceptionHashMap = (HashMap<String, List<String>>) stepExecution.getJobExecution().getExecutionContext().get(BatchKeyConstant.EXCEPTION_LOG.getValue());
    }

    @Override
    public void write(List<? extends CustomBatchItemDto<AppUserCreateRequestDto>> appUserCreateRequestDtoList) throws Exception {

        log.info("Received the information of {} item", appUserCreateRequestDtoList.size());

        appUserCreateRequestDtoList.forEach(dto -> {

            if (!ObjectUtils.isEmpty(dto.getData())) {

                log.debug("Received the information of a item: {}", dto);

                //اعتبار سنجی
                Set<ConstraintViolation<AppUserCreateRequestDto>> violationSet = validator.validate(dto.getData());
                if (ObjectUtils.isEmpty(violationSet)) {
                    try {
                        //ثبت در دیتابیس
                        appUserService.create(dto.getData(), false);
                    } catch (Exception exception) {
                        log.error(" business exception: {}", exception.getMessage());
                        //پوش کردن خطا در هش مپ
                        this.exceptionHashMap.put(dto.getRowNumber(), List.of(StringTools.translateCustomMessage(messageSource, exception.getMessage())));
                    }
                } else {
                    //پوش کردن خطا در هش مپ
                    this.exceptionHashMap.put(dto.getRowNumber(), new ArrayList<>());
                    violationSet.forEach(item -> {
                        log.error(" validation exception: {}", item.toString());
                        this.exceptionHashMap.get(dto.getRowNumber()).add(StringTools.translateCustomMessage(messageSource, item.getMessage()));
                    });
                }
            } else {
                //اگر قبلا در پردازش اطلاعات خطایی وجود داشته خطا را در مپ خطاها ثبت میکنیم که سطر بعدی هم نوشته شود
                log.error(" create batch rowNumber:{} exception: ", dto.getRowNumber(), dto.getException());
                this.exceptionHashMap.put(dto.getRowNumber(), List.of(StringTools.translateCustomMessage(messageSource, AppUserServiceImpl.BUSINESS_EXCEPTION_APP_USER_HAS_ERROR_IN_CREATE_BATCH) + " : " + dto.getException().getMessage()));
            }
        });

    }
}

