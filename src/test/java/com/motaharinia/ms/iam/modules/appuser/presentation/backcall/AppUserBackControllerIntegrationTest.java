package com.motaharinia.ms.iam.modules.appuser.presentation.backcall;

import com.motaharinia.ms.iam.modules.appuser.presentation.dto.AppUserAnnualPointDto;
import com.motaharinia.ms.iam.modules.appuser.presentation.dto.AppUserReadResponseDto;
import com.motaharinia.ms.iam.modules.appuser.presentation.dto.AppUserTotalCountResponseDto;
import com.motaharinia.ms.iam.modules.appuser.presentation.dto.AppUserValidReadDto;
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

import java.util.Collections;
import java.util.Locale;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("dev")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Slf4j
class AppUserBackControllerIntegrationTest {
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
        this.MODULE_API = "http://localhost:" + PORT + "/api/v1.0/back/app-user";
    }

    private HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        return headers;
    }


    /**
     * جستجو کاربر برنامه فرانت
     */
    @Test
    @Order(1)
    void readByIdTest() {
        try {
            //ارسال درخواست
            long testId = 1;
            ResponseEntity<ClientResponseDto<AppUserReadResponseDto>> response = this.testRestTemplate.exchange(this.MODULE_API + "/" + testId, HttpMethod.GET, new HttpEntity<>(getHeaders()), new ParameterizedTypeReference<>() {
            });
            //بررسی پاسخ
            assertThat(response).isNotNull();
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
            //بررسی های تست
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getData()).isNotNull();
            AppUserReadResponseDto responseDto = response.getBody().getData();
            assertThat(responseDto).isNotNull();
            System.out.println(responseDto.toString());
        } catch (Exception exception) {
            log.error("Exception:", exception);
            fail("Exception: {}", exception);
        }
    }


    /**
     * متد گرفتن اشخاصی که در روز و ماه جاری ثبت نام کرده اند
     */
    @Test
    @Order(2)
    void getAllUsersCountTest() {
        try {
            //ارسال درخواست
            ResponseEntity<ClientResponseDto<AppUserTotalCountResponseDto>> response = this.testRestTemplate.exchange(this.MODULE_API + "/total-count", HttpMethod.GET, new HttpEntity<>(getHeaders()), new ParameterizedTypeReference<>() {
            });
            //بررسی پاسخ
            assertThat(response).isNotNull();
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
            //بررسی های تست
            assertThat(response.getBody().getData()).isNotNull();
        } catch (Exception exception) {
            log.error("Exception:", exception);
            fail("Exception: {}", exception);
        }
    }


    /**
     * جستجو کاربر برنامه فرانت
     */
    @Test
    @Order(3)
    void readByIdsTest() {
        try {
            //ارسال درخواست
            ResponseEntity<ClientResponseDto<Set<AppUserValidReadDto>>> response = this.testRestTemplate.exchange(this.MODULE_API + "/read-by-ids", HttpMethod.POST, new HttpEntity<>(Set.of(1, 2, 3), getHeaders()), new ParameterizedTypeReference<>() {
            });
            //بررسی پاسخ
            assertThat(response).isNotNull();
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
            //بررسی های تست
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getData()).isNotNull();
            Set<AppUserValidReadDto> responseDto = response.getBody().getData();
            System.out.println(responseDto.toString());
        } catch (Exception exception) {
            log.error("Exception:", exception);
            fail("Exception: {}", exception);
        }
    }


    /**
     * متد گرفتن اشخاصی که در روز و ماه جاری تولدشان هست
     */
    @Test
    @Order(4)
    void readAllByDateOfBirthTest() {
        try {
            //ارسال درخواست
            ResponseEntity<ClientResponseDto<Set<AppUserAnnualPointDto>>> response = this.testRestTemplate.exchange(this.MODULE_API + "/read-all-by-date-of-birth", HttpMethod.GET, new HttpEntity<>(getHeaders()), new ParameterizedTypeReference<>() {
            });
            //بررسی پاسخ
            assertThat(response).isNotNull();
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
            //بررسی های تست
            assertThat(response.getBody().getData()).isNotNull();
        } catch (Exception exception) {
            log.error("Exception:", exception);
            fail("Exception: {}", exception);
        }
    }

    /**
     * متد گرفتن اشخاصی که در روز و ماه جاری ثبت نام کرده اند
     */
    @Test
    @Order(5)
    void readAllByDateOfSignUpTest() {
        try {
            //ارسال درخواست
            ResponseEntity<ClientResponseDto<Set<AppUserAnnualPointDto>>> response = this.testRestTemplate.exchange(this.MODULE_API + "/read-all-by-date-of-signup", HttpMethod.GET, new HttpEntity<>(getHeaders()), new ParameterizedTypeReference<>() {
            });
            //بررسی پاسخ
            assertThat(response).isNotNull();
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
            //بررسی های تست
            assertThat(response.getBody().getData()).isNotNull();
        } catch (Exception exception) {
            log.error("Exception:", exception);
            fail("Exception: {}", exception);
        }
    }

    /**
     * جستجو کاربر برنامه فرانت با شناسه ملی
     */
    @Test
    @Order(6)
    void readByNationalCodeTest() {
        try {
            //ارسال درخواست
            String testNationalCode = "0083419004";
            ResponseEntity<ClientResponseDto<AppUserValidReadDto>> response = this.testRestTemplate.exchange(this.MODULE_API + "/read-by-national-code/" + testNationalCode, HttpMethod.GET, new HttpEntity<>(getHeaders()), new ParameterizedTypeReference<>() {
            });
            //بررسی پاسخ
            assertThat(response).isNotNull();
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
            //بررسی های تست
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getData()).isNotNull();
            AppUserValidReadDto responseDto = response.getBody().getData();
            assertThat(responseDto).isNotNull();
            System.out.println(responseDto.toString());
        } catch (Exception exception) {
            log.error("Exception:", exception);
            fail("Exception: {}", exception);
        }
    }

    /**
     * جستجو کاربر برنامه فرانت با شماره موبایل
     */
    @Test
    @Order(6)
    void readByMobileNoTest() {
        try {
            //ارسال درخواست
            ResponseEntity<ClientResponseDto<Set<AppUserValidReadDto>>> response = this.testRestTemplate.exchange(this.MODULE_API + "/read-by-mobile-nos", HttpMethod.POST, new HttpEntity<>(Set.of("09354161222"),getHeaders()), new ParameterizedTypeReference<>() {
            });
            //بررسی پاسخ
            assertThat(response).isNotNull();
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
            //بررسی های تست
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getData()).isNotNull();
            Set<AppUserValidReadDto> responseDto = response.getBody().getData();
            assertThat(responseDto).isNotNull();
            System.out.println(responseDto.toString());
        } catch (Exception exception) {
            log.error("Exception:", exception);
            fail("Exception: {}", exception);
        }
    }
}
