package com.motaharinia.ms.iam.external.common.state;


import com.motaharinia.ms.iam.config.caching.CachingConfiguration;
import com.motaharinia.ms.iam.external.common.state.enumeration.StateProcess;
import com.motaharinia.ms.iam.external.common.state.exception.StateException;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.concurrent.TimeUnit;

/**
 * @author eng.motahari@gmail.com<br>
 * کلاس پیاده سازی سرویس مدیریت گامهای وضعیت یک فرآیند
 */
@Service
public class StateTools {

    private final RedissonClient redissonClient;

    private static final String BUSINESS_EXCEPTION_EXTERNAL_STATE_MANAGER_STEP_TIMEOUT = "BUSINESS_EXCEPTION.EXTERNAL_STATE_MANAGER_STEP_TIMEOUT";
    private static final String BUSINESS_EXCEPTION_EXTERNAL_STATE_MANAGER_STEP_JUMP = "BUSINESS_EXCEPTION_EXTERNAL_STATE_MANAGER_STEP_JUMP";

    public StateTools(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    /**
     * متد بررسی جلو بردن گام یک فرآیند
     *
     * @param stateProcess مقدار ثابت فرآیند مورد نظر
     * @param key          کلید یکتای دیتا
     * @param step         گام دیتا
     */
    public void stepForwardCheck(@NotNull StateProcess stateProcess, @NotNull String key, @NotNull Integer step) {
        String bucketKey = CachingConfiguration.REDIS_EXTERNAL_PREFIX + "_STATE_MANAGEMENT_SERVICE-" + stateProcess + "_" + key;
        RBucket<Integer> readRBucket = redissonClient.getBucket(bucketKey);
        if (step != 1) {
            if (redissonClient.getKeys().count() == 0) {
                //در صورتی که در ردیس رکورد موردنظر را پیدا نکند به استپ اول برگشت داده میشود
                throw new StateException(key, BUSINESS_EXCEPTION_EXTERNAL_STATE_MANAGER_STEP_TIMEOUT,  key);
            }

            Integer lastStep = readRBucket.get();

            if (step != lastStep + 1) {
                //در صورتی که مراحل به ترتیب انجام نشده باشد به استپ اول برگشت داده میشود
                throw new StateException(key, BUSINESS_EXCEPTION_EXTERNAL_STATE_MANAGER_STEP_JUMP,  key);
            }
        }
    }

    /**
     * متد بررسی و جلو بردن گام یک فرآیند
     *
     * @param stateProcess مقدار ثابت فرآیند مورد نظر
     * @param key          کلید یکتای دیتا
     * @param step         گام دیتا
     */
    public void stepForward(@NotNull StateProcess stateProcess, @NotNull String key, @NotNull Integer step) {
        String bucketKey = CachingConfiguration.REDIS_EXTERNAL_PREFIX + "_STATE_MANAGEMENT_SERVICE-" + stateProcess + "_" + key;
        RBucket<Integer> readRBucket = redissonClient.getBucket(bucketKey);
        if (step != 1) {
            if (redissonClient.getKeys().count() == 0) {
                //در صورتی که در ردیس رکورد موردنظر را پیدا نکند به استپ اول برگشت داده میشود
                throw new StateException(key, BUSINESS_EXCEPTION_EXTERNAL_STATE_MANAGER_STEP_TIMEOUT, "username:" + key);
            }

            Integer lastStep = readRBucket.get();

            if (step != lastStep + 1) {
                //در صورتی که مراحل به ترتیب انجام نشده باشد به استپ اول برگشت داده میشود
                throw new StateException(key, BUSINESS_EXCEPTION_EXTERNAL_STATE_MANAGER_STEP_JUMP, "username:" + key);
            }
        }
        readRBucket.set(step, TimeUnit.MINUTES.toSeconds(stateProcess.getValue()), TimeUnit.SECONDS);
    }
}
