package com.motaharinia.ms.iam.modules.securityclient.presentation;

import com.motaharinia.ms.iam.config.security.oauth2.dto.BearerTokenDto;
import com.motaharinia.ms.iam.modules.securityclient.business.enumeration.GrantTypeEnum;
import com.motaharinia.ms.iam.modules.securityclient.business.service.SecurityClientService;
import com.motaharinia.ms.iam.modules.securityclient.presentation.securityclienttoken.SecurityClientTokenDto;
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
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("dev")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Slf4j
class SecurityClientTokenControllerIntegrationTest {
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
    private SecurityClientService securityClientService;

    private static final String tokenUsername = "pourya123";
    //private static final String tokenUsername = "0063234114";

    /**
     * رفرش توکن
     */
    private String refreshToken = "1641302227157_fdbe5832-4e87-4b0d-ae47-3e81a353e928";

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
        this.MODULE_API = "http://localhost:" + PORT + "/api/v1.0/security-client-token";
    }

    private HttpHeaders getHeaders(String tokenUsername) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        if (!ObjectUtils.isEmpty(tokenUsername)) {
            BearerTokenDto bearerTokenDto = securityClientService.createBearerToken(tokenUsername, "12345", GrantTypeEnum.CLIENT_CREDENTIAL.getValue(), "all");
            headers.set("X-Authorization", "Bearer " + bearerTokenDto.getAccessToken());
        }
        return headers;
    }

    //-------------------------------------------------------
    //CRUD
    //-------------------------------------------------------

    /**
     * مشاهده توکن امنیت
     */
    @Test
    @Order(1)
    void readAllTest() {
        try {

            //ارسال درخواست
//            ResponseEntity<ClientResponseDto<CustomPageResponseDto<SecurityTokenDto>>> response = this.testRestTemplate.exchange(this.MODULE_API + "/read-all?page=0&size=20&fromDate=1628951456137&toDate=1628951456137", HttpMethod.GET, new HttpEntity<>(null, getHeaders(tokenUsername)), new ParameterizedTypeReference<>() {
//            });
            //ارسال درخواست
            ResponseEntity<ClientResponseDto<CustomPageResponseDto<SecurityClientTokenDto>>> response = this.testRestTemplate.exchange(this.MODULE_API + "/read-all", HttpMethod.GET, new HttpEntity<>(null, getHeaders(tokenUsername)), new ParameterizedTypeReference<>() {
            });
            //بررسی پاسخ
            assertThat(response).isNotNull();
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
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
     * رفرش توکن امنیت
     */
    @Test
    @Order(2)
    void renewTokenTest() {
        try {
            //ارسال درخواست
            ResponseEntity<ClientResponseDto<BearerTokenDto>> response = this.testRestTemplate.exchange(this.MODULE_API + "/renew-token/" + refreshToken, HttpMethod.PUT, new HttpEntity<>(null, getHeaders(null)), new ParameterizedTypeReference<>() {
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


}
