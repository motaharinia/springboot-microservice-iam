package com.motaharinia.ms.iam.modules.captcha.presentation;

import com.motaharinia.msutility.tools.string.RandomGenerationTypeEnum;
import com.motaharinia.msutility.tools.string.StringTools;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;

import java.util.Collections;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("dev")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Slf4j
class CaptchaControllerIntegrationTest {
    /**
     * پورت رندوم تست
     */
    @LocalServerPort
    private Integer PORT;
    /**
     * نشانی وب ماژول
     */
    private String MODULE_API;
    /**
     * هدر درخواستها
     */
    private HttpHeaders headers;

    /**
     * شیی فراخوان تست
     * در صورتی که پروژه ریسورس سرور است و باید توکن خود را با سرور احراز هویت چک کند
     */
    @Autowired
    private TestRestTemplate testRestTemplate;


    /**
     * این متد مقادیر پیش فرض قبل از هر تست این کلاس تست را مقداردهی اولیه میکند
     */
    @BeforeEach
    void beforeEach() {

    }


    /**
     * این متد مقادیر پیش فرض را قبل از اجرای تمامی متدهای تست این کلاس مقداردهی اولیه میکند
     */
    @BeforeAll
    void beforeAll() {
        //تنظیم زبان لوکیل پروژه روی پارسی
        Locale.setDefault(new Locale("fa", "IR"));
        //ساخت هدر درخواست
        this.headers = new HttpHeaders();
        this.headers.setContentType(MediaType.APPLICATION_JSON);
        this.headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        this.MODULE_API = "http://localhost:" + PORT + "/api/v1.0/captcha";
    }


    /**
     * متد تولیدکننده کلید و مقدار کد کپچا (به همراه تصویر) بر اساس کلید کپچا دلخواه
     */
    @Test
    @Order(1)
    void readCaptchaTest() {
        try {
            //ارسال درخواست
            String randomKey = StringTools.generateRandomString(RandomGenerationTypeEnum.NUMBER, 6, false);
            ResponseEntity<byte[]> response = this.testRestTemplate.exchange(this.MODULE_API + "/" + randomKey, HttpMethod.GET, new HttpEntity<>(this.headers), new ParameterizedTypeReference<>() {
            });
            //بررسی پاسخ
            assertThat(response).isNotNull();
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
            System.out.println("readCaptchaTest response.getBody().length:" + response.getBody().length);
        } catch (Exception exception) {
            log.error("Exception: ", exception);
            fail("Exception: {}", exception);
        }
    }


}
