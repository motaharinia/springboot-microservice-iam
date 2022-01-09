package com.motaharinia.ms.iam.external.common.ratelimit.service;

import com.motaharinia.ms.iam.external.common.ratelimit.annotation.RateLimit;
import com.motaharinia.ms.iam.external.common.ratelimit.presentation.RateRequestDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author pourya <br>
 * کلاس سرویس تست محدودیت بازدید
 */
@Slf4j
@Service
public class RateLimitServiceTest {

    @RateLimit(tryCount = 1, tryTtlInMinutes = 3, banTtlInMinutes = 1)
    public void rateLimitTest(RateRequestDto requestDto) {
    }
}
