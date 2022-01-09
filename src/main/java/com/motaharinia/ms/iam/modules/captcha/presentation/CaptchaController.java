package com.motaharinia.ms.iam.modules.captcha.presentation;

import com.motaharinia.ms.iam.external.captchaotp.business.service.CaptchaOtpExternalService;
import com.motaharinia.ms.iam.external.common.enumeration.SourceProjectEnum;
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author eng.motahari@gmail.com<br>
 * کلاس کنترلر کپچا
 */
@GraphQLApi
@RestController
@RequestMapping("/api/v1.0/captcha")
@Slf4j
public class CaptchaController {

    @Value("${app.ms-captcha-otp.captcha-length}")
    private Integer captchaLength;

    @Value("${app.ms-captcha-otp.captcha-ttl-seconds}")
    private Long captchaTtl;

    private final CaptchaOtpExternalService captchaOtpExternalService;

    public CaptchaController(CaptchaOtpExternalService captchaOtpExternalService) {
        this.captchaOtpExternalService = captchaOtpExternalService;
    }

    /**
     * متد تولیدکننده کلید و مقدار کد کپچا (به همراه تصویر) بر اساس کلید کپچا دلخواه
     * @param key               کلید کد کپچا
     * @param response  خروجی: تصویر کپچا
     * @throws IOException خطا
     */
    @GetMapping("/{key}")
    public void readCaptcha(@PathVariable String key, HttpServletResponse response) throws IOException {
        //خواندن تصویر کپچا از سرویس کپچا
        byte[] captchaByteArray= captchaOtpExternalService.captchaCreate(SourceProjectEnum.MS_IAM,key, captchaLength,captchaTtl);

        //تنظیمات هدر برای ارسال به کلاینت
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
        response.setDateHeader("Max-Age", 0);
        response.setContentType("image/jpg");
        response.setContentLength(captchaByteArray.length);

        //اگر درخواست شده پنجره دانلود برای کلاینت باز شود
        response.setHeader("Content-Disposition", "inline; filename=\"captcha.jpg\"");
        //نوشتن داده فایل بر روی شیی پاسخ به کلاینت
        OutputStream outputStream = response.getOutputStream();
        FileCopyUtils.copy(captchaByteArray, outputStream);
        outputStream.close();
        outputStream.flush();
    }

}
