package com.motaharinia.ms.iam.modules.backuser.presentation;

import com.motaharinia.ms.iam.config.security.oauth2.dto.BackUserDto;
import com.motaharinia.ms.iam.config.security.oauth2.dto.BearerTokenDto;
import com.motaharinia.ms.iam.config.security.oauth2.enumeration.GenderEnum;
import com.motaharinia.ms.iam.modules.appuser.presentation.dto.AppUserResponseDto;
import com.motaharinia.ms.iam.modules.backuser.business.BackUserService;
import com.motaharinia.ms.iam.modules.backuser.business.mapper.BackUserMapper;
import com.motaharinia.ms.iam.modules.backuser.presentation.dto.BackUserCreateRequestDto;
import com.motaharinia.ms.iam.modules.backuser.presentation.dto.BackUserResponseDto;
import com.motaharinia.ms.iam.modules.backuser.presentation.dto.BackUserUpdateRequestDto;
import com.motaharinia.ms.iam.modules.backuser.presentation.signin.SigninCheckCredentialRequestDto;
import com.motaharinia.ms.iam.modules.backuser.presentation.signin.SigninCheckCredentialResponseDto;
import com.motaharinia.ms.iam.modules.backuser.presentation.signin.SigninCheckOtpRequestDto;
import com.motaharinia.ms.iam.modules.securityuser.business.service.SecurityUserService;
import com.motaharinia.ms.iam.modules.securityuser.persistence.orm.SecurityUser;
import com.motaharinia.msjpautility.page.CustomPageResponseDto;
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
import org.springframework.util.ObjectUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("dev")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Slf4j
class BackUserControllerIntegrationTest {
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
    private SecurityUserService securityUserService;
    @Autowired
    private BackUserService backUserService;
    @Autowired
    private BackUserMapper backUserMapper;

    //برای ثبت backUser استفاده میشود
    //private static final String nationalCode = "10103800056";
    //private static final String nationalCode = "0083419004";
    //private static final String nationalCode = "0063234114";
    private static final String username = "0078011388";
    private static final String nationalCode = "0110543726";
    private static final String password = "asd123ASD!@#";
    private static final String newPassword = "asd123ASD!@#$";
    private static final String tokenUsername = "0083419004";

    private static String testOtp = "";
    private Long id = 1l;

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
        this.MODULE_API = "http://localhost:" + PORT + "/api/v1.0/back-user";
    }

    private HttpHeaders getHeaders(String tokenUsername) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        //headers.set(headerCaptchaKey, "123456");
        //headers.set(headerCaptchaValue, "a3df23");
        if (!ObjectUtils.isEmpty(tokenUsername)) {
            SecurityUser securityUser = securityUserService.serviceReadByUsernameForBack(tokenUsername);
            BackUserDto backUserDto = backUserService.serviceReadById(securityUser.getBackUserId());
            BearerTokenDto bearerTokenDto = securityUserService.createBearerToken(securityUser.getId(), false, backUserDto, new HashMap<>());
            headers.set("Authorization", "Bearer " + bearerTokenDto.getAccessToken());
        }
        return headers;
    }


    //-------------------------------------------------------
    //sign in
    //-------------------------------------------------------

    /**
     * متد گام اول احراز هویت(بررسی کلمه کاربری و رمز عبور)
     */
    @Test
    @Order(3)
    void signinCheckCredentialTest() {
        try {
            //ایجاد مدل درخواست
            SigninCheckCredentialRequestDto dto = new SigninCheckCredentialRequestDto(username, password);

            //ارسال درخواست
            ResponseEntity<ClientResponseDto<SigninCheckCredentialResponseDto>> response = this.testRestTemplate.exchange(this.MODULE_API + "/signin-check-credential", HttpMethod.POST, new HttpEntity<>(dto, getHeaders("")), new ParameterizedTypeReference<>() {
            });
            //بررسی پاسخ
            assertThat(response).isNotNull();
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
            //بررسی های تست
            assertThat(response.getBody().getData()).isNotNull();
            testOtp = response.getBody().getData().getTestOtp();
            System.out.println("testOtp:" + testOtp);
        } catch (Exception exception) {
            log.error("Exception {}", exception.toString());
            fail("Exception {}", exception.toString());
        }
    }

    /**
     * متد گام دوم احراز هویت(بررسی کد تایید)
     */
    @Test
    @Order(4)
    void signinCheckOtpTest() {
        try {
            //ایجاد مدل درخواست
            SigninCheckOtpRequestDto dto = new SigninCheckOtpRequestDto(username, password, testOtp, false);

            //ارسال درخواست
            ResponseEntity<ClientResponseDto<BearerTokenDto>> response = this.testRestTemplate.exchange(this.MODULE_API + "/signin-check-otp", HttpMethod.POST, new HttpEntity<>(dto, getHeaders("")), new ParameterizedTypeReference<>() {
            });
            //بررسی پاسخ
            assertThat(response).isNotNull();
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
            //بررسی های تست
            assertThat(response.getBody().getData()).isNotNull();
            assertThat(response.getBody().getData().getAccessToken()).isNotNull();
        } catch (Exception exception) {
            log.error("Exception {}", exception.toString());
            fail("Exception {}", exception.toString());
        }
    }
    //-------------------------------------------------------
    //change password
    //-------------------------------------------------------
