package com.motaharinia.ms.iam.config.batch;

import com.motaharinia.ms.iam.modules.appuser.business.batch.processor.AppUserItemProcessor;
import com.motaharinia.ms.iam.modules.appuser.business.batch.reader.AppUserItemReader;
import com.motaharinia.ms.iam.modules.appuser.business.batch.writer.AppUserItemWriter;
import com.motaharinia.ms.iam.modules.appuser.presentation.dto.create.AppUserCreateExcelDto;
import com.motaharinia.ms.iam.modules.appuser.presentation.dto.create.AppUserCreateRequestDto;
import com.motaharinia.ms.iam.modules.appuser.presentation.dto.create.CustomBatchItemDto;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.configuration.annotation.DefaultBatchConfigurer;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.step.tasklet.TaskletStep;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * @author m.azish
 */
@Configuration
@EnableBatchProcessing
public class BatchConfiguration extends DefaultBatchConfigurer {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final AppUserItemReader appUserItemReader;
    private final AppUserItemProcessor appUserItemProcessor;
    private final AppUserItemWriter appUserItemWriter;
    private final BatchJobExecutionListener batchJobExecutionListener;


    public BatchConfiguration(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory, AppUserItemReader appUserItemReader, AppUserItemProcessor appUserItemProcessor, AppUserItemWriter appUserItemWriter, BatchJobExecutionListener batchJobExecutionListener) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
        this.appUserItemReader = appUserItemReader;
        this.appUserItemProcessor = appUserItemProcessor;
        this.appUserItemWriter = appUserItemWriter;
        this.batchJobExecutionListener = batchJobExecutionListener;
    }

    @Override
    public void setDataSource(DataSource dataSource) {
        //This BatchConfigurer ignores any DataSource
    }

    @Bean(name = "appUserCreateJob")
    public Job appUserJob() {
        TaskletStep step = stepBuilderFactory.get("stepBuilder1")
                .<CustomBatchItemDto<AppUserCreateExcelDto>, CustomBatchItemDto<AppUserCreateRequestDto>>chunk(1)
                .reader(appUserItemReader)
                .processor(appUserItemProcessor)
                .writer(appUserItemWriter)
                .build();

        return jobBuilderFactory.get("jobBuilder1")
                .listener(batchJobExecutionListener)
                .flow(step)
                .end()
                .build();
    }

}
