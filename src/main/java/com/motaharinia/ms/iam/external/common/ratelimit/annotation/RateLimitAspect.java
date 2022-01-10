package com.motaharinia.ms.iam.external.common.ratelimit.annotation;

import com.motaharinia.ms.iam.config.caching.CachingConfiguration;
import com.motaharinia.ms.iam.config.security.oauth2.dto.LoggedInUserDto;
import com.motaharinia.ms.iam.config.security.oauth2.resource.ResourceUserTokenProvider;
import com.motaharinia.ms.iam.external.common.ratelimit.presentation.RateRequestDto;
import com.motaharinia.msutility.custom.customexception.ratelimit.RateLimitException;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * @author eng.motahari@gmail.com<br>
 * کلاس Aspect انوتیشن محدودیت بازدید متد
 */
@Aspect
@Component
public class RateLimitAspect {

    private final RedissonClient redissonClient;
    private final ResourceUserTokenProvider resourceUserTokenProvider;

    private static final String BUSINESS_EXCEPTION_PARAMETER_NOT_FOUND = "BUSINESS_EXCEPTION.PARAMETER_NOT_FOUND";

    public RateLimitAspect(RedissonClient redissonClient, ResourceUserTokenProvider resourceUserTokenProvider) {
        this.redissonClient = redissonClient;
        this.resourceUserTokenProvider = resourceUserTokenProvider;
    }

    @Before("@annotation(com.motaharinia.ms.iam.external.common.ratelimit.annotation.RateLimit)")
    public void validateRateLimit(JoinPoint joinPoint) {

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        RateLimit rateLimitAnnotation = method.getAnnotation(RateLimit.class);

        //گرفتن نام کاربری از کانتکس سکیوریتی
        Optional<LoggedInUserDto> loggedInUserDtoOptional = resourceUserTokenProvider.getLoggedInDto();
        //ست کردن نام کاربری
        String username;
        if (loggedInUserDtoOptional.isEmpty()) {
            //گرفتن نام کاربری از ورودی های متد
            username = ((RateRequestDto) getParameter(joinPoint, RateRequestDto.class)).getUsername();
        } else {
            username = loggedInUserDtoOptional.get().getUsername();
        }

        String banKey = CachingConfiguration.REDIS_EXTERNAL_PREFIX + "_RATE_LIMIT_SERVICE-" + method.getName() + "_" + username + "_BAN";
        String tryKey = CachingConfiguration.REDIS_EXTERNAL_PREFIX + "_RATE_LIMIT_SERVICE-" + method.getName() + "_" + username + "_TRY";


        // اگر کاربر قبلا محدود شده است
        if (redissonClient.getKeys().countExists(banKey) > 0)
            throw new RateLimitException();

        RBucket<Integer> tryBucket = redissonClient.getBucket(tryKey);
        // اگر برای اولین بار متد را فراخوانی کرده است
        if (redissonClient.getKeys().countExists(tryKey) == 0) {
            tryBucket.set(1, TimeUnit.MINUTES.toSeconds(rateLimitAnnotation.tryTtlInMinutes()) - 5, TimeUnit.SECONDS);
            return;
        }

        //بررسی تعداد دفعات مجاز
        int numberOfTries = tryBucket.get() + 1;
        if (numberOfTries > rateLimitAnnotation.tryCount()) {
            RBucket<Boolean> banBucket = redissonClient.getBucket(banKey);
            //در صورتی که بیش از تعداد دفعات مجاز باشد ، کاربر را محدود میکنیم
            banBucket.set(true, TimeUnit.MINUTES.toSeconds(rateLimitAnnotation.banTtlInMinutes()) - 5, TimeUnit.SECONDS);
            throw new RateLimitException();
        } else {
            //افزایش تعداد دفعات  فراخوانی متد
            tryBucket.set(numberOfTries, redissonClient.getKeys().remainTimeToLive(tryKey), TimeUnit.MILLISECONDS);
        }
    }

    /**
     * @author eng.motahari@gmail.com
     * متد دریافت پارامتر متد براساس نوع
     */
    private Object getParameter(JoinPoint joinPoint, Class parameterType) {
        Object valueParameter = null;
        if (Objects.nonNull(joinPoint) && joinPoint.getSignature() instanceof MethodSignature) {
            MethodSignature method = (MethodSignature) joinPoint.getSignature();
            Class[] parameters = method.getParameterTypes();
            for (int t = 0; t < parameters.length; t++) {
                if (parameters[t] == parameterType) {
                    Object[] obj = joinPoint.getArgs();
                    valueParameter = obj[t];
                    if (valueParameter == null)
                        continue;
                    return valueParameter;
                }
            }
        }
        throw new com.motaharinia.ms.iam.external.common.ratelimit.exception.RateLimitException(parameterType.toString(), BUSINESS_EXCEPTION_PARAMETER_NOT_FOUND, "");
    }

}
