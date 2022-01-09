package com.motaharinia.ms.iam.modules.securityuser.business.service;

import com.motaharinia.ms.iam.config.security.oauth2.dto.AppUserDto;
import com.motaharinia.ms.iam.config.security.oauth2.dto.BearerTokenDto;
import com.motaharinia.ms.iam.config.security.oauth2.dto.LoggedInUserDto;
import com.motaharinia.ms.iam.config.security.oauth2.dto.SecurityUserDto;
import com.motaharinia.ms.iam.config.security.oauth2.resource.ResourceUserTokenProvider;
import com.motaharinia.ms.iam.modules.appuser.business.mapper.AppUserMapper;
import com.motaharinia.ms.iam.modules.appuser.business.service.AppUserService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("dev")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Slf4j
class ResourceUserTokenProviderUnitTest {
    @Autowired
    SecurityUserTokenService securityUserTokenService;
    @Autowired
    private AppUserService appUserService;
    @Autowired
    private AppUserMapper mapper;
    @Autowired
    private ResourceUserTokenProvider resourceUserTokenProvider;

    private static final String username = "0083419004";
    private static final String password = "asd123ASD!@#";
    private static final String mobileNo = "09354161222";
    private static Set<String> securityRoleDtoSet;
    private static Set<String> securityPermissionDtoSet;


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
        //تولید لیست نقش های کاربری
        securityRoleDtoSet = new HashSet<>();
        securityRoleDtoSet.add("DARA");
        securityRoleDtoSet.add("DANA");
        //تولید لیست دسترسی ها
        securityPermissionDtoSet = new HashSet<>();
        securityPermissionDtoSet.add("appUser_readByNationalCode");
    }

    @Test
    @Order(1)
    void createTokenTest() {
        try {
            //جستجوی کاربر امنیت
            SecurityUserDto securityUserDto = new SecurityUserDto();
            securityUserDto.setUsername(username);
            securityUserDto.setMobileNo(mobileNo);

            //جستجوی کاربر فرانت برنامه
            AppUserDto appUserDto = appUserService.readByNationalCode(username);

            //ایجاد مدل کاربر لاگین شده
            LoggedInUserDto loggedInUserDto = new LoggedInUserDto(securityUserDto,securityRoleDtoSet,securityPermissionDtoSet,appUserDto);

            //ایجاد توکن
            BearerTokenDto bearerTokenDto =  securityUserTokenService.createBearerToken(loggedInUserDto,false,new HashMap<>(),null,true);
            assertThat(bearerTokenDto).isNotNull();
            log.info("token:" + bearerTokenDto);

            Authentication authentication = resourceUserTokenProvider.getAuthentication(bearerTokenDto.getAccessToken());

            SecurityContextHolder.getContext().setAuthentication(authentication);

            Optional<LoggedInUserDto> loggedInUserDtoOptional = resourceUserTokenProvider.getLoggedInDto();
            assertThat(loggedInUserDtoOptional).isPresent();
            assertThat(loggedInUserDtoOptional.get().getSecurityRoleSet()).isNotNull();
            assertThat(loggedInUserDtoOptional.get().getSecurityRoleSet().size()).isEqualTo(securityRoleDtoSet.size());
            assertThat(loggedInUserDtoOptional.get().getSecurityPermissionSet()).isNotNull();
            assertThat(loggedInUserDtoOptional.get().getSecurityPermissionSet().size()).isEqualTo(securityPermissionDtoSet.size());

            Optional<Set<String>> loggedInAuthorities = resourceUserTokenProvider.getLoggedInAuthorities();
            assertThat(loggedInAuthorities).isPresent();
            assertThat(loggedInAuthorities.get().size()).isEqualTo(securityRoleDtoSet.size() + securityPermissionDtoSet.size());

        }catch (Exception exception){
            log.error("Exception: {}", exception);
            fail("Exception: {}", exception);
        }
    }
}