//
//    /**
//     * متد تغییر رمز عبور
//     */
//    @Test
//    @Order(5)
//    void changePasswordTest() {
//        try {
//            //ایجاد مدل درخواست
//            ChangePasswordRequestDto dto = new ChangePasswordRequestDto(password, newPassword, newPassword);
//            //ارسال درخواست
//            ResponseEntity<ClientResponseDto<ChangePasswordResponseDto>> response = this.testRestTemplate.exchange(this.MODULE_API + "/change-password", HttpMethod.PUT, new HttpEntity<>(dto, getHeaders(tokenUsername)), new ParameterizedTypeReference<>() {
//            });
//            //بررسی پاسخ
//            assertThat(response).isNotNull();
//            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//            assertThat(response.getBody()).isNotNull();
//            //بررسی های تست
//            assertThat(response.getBody().getData()).isNotNull();
//            assertThat(response.getBody().getData().getUsername()).isNotNull();
//        } catch (Exception exception) {
//            log.error("Exception {}", exception.toString());
//            fail("Exception {}", exception.toString());
//        }
//    }
//
//    /**
//     * متد گام اول فراموشی رمز عبور (ارسال کد تایید)
//     */
//    @Test
//    @Order(6)
//    void forgetPasswordCheckUsernameTest() {
//        try {
//            //ارسال درخواست
//            ResponseEntity<ClientResponseDto<ForgetPasswordCheckUsernameResponseDto>> response = this.testRestTemplate.exchange(this.MODULE_API + "/forget-password-check-username/" + username, HttpMethod.PUT, new HttpEntity<>(getHeaders("")), new ParameterizedTypeReference<>() {
//            });
//            //بررسی پاسخ
//            assertThat(response).isNotNull();
//            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//            assertThat(response.getBody()).isNotNull();
//            //بررسی های تست
//            assertThat(response.getBody().getData()).isNotNull();
//            assertThat(response.getBody().getData().getTestOtp()).isNotNull();
//            testOtp = response.getBody().getData().getTestOtp();
//            System.out.println("testOtp:" + testOtp);
//        } catch (Exception exception) {
//            log.error("Exception {}", exception.toString());
//            fail("Exception {}", exception.toString());
//        }
//    }
//
//    /**
//     * متد گام دوم فراموشی رمز عبور (ریست کردن رمز عبور یا کد تایید)
//     */
//    @Test
//    @Order(7)
//    void forgetPasswordCheckOtpTest() {
//        try {
//            //ایجاد مدل درخواست
//            ForgetPasswordCheckOtpRequestDto dto = new ForgetPasswordCheckOtpRequestDto(username, newPassword, newPassword, testOtp, false);
//            //ارسال درخواست
//            ResponseEntity<ClientResponseDto<BearerTokenDto>> response = this.testRestTemplate.exchange(this.MODULE_API + "/forget-password-check-otp", HttpMethod.PUT, new HttpEntity<>(dto, getHeaders("")), new ParameterizedTypeReference<>() {
//            });
//            //بررسی پاسخ
//            assertThat(response).isNotNull();
//            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//            assertThat(response.getBody()).isNotNull();
//            //بررسی های تست
//            assertThat(response.getBody().getData()).isNotNull();
//            assertThat(response.getBody().getData().getAccessToken()).isNotNull();
//        } catch (Exception exception) {
//            log.error("Exception {}", exception.toString());
//            fail("Exception {}", exception.toString());
//        }
//    }


    //-------------------------------------------------------
    //CRUD
    //-------------------------------------------------------

    /**
     * متد ثبت
     */
    @Test
    @Order(8)
    void createTest() {
        try {
            //ایجاد مدل درخواست
            BackUserDto backUserDto = new BackUserDto(null, "مریم", "آزیش", "09354161222", GenderEnum.FEMALE, null, null, null, null);

            BackUserCreateRequestDto dto = new BackUserCreateRequestDto(username, password, backUserDto, Set.of(1L), null);

            dto.setBackUserDto(backUserDto);

            //ارسال درخواست
            ResponseEntity<ClientResponseDto<BackUserResponseDto>> response = this.testRestTemplate.exchange(this.MODULE_API, HttpMethod.POST, new HttpEntity<>(dto, getHeaders(tokenUsername)), new ParameterizedTypeReference<>() {
            });
            //بررسی پاسخ
            assertThat(response).isNotNull();
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
            //بررسی های تست
            assertThat(response.getBody().getData()).isNotNull();
            assertThat(response.getBody().getData().getId()).isNotNull();
        } catch (Exception exception) {
            log.error("Exception {}", exception.toString());
            fail("Exception {}", exception.toString());
        }
    }

    /**
     * ویرایش اطلاعات کاربر برنامه فرانت
     */
    @Test
    @Order(9)
    void updateTest() {
        try {
            Long testId = 1L;
            BackUserDto backUserDto = new BackUserDto(null, "مریم", "آزیش", "09354161222", GenderEnum.FEMALE, null, null, null, null);

            BackUserUpdateRequestDto dto = new BackUserUpdateRequestDto(testId, username, password, backUserDto, Set.of(1L), null);

            //ارسال درخواست
            ResponseEntity<ClientResponseDto<AppUserResponseDto>> response = this.testRestTemplate.exchange(this.MODULE_API, HttpMethod.PUT, new HttpEntity<>(dto, getHeaders(tokenUsername)), new ParameterizedTypeReference<>() {
            });
            //بررسی پاسخ
            assertThat(response).isNotNull();
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
            //بررسی های تست
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getData()).isNotNull();
            AppUserResponseDto responseDto = response.getBody().getData();
            assertThat(responseDto).isNotNull();
            assertThat(responseDto.getId()).isNotNull();
            assertThat(responseDto.getId()).isEqualTo(testId);
            id = responseDto.getId();
        } catch (Exception exception) {
            log.error("Exception: {}", exception);
            fail("Exception: {}", exception);
        }
    }
    /**
     * جستجو کاربر برنامه بک
     */
    @Test
    @Order(12)
    void readByIdTest() {
        try {
            //ارسال درخواست
            ResponseEntity<ClientResponseDto<BackUserDto>> response = this.testRestTemplate.exchange(this.MODULE_API + "/" + id, HttpMethod.GET, new HttpEntity<>(getHeaders(tokenUsername)), new ParameterizedTypeReference<>() {
            });
            //بررسی پاسخ
            assertThat(response).isNotNull();
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
            //بررسی های تست
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getData()).isNotNull();
            BackUserDto responseDto = response.getBody().getData();
            assertThat(responseDto).isNotNull();
            System.out.println(responseDto.toString());
        } catch (Exception exception) {
            log.error("Exception: {}", exception);
            fail("Exception: {}", exception);
        }
    }

    /**
     * جستجو تمامی کاربران برنامه بک
     */
    @Test
    @Order(13)
    void readAllTest() {
        try {
            //ارسال درخواست
            ResponseEntity<ClientResponseDto<CustomPageResponseDto<BackUserDto>>> response = this.testRestTemplate.exchange(this.MODULE_API + "/read-all", HttpMethod.GET, new HttpEntity<>(getHeaders(tokenUsername)), new ParameterizedTypeReference<>() {
            });
            //بررسی پاسخ
            assertThat(response).isNotNull();
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
            //بررسی های تست
            assertThat(response.getBody()).isNotNull();
            CustomPageResponseDto<BackUserDto> responseDto = response.getBody().getData();
            assertThat(responseDto.getSize()).isGreaterThan(0);
        } catch (Exception exception) {
            log.error("Exception: {}", exception);
            fail("Exception: {}", exception);
        }
    }

    /**
     * غیرفعال کاربر برنامه بک
     */
    @Test
    @Order(14)
    void invalidTest() {
        try {
            //ارسال درخواست
            ResponseEntity<ClientResponseDto<Boolean>> response = this.testRestTemplate.exchange(this.MODULE_API + "/invalid/true/" + id, HttpMethod.PUT, new HttpEntity<>(getHeaders(tokenUsername)), new ParameterizedTypeReference<>() {
            });
            //بررسی پاسخ
            assertThat(response).isNotNull();
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
            //بررسی های تست
            assertThat(response.getBody()).isNotNull();
        } catch (Exception exception) {
            log.error("Exception: {}", exception);
            fail("Exception: {}", exception);
        }
    }

    /**
     * حذف کاربر برنامه بک
     * به دلیل اینکه بعد از ثبت نمیخواستم حذف شود این متد تست کامنت شده است
     */
    @Test
    @Order(14)
    void deleteTest() {
        try {
            //ارسال درخواست
            ResponseEntity<ClientResponseDto<Boolean>> response = this.testRestTemplate.exchange(this.MODULE_API + "/" + id, HttpMethod.DELETE, new HttpEntity<>(getHeaders(tokenUsername)), new ParameterizedTypeReference<>() {
            });
            //بررسی پاسخ
            assertThat(response).isNotNull();
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
            //بررسی های تست
            assertThat(response.getBody()).isNotNull();
        } catch (Exception exception) {
            log.error("Exception: {}", exception);
            fail("Exception: {}", exception);
        }
    }
}
