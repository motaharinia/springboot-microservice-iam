package com.motaharinia.ms.iam.modules.theme.presentation;

import com.motaharinia.ms.iam.config.security.oauth2.dto.AppUserDto;
import com.motaharinia.ms.iam.config.security.oauth2.dto.BackUserDto;
import com.motaharinia.ms.iam.config.security.oauth2.dto.BearerTokenDto;
import com.motaharinia.ms.iam.modules.appuser.business.service.AppUserService;
import com.motaharinia.ms.iam.modules.backuser.business.BackUserService;
import com.motaharinia.ms.iam.modules.fso.FsoSetting;
import com.motaharinia.ms.iam.modules.securityuser.business.service.SecurityUserService;
import com.motaharinia.ms.iam.modules.securityuser.presentation.securitypermission.SecurityPermissionCreateRequestDto;
import com.motaharinia.ms.iam.modules.securityuser.presentation.securityuser.SecurityUserReadDto;
import com.motaharinia.ms.iam.modules.theme.presentation.dto.*;
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
class ThemeControllerIntegrationTest {
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
     * کلاس آپلود تستی فایل
     */
    private TestFileUtils testFileUtils;


    @Autowired
    private SecurityUserService securityUserService;
    @Autowired
    private AppUserService appUserService;
    @Autowired
    private BackUserService backUserService;

    Random random = new Random();


    private static final String backUserTokenUsername = "0083419004";
    private static final String appUserTokenUsername = "0083419004";

    private static String testOtp = "";
    private static Long testId = 1L;

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
        this.MODULE_API = "http://localhost:" + PORT + "/api/v1.0/theme";

