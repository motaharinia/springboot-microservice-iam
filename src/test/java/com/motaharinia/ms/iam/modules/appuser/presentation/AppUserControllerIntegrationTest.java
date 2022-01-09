package com.motaharinia.ms.iam.modules.appuser.presentation;

import com.motaharinia.ms.iam.config.security.oauth2.dto.AppUserDto;
import com.motaharinia.ms.iam.config.security.oauth2.dto.BackUserDto;
import com.motaharinia.ms.iam.config.security.oauth2.dto.BearerTokenDto;
import com.motaharinia.ms.iam.config.security.oauth2.enumeration.GenderEnum;
import com.motaharinia.ms.iam.modules.appuser.business.service.AppUserService;
import com.motaharinia.ms.iam.modules.appuser.presentation.changepassword.ChangePasswordRequestDto;
import com.motaharinia.ms.iam.modules.appuser.presentation.changepassword.ChangePasswordResponseDto;
import com.motaharinia.ms.iam.modules.appuser.presentation.dto.*;
import com.motaharinia.ms.iam.modules.appuser.presentation.dto.create.AppUserCreateRequestDto;
import com.motaharinia.ms.iam.modules.appuser.presentation.forgetpassword.ForgetPasswordCheckOtpRequestDto;
import com.motaharinia.ms.iam.modules.appuser.presentation.forgetpassword.ForgetPasswordCheckUsernameResponseDto;
import com.motaharinia.ms.iam.modules.appuser.presentation.signin.SigninCheckCredentialRequestDto;
import com.motaharinia.ms.iam.modules.appuser.presentation.signin.SigninCheckCredentialResponseDto;
import com.motaharinia.ms.iam.modules.appuser.presentation.signup.SignupCheckCredentialResponseDto;
import com.motaharinia.ms.iam.modules.appuser.presentation.signup.SignupCheckOtpRequestOtpDto;
import com.motaharinia.ms.iam.modules.backuser.business.BackUserService;
import com.motaharinia.ms.iam.modules.backuser.presentation.signin.SigninCheckOtpRequestDto;
import com.motaharinia.ms.iam.modules.fso.FsoSetting;
import com.motaharinia.ms.iam.modules.securityuser.business.service.SecurityUserService;
import com.motaharinia.ms.iam.modules.securityuser.presentation.securitypermission.SecurityPermissionCreateRequestDto;
import com.motaharinia.ms.iam.modules.securityuser.presentation.securityuser.SecurityUserReadDto;
import com.motaharinia.ms.iam.utils.TestFileUtils;
import com.motaharinia.msjpautility.page.CustomPageResponseDto;
import com.motaharinia.msutility.custom.customdto.ClientResponseDto;
import com.motaharinia.msutility.tools.fso.view.FileViewDto;
import com.motaharinia.msutility.tools.fso.view.FileViewDtoStatusEnum;
import com.motaharinia.msutility.tools.string.RandomGenerationTypeEnum;
import com.motaharinia.msutility.tools.string.StringTools;
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

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("dev")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Slf4j
class AppUserControllerIntegrationTest {
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

    /**
     * کلاس آپلود تستی فایل
     */
    private TestFileUtils testFileUtils;


    @Autowired
    private SecurityUserService securityUserService;
    @Autowired
    private AppUserService appUserService;
    @Autowired
    private BackUserService backUserService;

    private static final String username = "0083419004";
    //private static final String username = "0063234114";
    //private static final String username = "10103800056";
    //private static final String username = "0036011861";
    private static final String password = "asd123ASD!@#";
    private static final String newPassword = "asd123ASD!@#$";

    private static final String backUserTokenUsername = "0083419004";
    private static final String appUserTokenUsername = "0063234114";

    private static String testOtp = "";
    private static Long testId = 8L;

    SecurityPermissionCreateRequestDto dto = null;


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
        this.MODULE_API = "http://localhost:" + PORT + "/api/v1.0/app-user";

