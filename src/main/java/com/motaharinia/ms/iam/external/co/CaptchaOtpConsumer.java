package com.motaharinia.ms.iam.external.co;

import com.motaharinia.ms.iam.external.common.enumeration.SourceProjectEnum;
import com.motaharinia.msutility.custom.customdto.ClientResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient(name = "ms-captchaotp")
@RequestMapping("/api/v1.0/back")
public interface CaptchaOtpConsumer {


    @GetMapping(value = "/captcha/create/{sourceProjectEnum}/{key}/{captchaLength}/{captchaTtl}/")
    ClientResponseDto<byte[]> create(@PathVariable("sourceProjectEnum") SourceProjectEnum sourceProjectEnum, @PathVariable("key") String key, @PathVariable("captchaLength") Integer captchaLength, @PathVariable(value = "captchaTtl") Long captchaTtl);

    @GetMapping(value = "/captcha/check/{sourceProjectEnum}/{key}/{value}/{methodName}/{username}/{tryCount}/{tryTtlInMinutes}/{banTtlInMinutes}/")
    ClientResponseDto<Void> check(@PathVariable("sourceProjectEnum") SourceProjectEnum sourceProjectEnum, @PathVariable("key") String key, @PathVariable("value") String value,
                                  @PathVariable("methodName") String methodName,
                                  @PathVariable("username") String username,
                                  @PathVariable("tryCount") Integer tryCount,
                                  @PathVariable("tryTtlInMinutes") Integer tryTtlInMinutes,
                                  @PathVariable("banTtlInMinutes") Integer banTtlInMinutes);
}