        //آماده سازی کلاس آپلود تستی فایل
        testFileUtils = new TestFileUtils(testRestTemplate, PORT);
    }

    private HttpHeaders getHeaders(String tokenUsername, Boolean isFront) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
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


    /**
     * ثبت تم
     */
    @Test
    @Order(1)
    void createTest() {
        try {

            String fileUploadKey1 = StringTools.generateRandomString(RandomGenerationTypeEnum.LATIN_CHARACTERS, 5, false);
            testFileUtils.upload(FsoSetting.MS_IAM_THEME_IMAGES, getClass().getClassLoader().getResource("testfile/image/1.png").getPath(), fileUploadKey1);

            String fileUploadKey2 = StringTools.generateRandomString(RandomGenerationTypeEnum.LATIN_CHARACTERS, 5, false);
            testFileUtils.upload(FsoSetting.MS_IAM_THEME_IMAGES, getClass().getClassLoader().getResource("testfile/image/2.png").getPath(), fileUploadKey2);


            // هش مپ تنظیمات تم
            HashMap<String, String> settingHashMap = new HashMap<>();
            settingHashMap.put("color", "red");
            settingHashMap.put("align", "left");


            // عکس های مربوط به تم
            FileViewDto fileViewDto1 = new FileViewDto();
            fileViewDto1.setStatusEnum(FileViewDtoStatusEnum.ADDED);
            fileViewDto1.setKey(fileUploadKey1);

            FileViewDto fileViewDto2 = new FileViewDto();
            fileViewDto2.setStatusEnum(FileViewDtoStatusEnum.ADDED);
            fileViewDto2.setKey(fileUploadKey2);

            ArrayList<FileViewDto> list = new ArrayList<>() {{
                add(fileViewDto1);
                add(fileViewDto2);
            }};

            ThemeCreateRequestDto dto = new ThemeCreateRequestDto("عنوان تم تستی" + random.nextInt(1000), settingHashMap, list);

            //ارسال درخواست
            ResponseEntity<ClientResponseDto<ThemeResponseDto>> response = this.testRestTemplate.exchange(this.MODULE_API, HttpMethod.POST, new HttpEntity<>(dto, getHeaders(backUserTokenUsername, false)), new ParameterizedTypeReference<>() {
            });

            //بررسی پاسخ
            assertThat(response).isNotNull();
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
            //بررسی های تست
            assertThat(response.getBody().getData()).isNotNull();
            ThemeResponseDto responseDto = response.getBody().getData();
            assertThat(responseDto).isNotNull();
            assertThat(responseDto.getId()).isNotNull();
            testId = responseDto.getId();
        } catch (Exception exception) {
            log.error("Exception:", exception);
            fail("Exception: {}", exception);
        }
    }

    /**
     * ویرایش اطلاعات تم
     */
    @Test
    @Order(2)
    void updateTest() {
        try {
            //ارسال درخواست گرفتن اطلاعات جهت ویرایش
            ResponseEntity<ClientResponseDto<ThemeReadResponseDto>> response = this.testRestTemplate.exchange(this.MODULE_API + "/" + testId, HttpMethod.GET, new HttpEntity<>(getHeaders(backUserTokenUsername, false)), new ParameterizedTypeReference<>() {
            });
            //بررسی پاسخ
            assertThat(response).isNotNull();
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
            //بررسی های تست
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getData()).isNotNull();
            ThemeReadResponseDto responseDto = response.getBody().getData();
            assertThat(responseDto).isNotNull();

            //آپلود و ساخت مدل فایل دوم
            String fileUploadKey = StringTools.generateRandomString(RandomGenerationTypeEnum.LATIN_CHARACTERS, 5, false);
            FileViewDto fileViewDto3 = new FileViewDto();
            testFileUtils.upload(FsoSetting.MS_IAM_THEME_IMAGES, getClass().getClassLoader().getResource("testfile/image/3.png").getPath(), fileUploadKey);
            fileViewDto3.setStatusEnum(FileViewDtoStatusEnum.ADDED);
            fileViewDto3.setKey(fileUploadKey);


            //ویرایش مدل - حذف فایل اول و اضافه کردن فایل دوم
            // هش مپ تنظیمات تم
            HashMap<String, String> settingHashMap = new HashMap<>();
            settingHashMap.put("color", "red");
            settingHashMap.put("align", "left");
            settingHashMap.put("font", "Tahoma");

            ThemeUpdateRequestDto dto = new ThemeUpdateRequestDto(testId,  "عنوان تم ویرایش شد"+ random.nextInt(100), settingHashMap, responseDto.getImageList());
            dto.getImageList().get(0).setStatusEnum(FileViewDtoStatusEnum.DELETED);
            dto.getImageList().get(1).setStatusEnum(FileViewDtoStatusEnum.EXISTED);
            dto.getImageList().add(fileViewDto3);

            //ارسال درخواست
            ResponseEntity<ClientResponseDto<ThemeResponseDto>> response2 = this.testRestTemplate.exchange(this.MODULE_API, HttpMethod.PUT, new HttpEntity<>(dto, getHeaders(backUserTokenUsername, false)), new ParameterizedTypeReference<>() {
            });
            //بررسی پاسخ
            assertThat(response2).isNotNull();
            assertThat(response2.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response2.getBody()).isNotNull();
            //بررسی های تست
            assertThat(response2.getBody().getData()).isNotNull();
            ThemeResponseDto responseDto2 = response2.getBody().getData();
            assertThat(responseDto2).isNotNull();
            assertThat(responseDto2.getId()).isNotNull();
            assertThat(responseDto2.getId()).isEqualTo(testId);
        } catch (Exception exception) {
            log.error("Exception:", exception);
            fail("Exception: {}", exception);
        }
    }

    /**
     * جستجو تم
     */
    @Test
    @Order(3)
    void readByIdTest() {
        try {
            //ارسال درخواست
            ResponseEntity<ClientResponseDto<ThemeReadResponseDto>> response = this.testRestTemplate.exchange(this.MODULE_API + "/" + testId, HttpMethod.GET, new HttpEntity<>(getHeaders(backUserTokenUsername, false)), new ParameterizedTypeReference<>() {
            });

            //بررسی پاسخ
            assertThat(response).isNotNull();
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
            //بررسی های تست
            assertThat(response.getBody().getData()).isNotNull();
            ThemeReadResponseDto responseDto = response.getBody().getData();
            assertThat(responseDto).isNotNull();
            System.out.println(responseDto.toString());

            responseDto.getImageHashMap().values().forEach(path -> {
                ResponseEntity<byte[]> responseEntity = this.testRestTemplate.exchange("http://localhost:" + PORT + path, HttpMethod.GET, new HttpEntity<>(getHeaders(null, null)), new ParameterizedTypeReference<>() {
                });
                assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
                assertThat(response.getBody()).isNotNull();
            });
        } catch (Exception exception) {
            log.error("Exception:", exception);
            fail("Exception: {}", exception);
        }
    }

    /**
     * جستجو تمامی تم ها
     */
    @Test
    @Order(4)
    void readAllTest() {
        try {
            //ارسال درخواست
            ResponseEntity<ClientResponseDto<CustomPageResponseDto<ThemeReadMinimalResponseDto>>> response = this.testRestTemplate.exchange(this.MODULE_API + "/read-all", HttpMethod.GET, new HttpEntity<>(getHeaders(backUserTokenUsername, false)), new ParameterizedTypeReference<>() {
            });
            //بررسی پاسخ
            assertThat(response).isNotNull();
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
            //بررسی های تست
            assertThat(response.getBody().getData()).isNotNull();
            CustomPageResponseDto<ThemeReadMinimalResponseDto> responseDto = response.getBody().getData();
            assertThat(responseDto.getSize()).isGreaterThan(0);
            System.out.println(responseDto.toString());
        } catch (Exception exception) {
            log.error("Exception:", exception);
            fail("Exception: {}", exception);
        }
    }

    /**
     * ست کردن تم برای کاربر برنامه بک
     */
    @Test
    @Order(5)
    void setTheme() {
        try {
            //ارسال درخواست
            ResponseEntity<ClientResponseDto<ThemeSetThemeDto>> response = this.testRestTemplate.exchange(this.MODULE_API + "/set-default-theme", HttpMethod.PUT, new HttpEntity<>(new ThemeSetThemeDto(testId), getHeaders(backUserTokenUsername, false)), new ParameterizedTypeReference<>() {
            });
            //بررسی پاسخ
            assertThat(response).isNotNull();
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
            //بررسی های تست
            assertThat(response.getBody().getData()).isNotNull();
            ThemeSetThemeDto responseDto = response.getBody().getData();
            assertThat(responseDto.getId()).isEqualTo(testId);
        } catch (Exception exception) {
            log.error("Exception:", exception);
            fail("Exception: {}", exception);
        }
    }

    /**
     * جستجو تم
     */
    @Test
    @Order(6)
    void readDefaultThemeTest() {
        try {
            //ارسال درخواست
            ResponseEntity<ClientResponseDto<ThemeReadResponseDto>> response = this.testRestTemplate.exchange(this.MODULE_API + "/read-default-theme", HttpMethod.GET, new HttpEntity<>(getHeaders(backUserTokenUsername, false)), new ParameterizedTypeReference<>() {
            });

            //بررسی پاسخ
            assertThat(response).isNotNull();
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
            //بررسی های تست
            assertThat(response.getBody().getData()).isNotNull();
            ThemeReadResponseDto responseDto = response.getBody().getData();
            assertThat(responseDto).isNotNull();
            System.out.println(responseDto.toString());

            responseDto.getImageHashMap().values().forEach(path -> {
                ResponseEntity<byte[]> responseEntity = this.testRestTemplate.exchange("http://localhost:" + PORT  + path, HttpMethod.GET, new HttpEntity<>(getHeaders(null, null)), new ParameterizedTypeReference<>() {
                });
                assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
                assertThat(response.getBody()).isNotNull();
            });
        } catch (Exception exception) {
            log.error("Exception:", exception);
            fail("Exception: {}", exception);
        }
    }


    /**
     *حذف تم
     */
    @Test
    @Order(7)
    void deleteTest() {
        try {
            //ارسال درخواست
            ResponseEntity<ClientResponseDto<Boolean>> response = this.testRestTemplate.exchange(this.MODULE_API + "/" + testId, HttpMethod.DELETE, new HttpEntity<>(getHeaders(backUserTokenUsername, false)), new ParameterizedTypeReference<>() {
            });
            //بررسی پاسخ
            assertThat(response).isNotNull();
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
            //بررسی های تست
            assertThat(response.getBody().getData()).isNotNull();
            Boolean responseDto = response.getBody().getData();
            assertThat(responseDto).isTrue();
        } catch (Exception exception) {
            log.error("Exception:", exception);
            fail("Exception: {}", exception);
        }
    }
}
