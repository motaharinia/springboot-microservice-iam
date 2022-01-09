package com.motaharinia.ms.iam.modules.securityuser.presentation;

import com.motaharinia.ms.iam.config.security.oauth2.dto.AppUserDto;
import com.motaharinia.ms.iam.config.security.oauth2.dto.BearerTokenDto;
import com.motaharinia.ms.iam.config.security.oauth2.resource.ResourceUserTokenProvider;
import com.motaharinia.ms.iam.modules.appuser.business.service.AppUserService;
import com.motaharinia.ms.iam.modules.securityuser.business.service.SecurityUserService;
import com.motaharinia.ms.iam.modules.securityuser.presentation.securityuser.SecurityUserReadDto;
import com.motaharinia.ms.iam.modules.securityuser.presentation.securityusertoken.SecurityUserTokenDto;
import com.motaharinia.msjpautility.page.CustomPageResponseDto;
import com.motaharinia.msutility.custom.customdto.ClientResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.ObjectUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("dev")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Slf4j
class SecurityUserTokenControllerIntegrationTest {
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
     * شیی فراخوان تست
     * در صورتی که پروژه ریسورس سرور است و باید توکن خود را با سرور احراز هویت چک کند
     */
    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private SecurityUserService securityUserService;
    @Autowired
    private AppUserService appUserService;
    @Autowired
    private ResourceUserTokenProvider resourceUserTokenProvider;

    private static final String tokenUsername = "0083419004";
    //private static final String tokenUsername = "0063234114";

    /**
     * رقرش توکن
     */
    String refreshToken = "1641306053550_c56b0917-4f13-4d4f-a584-82908da4626e";

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
        //مسیر پیش فرض ماژول
        this.MODULE_API = "http://localhost:" + PORT + "/api/v1.0/security-user-token";
    }

    private HttpHeaders getHeaders(String tokenUsername) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        if (!ObjectUtils.isEmpty(tokenUsername)) {
            SecurityUserReadDto securityUserReadDto = securityUserService.serviceReadByUsername(tokenUsername,true);
            AppUserDto appUserDto = appUserService.serviceReadById(securityUserReadDto.getAppUserId());
            BearerTokenDto bearerTokenDto = securityUserService.createBearerToken(securityUserReadDto.getSecurityUserId(),false,  appUserDto, new HashMap<>());
            headers.set("Authorization", "Bearer " + bearerTokenDto.getAccessToken());
//
//            Authentication authentication = resourceTokenProvider.getAuthentication(bearerTokenDto.getAccessToken());
//            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        return headers;
    }

    //-------------------------------------------------------
    //CRUD
    //-------------------------------------------------------

    /**
     *مشاهده توکن امنیت
     */
    @Test
    @Order(1)
    void readAllTest() {
        try {

            //ارسال درخواست
//            ResponseEntity<ClientResponseDto<CustomPageResponseDto<SecurityTokenDto>>> response = this.testRestTemplate.exchange(this.MODULE_API + "/read-all?page=0&size=20&fromDate=1628951456137&toDate=1628951456137", HttpMethod.GET, new HttpEntity<>(null, getHeaders(tokenUsername)), new ParameterizedTypeReference<>() {
//            });
            //ارسال درخواست
            ResponseEntity<ClientResponseDto<CustomPageResponseDto<SecurityUserTokenDto>>> response = this.testRestTemplate.exchange(this.MODULE_API + "/read-all", HttpMethod.GET, new HttpEntity<>(null, getHeaders(tokenUsername)), new ParameterizedTypeReference<>() {
            });
            //بررسی پاسخ
            assertThat(response).isNotNull();
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
            //بررسی های تست
            assertThat(response.getBody()).isNotNull();
            //بررسی های تست
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getData()).isNotNull();
            assertThat(response.getBody().getData().getContent().get(1)).isNotNull();

            refreshToken = response.getBody().getData().getContent().get(1).getRefreshToken();

        } catch (Exception exception) {
            log.error("Exception:{}", exception);
            fail("Exception: {}", exception);
        }
    }

    /**
     *رفرش توکن امنیت
     */
    @Test
    @Order(2)
    void renewTokenTest() {
        try {
            //ارسال درخواست
            ResponseEntity<ClientResponseDto<BearerTokenDto>> response = this.testRestTemplate.exchange(this.MODULE_API + "/renew-token/"+refreshToken, HttpMethod.PUT, new HttpEntity<>(null, getHeaders(null)), new ParameterizedTypeReference<>() {
            });
            //بررسی پاسخ
            assertThat(response).isNotNull();
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
            //بررسی های تست
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getData().getRefreshToken().equals(refreshToken));
        } catch (Exception exception) {
            log.error("Exception:{}", exception);
            fail("Exception: {}", exception);
        }
    }

    /**
     * مشاهده سشن های فعال کاربر لاگین شده
     */
    @Test
    @Order(3)
    void readAllActiveSessionByCurrentUserTest() {
        try {
            //ارسال درخواست
            ResponseEntity<ClientResponseDto<CustomPageResponseDto<SecurityUserTokenDto>>> response = this.testRestTemplate.exchange(this.MODULE_API + "/read-all-active-session-by-current-user", HttpMethod.GET, new HttpEntity<>(null, getHeaders(tokenUsername)), new ParameterizedTypeReference<>() {
            });
            //بررسی پاسخ
            assertThat(response).isNotNull();
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
            //بررسی های تست
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getData()).isNotNull();
        } catch (Exception exception) {
            log.error("Exception:{}", exception);
            fail("Exception: {}", exception);
        }
    }

    /**
     * متد kill کردن رفرش توکن
     */
    @Test
    @Order(4)
    void terminateTest() {
        try {
            String refreshToken = "636832e2-08b3-44e4-8a80-df04819c989a";
            //ارسال درخواست
            ResponseEntity<ClientResponseDto<Boolean>> response = this.testRestTemplate.exchange(this.MODULE_API + "/terminate/"+refreshToken, HttpMethod.PUT, new HttpEntity<>(null, getHeaders(tokenUsername)), new ParameterizedTypeReference<>() {
            });
            //بررسی پاسخ
            assertThat(response).isNotNull();
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
            //بررسی های تست
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getData().equals(true));
        } catch (Exception exception) {
            log.error("Exception:{}", exception);
            fail("Exception: {}", exception);
        }
    }

    /**
     * متد kill کردن رفرش توکن
     */
    @Test
    @Order(5)
    void logoutTest() {
        try {
            //ارسال درخواست
            ResponseEntity<ClientResponseDto<Boolean>> response = this.testRestTemplate.exchange(this.MODULE_API + "/logout", HttpMethod.PUT, new HttpEntity<>(null, getHeaders(tokenUsername)), new ParameterizedTypeReference<>() {
            });
            //بررسی پاسخ
            assertThat(response).isNotNull();
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
            //بررسی های تست
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getData().equals(true));
        } catch (Exception exception) {
            log.error("Exception:{}", exception);
            fail("Exception: {}", exception);
        }
    }

}
