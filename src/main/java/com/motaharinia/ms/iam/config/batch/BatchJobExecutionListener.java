package com.motaharinia.ms.iam.config.batch;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;

/**
 * کلاس لاگ کننده رویدادهای reader اکسل AppUser
 */

@Slf4j
@Component
public class BatchJobExecutionListener implements JobExecutionListener {


    @Override
    public void beforeJob(JobExecution jobExecution) {
        log.info("BatchJobExecutionListener.beforeJob : {}" , jobExecution.toString());

        //هش مپ جهت پوش کردن خطاهایی که در حین جاب اتفاق میفتد
        HashMap<String, List<String>> exceptionHashMap = new HashMap<>();
        jobExecution.getExecutionContext().put(BatchKeyConstant.EXCEPTION_LOG.getValue(), exceptionHashMap);
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        log.info("BatchJobExecutionListener.afterJob: {}" , jobExecution.getStatus());
    }
}
