package com.motaharinia.ms.iam.modules.dev.presentation.backcall;

import com.motaharinia.ms.iam.modules.securityuser.business.service.SecurityRoleService;
import com.motaharinia.msutility.custom.customdto.ClientResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
class DevControllerIntegrationTest {
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
     * عنوان هدر کلید کپچا
     */
    @Value("${app.ms-captcha-otp.header-key}")
    private String headerCaptchaKey;
    /**
     * عنوان هدر مقدار کپچا
     */
    @Value("${app.ms-captcha-otp.header-value}")
    private String headerCaptchaValue;
    /**
     * شیی فراخوان تست
     * در صورتی که پروژه ریسورس سرور است و باید توکن خود را با سرور احراز هویت چک کند
     */
    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private SecurityRoleService securityRoleService;


    //final String frontUsername = "10103800056";
    private static final String frontUsername = "0063234114";
    private static final String backUsername = "0083419004";
    private static String accessToken = "";


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
        this.headers.set(headerCaptchaKey, "123456");
        this.headers.set(headerCaptchaValue, "a3df23");
        this.MODULE_API = "http://localhost:" + PORT + "/api/v1.0/back/dev";
        //در صورتی که پروژه ریسورس سرور است و باید توکن خود را با سرور احراز هویت چک کند
//        this.testRestTemplate = authorizationClient.getTemplate();
//        log.info("accessToken:" + testRestTemplate.getAccessToken());
    }


    /**
     * متد تولید توکن احراز هویت برای فرانت
     */
    @Test
    @Order(1)
    void frontCreateBearerTokenTest() {
        try {
            //ارسال درخواست
            ResponseEntity<ClientResponseDto<String>> response = this.testRestTemplate.exchange(this.MODULE_API + "/app-user/token/" + frontUsername, HttpMethod.GET, new HttpEntity<>(this.headers), new ParameterizedTypeReference<>() {
            });
            //بررسی پاسخ
            assertThat(response).isNotNull();
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
            //بررسی های تست
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getData()).isNotNull();
            accessToken = response.getBody().getData();
        } catch (Exception exception) {
            log.error("Exception: ", exception);
            fail("Exception: {}", exception);
        }
    }

    /**
     * متد تولید توکن احراز هویت برای فرانت
     */
    @Test
    @Order(2)
    void backCreateBearerTokenTest() {
        try {
            //ارسال درخواست
            ResponseEntity<ClientResponseDto<String>> response = this.testRestTemplate.exchange(this.MODULE_API + "/back-user/token/" + backUsername, HttpMethod.GET, new HttpEntity<>(this.headers), new ParameterizedTypeReference<>() {
            });
            //بررسی پاسخ
            assertThat(response).isNotNull();
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
            //بررسی های تست
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getData()).isNotNull();
            accessToken = response.getBody().getData();
            System.out.println("backAccessToken:" + accessToken);
        } catch (Exception exception) {
            log.error("Exception: ", exception);
            fail("Exception: {}", exception);
        }
    }

}
