package com.motaharinia.ms.iam.external.co;

import com.motaharinia.msutility.custom.customdto.ClientResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name="ms-captchaotp")
public interface CaptchaOtpConsumer {
    @GetMapping("/api/v1.0/back/captcha/test")
    ClientResponseDto<String> test(@RequestHeader(value = "Authorization", required = true) String authorizationHeader);
}