        //آماده سازی کلاس آپلود تستی فایل
        testFileUtils = new TestFileUtils(testRestTemplate, PORT);
    }

    private HttpHeaders getHeaders(String tokenUsername, Boolean isFront) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.set(headerCaptchaKey, "123456");
        headers.set(headerCaptchaValue, "a3df23");
        if (!ObjectUtils.isEmpty(tokenUsername)) {
            BearerTokenDto bearerTokenDto = null;
            SecurityUserReadDto securityUserReadDto = securityUserService.serviceReadByUsername(tokenUsername, isFront);
            if (isFront) {
                AppUserDto appUserDto = appUserService.serviceReadById(securityUserReadDto.getAppUserId());
                bearerTokenDto = securityUserService.createBearerToken(securityUserReadDto.getSecurityUserId(), false, appUserDto, new HashMap<>());
            } else {
                BackUserDto backUserDto = backUserService.serviceReadById(securityUserReadDto.getBackUserId());
                bearerTokenDto = securityUserService.createBearerToken(securityUserReadDto.getSecurityUserId(), false, backUserDto, new HashMap<>());
            }
            headers.set("Authorization", "Bearer " + bearerTokenDto.getAccessToken());
        }
        return headers;
    }

    //-------------------------------------------------------
    //sign up
    //-------------------------------------------------------

    /**
     * متد گام اول ثبت نام(بررسی کلمه کاربری و رمزعبور)
     */
    @Test
    @Order(1)
    void signupCheckCredentialTest() {
        try {
            //ایجاد مدل درخواست
            //کدملی همان username است
            SignupCheckOtpRequestOtpDto dto = new SignupCheckOtpRequestOtpDto(username, password, password, "09354161222", testOtp, false, null);

            //ارسال درخواست
            ResponseEntity<ClientResponseDto<SignupCheckCredentialResponseDto>> response = this.testRestTemplate.exchange(this.MODULE_API + "/signup-check-credential", HttpMethod.POST, new HttpEntity<>(dto, getHeaders("", null)), new ParameterizedTypeReference<>() {
            });
            //بررسی پاسخ
            assertThat(response).isNotNull();
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
            //بررسی های تست
            assertThat(response.getBody().getData()).isNotNull();
            assertThat(response.getBody().getData().getTestOtp()).isNotNull();

            testOtp = response.getBody().getData().getTestOtp();
            System.out.println("testOtp:" + testOtp);
        } catch (Exception exception) {
            log.error("Exception {}", exception.toString());
            fail("Exception {}", exception.toString());
        }
    }

    /**
     * متد گام دوم ثبت نام(بررسی کد تایید)
     */
    @Test
    @Order(2)
    void signupCheckOtpTest() {
        try {
            //ایجاد مدل درخواست
            //کدملی همان username است
            SignupCheckOtpRequestOtpDto dto = new SignupCheckOtpRequestOtpDto(username, password, password, "09354161222", testOtp, false, null);
            //ارسال درخواست
            ResponseEntity<ClientResponseDto<BearerTokenDto>> response = this.testRestTemplate.exchange(this.MODULE_API + "/signup-check-otp", HttpMethod.POST, new HttpEntity<>(dto, getHeaders("", null)), new ParameterizedTypeReference<>() {
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
            ResponseEntity<ClientResponseDto<SigninCheckCredentialResponseDto>> response = this.testRestTemplate.exchange(this.MODULE_API + "/signin-check-credential", HttpMethod.POST, new HttpEntity<>(dto, getHeaders("", null)), new ParameterizedTypeReference<>() {
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
            ResponseEntity<ClientResponseDto<BearerTokenDto>> response = this.testRestTemplate.exchange(this.MODULE_API + "/signin-check-otp", HttpMethod.POST, new HttpEntity<>(dto, getHeaders("", null)), new ParameterizedTypeReference<>() {
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

    /**
     * متد تغییر رمز عبور
     */
    @Test
    @Order(5)
    void changePasswordTest() {
        try {
            //ایجاد مدل درخواست
            ChangePasswordRequestDto dto = new ChangePasswordRequestDto(password, newPassword, newPassword);
            //ارسال درخواست
            ResponseEntity<ClientResponseDto<ChangePasswordResponseDto>> response = this.testRestTemplate.exchange(this.MODULE_API + "/change-password", HttpMethod.PUT, new HttpEntity<>(dto, getHeaders(backUserTokenUsername, true)), new ParameterizedTypeReference<>() {
            });
            //بررسی پاسخ
            assertThat(response).isNotNull();
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
            //بررسی های تست
            assertThat(response.getBody().getData()).isNotNull();
            assertThat(response.getBody().getData().getUsername()).isNotNull();
        } catch (Exception exception) {
            log.error("Exception {}", exception.toString());
            fail("Exception {}", exception.toString());
        }
    }

    /**
     * متد گام اول فراموشی رمز عبور (ارسال کد تایید)
     */
    @Test
    @Order(6)
    void forgetPasswordCheckUsernameTest() {
        try {
            //ارسال درخواست
            ResponseEntity<ClientResponseDto<ForgetPasswordCheckUsernameResponseDto>> response = this.testRestTemplate.exchange(this.MODULE_API + "/forget-password-check-username/" + username + "/", HttpMethod.PUT, new HttpEntity<>(getHeaders("", null)), new ParameterizedTypeReference<>() {
            });
            //بررسی پاسخ
            assertThat(response).isNotNull();
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
            //بررسی های تست
            assertThat(response.getBody().getData()).isNotNull();
            assertThat(response.getBody().getData().getTestOtp()).isNotNull();
            testOtp = response.getBody().getData().getTestOtp();
            System.out.println("testOtp:" + testOtp);
        } catch (Exception exception) {
            log.error("Exception {}", exception.toString());
            fail("Exception {}", exception.toString());
        }
    }

    /**
     * متد گام دوم فراموشی رمز عبور (ریست کردن رمز عبور یا کد تایید)
     */
    @Test
    @Order(7)
    void forgetPasswordCheckOtpTest() {
        try {
            //ایجاد مدل درخواست
            ForgetPasswordCheckOtpRequestDto dto = new ForgetPasswordCheckOtpRequestDto(username, newPassword, newPassword, testOtp, false);
            //ارسال درخواست
            ResponseEntity<ClientResponseDto<BearerTokenDto>> response = this.testRestTemplate.exchange(this.MODULE_API + "/forget-password-check-otp", HttpMethod.PUT, new HttpEntity<>(dto, getHeaders("", null)), new ParameterizedTypeReference<>() {
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
    //CRUD
    //-------------------------------------------------------

    /**
     * ثبت اطلاعات کاربر برنامه فرانت
     */
    @Test
    @Order(8)
    void createTest() {
        try {

            String fileUploadKey = StringTools.generateRandomString(RandomGenerationTypeEnum.LATIN_CHARACTERS, 5, false);
            testFileUtils.upload(FsoSetting.MS_IAM_APP_USER_PROFILE_IMAGE, getClass().getClassLoader().getResource("testfile/image/1.png").getPath(), fileUploadKey);

            AppUserDto appUserDto = new AppUserDto(null, "کرم", "کرمی", "09555555575", "mm@gmail.com", GenderEnum.FEMALE, null, null, null);

            FileViewDto fileViewDto = new FileViewDto();
            fileViewDto.setStatusEnum(FileViewDtoStatusEnum.ADDED);
            fileViewDto.setKey(fileUploadKey);
            ArrayList<FileViewDto> list = new ArrayList<>() {{
                add(fileViewDto);
            }};

            AppUserCreateRequestDto appUserCreateRequestDto = new AppUserCreateRequestDto("0051558602", "asd123ASD!@#", "asd123ASD!@#", appUserDto, null, null, null, null, list);

            //ارسال درخواست
            ResponseEntity<ClientResponseDto<AppUserResponseDto>> response = this.testRestTemplate.exchange(this.MODULE_API, HttpMethod.POST, new HttpEntity<>(appUserCreateRequestDto, getHeaders(backUserTokenUsername, false)), new ParameterizedTypeReference<>() {
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
            testId = responseDto.getId();
        } catch (Exception exception) {
            log.error("Exception:", exception);
            fail("Exception: {}", exception);
        }
    }

    /**
     * ویرایش اطلاعات کاربر برنامه فرانت
     */
    @Test
    @Order(9)
    void updateTest() {
        try {

            //ارسال درخواست گرفتن اطلاعات جهت ویرایش
            ResponseEntity<ClientResponseDto<AppUserReadResponseDto>> response = this.testRestTemplate.exchange(this.MODULE_API + "/" + testId, HttpMethod.GET, new HttpEntity<>(getHeaders(backUserTokenUsername, false)), new ParameterizedTypeReference<>() {
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

            //آپلود و ساخت مدل فایل دوم
            String fileUploadKey = StringTools.generateRandomString(RandomGenerationTypeEnum.LATIN_CHARACTERS, 5, false);
            FileViewDto fileViewDto2 = new FileViewDto();
            testFileUtils.upload(FsoSetting.MS_IAM_APP_USER_PROFILE_IMAGE, getClass().getClassLoader().getResource("testfile/image/2.png").getPath(), fileUploadKey);
            fileViewDto2.setStatusEnum(FileViewDtoStatusEnum.ADDED);
            fileViewDto2.setKey(fileUploadKey);

            //ویرایش مدل - حذف فایل اول و اضافه کردن فایل دوم
            AppUserDto appUserDto = new AppUserDto(testId, "کریم", "کریمی", "09555555571", "nn@gmail.com", GenderEnum.FEMALE, null, null, null);
            AppUserUpdateRequestDto appUserUpdateRequestDto = new AppUserUpdateRequestDto(testId, "0451682300", appUserDto, 73264271044L, null, null, null, responseDto.getProfileImageFileList());
            //appUserUpdateRequestDto.getProfileImageFileList().get(0).setStatusEnum(FileViewDtoStatusEnum.DELETED);
            appUserUpdateRequestDto.getProfileImageFileList().add(fileViewDto2);

            //ارسال درخواست
            ResponseEntity<ClientResponseDto<AppUserResponseDto>> response2 = this.testRestTemplate.exchange(this.MODULE_API, HttpMethod.PUT, new HttpEntity<>(appUserUpdateRequestDto, getHeaders(backUserTokenUsername, false)), new ParameterizedTypeReference<>() {
            });
            //بررسی پاسخ
            assertThat(response2).isNotNull();
            assertThat(response2.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response2.getBody()).isNotNull();
            //بررسی های تست
            assertThat(response2.getBody()).isNotNull();
            assertThat(response2.getBody().getData()).isNotNull();
            AppUserResponseDto responseDto2 = response2.getBody().getData();
            assertThat(responseDto2).isNotNull();
            assertThat(responseDto2.getId()).isNotNull();
            assertThat(responseDto2.getId()).isEqualTo(testId);
        } catch (Exception exception) {
            log.error("Exception:", exception);
            fail("Exception: {}", exception);
        }
    }

    /**
     * جستجو کاربر برنامه فرانت
     */
    @Test
    @Order(12)
    void readByIdTest() {
        try {
            //ارسال درخواست
            ResponseEntity<ClientResponseDto<AppUserReadResponseDto>> response = this.testRestTemplate.exchange(this.MODULE_API + "/" + testId, HttpMethod.GET, new HttpEntity<>(getHeaders(backUserTokenUsername, false)), new ParameterizedTypeReference<>() {
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
     * جستجو تمامی کاربران برنامه فرانت
     */
    @Test
    @Order(13)
    void readAllTest() {
        try {
            //ارسال درخواست
            ResponseEntity<ClientResponseDto<CustomPageResponseDto<AppUserReadResponseDto>>> response = this.testRestTemplate.exchange(this.MODULE_API + "/read-all", HttpMethod.GET, new HttpEntity<>(getHeaders(backUserTokenUsername, false)), new ParameterizedTypeReference<>() {
            });
            //بررسی پاسخ
            assertThat(response).isNotNull();
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
            //بررسی های تست
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getData()).isNotNull();
            CustomPageResponseDto<AppUserReadResponseDto> responseDto = response.getBody().getData();
            assertThat(responseDto.getSize()).isGreaterThan(0);
            System.out.println(responseDto.toString());
        } catch (Exception exception) {
            log.error("Exception:", exception);
            fail("Exception: {}", exception);
        }
    }


    /**
     * فعال و غیرفعال کردن
     */
    @Test
    @Order(15)
    void invalidTest() {
        try {
            //ارسال درخواست
            ResponseEntity<ClientResponseDto<Boolean>> response = this.testRestTemplate.exchange(this.MODULE_API + "/invalid/true/" + testId, HttpMethod.PUT, new HttpEntity<>(getHeaders(backUserTokenUsername, false)), new ParameterizedTypeReference<>() {
            });
            //بررسی پاسخ
            assertThat(response).isNotNull();
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
            //بررسی های تست
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getData()).isNotNull();
            Boolean responseDto = response.getBody().getData();
            assertThat(responseDto).isNotNull();
            assertThat(responseDto).isTrue();
        } catch (Exception exception) {
            log.error("Exception:", exception);
            fail("Exception: {}", exception);
        }
    }

    @Test
    @Order(18)
    void createBatchTest() {
        try {

            String fileUploadKey = StringTools.generateRandomString(RandomGenerationTypeEnum.LATIN_CHARACTERS, 5, false);
            testFileUtils.upload(FsoSetting.MS_IAM_APP_USER_CREATE_BATCH, getClass().getClassLoader().getResource("testfile/excel/AppUserCreate.xlsx").getPath(), fileUploadKey);

            //ارسال درخواست
            ResponseEntity<ClientResponseDto<Boolean>> response = this.testRestTemplate.exchange(this.MODULE_API + "/create-batch/" + fileUploadKey, HttpMethod.POST, new HttpEntity<>(getHeaders("0083419004", false)), new ParameterizedTypeReference<>() {
            });
            //بررسی پاسخ
            assertThat(response).isNotNull();
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(Objects.requireNonNull(response.getBody()).getData()).isTrue();

        } catch (Exception exception) {
            log.error("Exception {}", exception.toString());
            fail("Exception {}", exception.toString());
        }
    }

    @Test
    @Order(19)
    void inviteFriendByInvitationCodeTest() {
        try {

            InviteFriendRequestDto dto = new InviteFriendRequestDto(Set.of("09553344777", "09553355888"));

            //ارسال درخواست
            ResponseEntity<ClientResponseDto<Boolean>> response = this.testRestTemplate.exchange(this.MODULE_API + "/invite-friend-by-invitation-code", HttpMethod.POST, new HttpEntity<>(dto, getHeaders("0083419004", true)), new ParameterizedTypeReference<>() {
            });
            //بررسی پاسخ
            assertThat(response).isNotNull();
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody().getData()).isTrue();

        } catch (Exception exception) {
            log.error("Exception {}", exception.toString());
            fail("Exception {}", exception.toString());
        }
    }

    /**
     * متد ویرایش اطلاعات پروفایل کاربر برنامه فرانت توسط خودش
     */
    @Test
    @Order(20)
    void updateProfileTest() {
        try {

            testId = 25L;
            //ارسال درخواست گرفتن اطلاعات جهت ویرایش
            ResponseEntity<ClientResponseDto<AppUserReadResponseDto>> response = this.testRestTemplate.exchange(this.MODULE_API + "/" + testId, HttpMethod.GET, new HttpEntity<>(getHeaders(backUserTokenUsername, false)), new ParameterizedTypeReference<>() {
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

            //آپلود و ساخت مدل فایل
            String fileUploadKey = StringTools.generateRandomString(RandomGenerationTypeEnum.LATIN_CHARACTERS, 5, false);
            FileViewDto fileViewDto3 = new FileViewDto();
            testFileUtils.upload(FsoSetting.MS_IAM_APP_USER_PROFILE_IMAGE, getClass().getClassLoader().getResource("testfile/image/3.png").getPath(), fileUploadKey);
            fileViewDto3.setStatusEnum(FileViewDtoStatusEnum.ADDED);
            fileViewDto3.setKey(fileUploadKey);

            //ویرایش مدل - حذف فایل اول و اضافه کردن فایل دوم
            AppUserDto appUserDto = new AppUserDto(testId, "کریم", "کریمی", "09555553371", "nn@gmail.com", GenderEnum.FEMALE, null, null, null);
            AppUserUpdateProfileRequestDto appUserUpdateProfileRequestDto = new AppUserUpdateProfileRequestDto(appUserDto, 73264271044L, "1819987664", null, null, responseDto.getProfileImageFileList());
            //appUserUpdateProfileRequestDto.getProfileImageFileList().get(0).setStatusEnum(FileViewDtoStatusEnum.DELETED);
            appUserUpdateProfileRequestDto.getProfileImageFileList().add(fileViewDto3);

            //ارسال درخواست
            ResponseEntity<ClientResponseDto<AppUserResponseDto>> response2 = this.testRestTemplate.exchange(this.MODULE_API + "/update-profile", HttpMethod.PUT, new HttpEntity<>(appUserUpdateProfileRequestDto, getHeaders(appUserTokenUsername, true)), new ParameterizedTypeReference<>() {
            });
            //بررسی پاسخ
            assertThat(response2).isNotNull();
            assertThat(response2.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response2.getBody()).isNotNull();
            //بررسی های تست
            assertThat(response2.getBody()).isNotNull();
            assertThat(response2.getBody().getData()).isNotNull();
            AppUserResponseDto responseDto2 = response2.getBody().getData();
            assertThat(responseDto2).isNotNull();
            assertThat(responseDto2.getId()).isNotNull();
            assertThat(responseDto2.getId()).isEqualTo(testId);
        } catch (Exception exception) {
            log.error("Exception:", exception);
            fail("Exception: {}", exception);
        }
    }

    /**
     *مشاهده اطلاعات پروفایل کاربر برنامه فرانت
     */
    @Test
    @Order(21)
    void readProfileTest() {
        try {
            //ارسال درخواست
            ResponseEntity<ClientResponseDto<AppUserReadResponseDto>> response = this.testRestTemplate.exchange(this.MODULE_API + "/read-profile", HttpMethod.GET, new HttpEntity<>(getHeaders(appUserTokenUsername, true)), new ParameterizedTypeReference<>() {
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
}
