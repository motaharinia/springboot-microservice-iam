package com.motaharinia.ms.iam.external.common.ratelimit.annotation;

import com.motaharinia.ms.iam.external.common.ratelimit.presentation.RateRequestDto;
import com.motaharinia.ms.iam.external.common.ratelimit.service.RateLimitServiceTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * @author pourya
 * @date 2021-09-11
 * کلاس تست محدودیت بازدید
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("dev")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Slf4j
class ServiceServiceRateLimitAspectTest {

    @Autowired
    RateLimitServiceTest rateLimitServiceTest;

    @Test
    void rateLimit() {
        RateRequestDto rateRequestDto = new RateRequestDto();
        rateRequestDto.setUsername("test");

        rateLimitServiceTest.rateLimitTest(rateRequestDto);

    }
}