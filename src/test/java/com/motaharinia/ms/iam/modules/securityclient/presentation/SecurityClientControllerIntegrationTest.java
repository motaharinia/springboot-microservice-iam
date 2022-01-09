package com.motaharinia.ms.iam.modules.securityclient.presentation;

import com.motaharinia.ms.iam.config.security.oauth2.dto.BearerTokenDto;
import com.motaharinia.ms.iam.modules.securityclient.business.enumeration.AuthorityEnum;
import com.motaharinia.ms.iam.modules.securityclient.business.enumeration.GrantTypeEnum;
import com.motaharinia.msutility.custom.customdto.ClientResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.util.UriComponentsBuilder;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author pourya
 * @date 2021-08-28
 * client کلاس تیت یکپارچه کاربر
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("dev")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Slf4j
class SecurityClientControllerIntegrationTest {

    /**
     * پورت رندوم تست
     */
    @LocalServerPort
    private Integer PORT;
    /**
     * نشانی وب ماژول
     */
    private String MODULE_API;

    HttpHeaders headers;

    /**
     * شیی فراخوان تست
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
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        //مسیر پیش فرض ماژول
        this.MODULE_API = "http://localhost:" + PORT + "/api/v1.0/security-client";
    }


    @Test
    @Order(1)
    void createTest() {
        //ایجاد مدل درخواست
        SecurityClientRequestDto dto = new SecurityClientRequestDto();
        dto.setClientId("pourya");
        dto.setClientSecret("1234");
        dto.setClientTitle("test title");
        HashSet<String> authoritySet = new HashSet<>();
        authoritySet.add(AuthorityEnum.READ_PROFILE.getValue());
        dto.setAuthoritySet(authoritySet);

        //ارسال درخواست
        ResponseEntity<ClientResponseDto<SecurityClientResponseDto>> response = this.testRestTemplate.exchange(this.MODULE_API + "/", HttpMethod.POST, new HttpEntity<>(dto, headers), new ParameterizedTypeReference<>() {
        });
        //بررسی پاسخ
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        //بررسی های تست
        assertThat(response.getBody().getData()).isNotNull();
        SecurityClientResponseDto responseDto = response.getBody().getData();
        assertThat(responseDto.getClientId()).isEqualTo(dto.getClientId());
        assertThat(responseDto.getClientTitle()).isEqualTo(dto.getClientTitle());
    }

    @Test
    @Order(2)
    void readTest() {
        //ایجاد مدل درخواست
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(this.MODULE_API + "/");
        builder.queryParam("clientId", "pourya");
        builder.queryParam("clientSecret", "1234");

        //ارسال درخواست
        ResponseEntity<ClientResponseDto<SecurityClientResponseDto>> response = this.testRestTemplate.exchange(builder.build().toUri(), HttpMethod.GET, new HttpEntity<>(headers), new ParameterizedTypeReference<>() {
        });
        //بررسی پاسخ
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        //بررسی های تست
        assertThat(response.getBody().getData()).isNotNull();
        SecurityClientResponseDto responseDto = response.getBody().getData();
        assertThat(responseDto.getClientId()).isEqualTo("pourya");
    }

    @Test
    @Order(3)
    void updateTest() {
        //ایجاد مدل درخواست
        UpdateSecurityClientRequestDto dto = new UpdateSecurityClientRequestDto();
        dto.setClientId("pourya");
        dto.setNewClientId("pourya123");
        dto.setClientSecret("1234");
        dto.setNewClientSecret("12345");
        dto.setClientTitle("test title123");
        dto.setSecretRequired(true);
        HashSet<String> authoritySet = new HashSet<>();
        authoritySet.add("READ_PROFILE");
        dto.setAuthoritySet(authoritySet);

        //ارسال درخواست
        ResponseEntity<ClientResponseDto<SecurityClientResponseDto>> response = this.testRestTemplate.exchange(this.MODULE_API + "/", HttpMethod.PUT, new HttpEntity<>(dto, headers), new ParameterizedTypeReference<>() {
        });
        //بررسی پاسخ
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        //بررسی های تست
        assertThat(response.getBody().getData()).isNotNull();
        SecurityClientResponseDto responseDto = response.getBody().getData();
        assertThat(responseDto.getClientId()).isEqualTo(dto.getNewClientId());
        assertThat(responseDto.getClientTitle()).isEqualTo(dto.getClientTitle());
    }

//    @Test
//    @Order(5)
//    void deleteTest() {
//        //ایجاد مدل درخواست
//        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(this.MODULE_API + "/");
//        builder.queryParam("clientId", "pourya123");
//        builder.queryParam("clientSecret", "12345");
//
//        //ارسال درخواست
//        ResponseEntity<ClientResponseDto<SecurityClientResponseDto>> response = this.testRestTemplate.exchange(builder.build().toUri(), HttpMethod.DELETE, new HttpEntity<>(headers), new ParameterizedTypeReference<>() {
//        });
//        //بررسی پاسخ
//        assertThat(response).isNotNull();
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//        assertThat(response.getBody()).isNotNull();
//        //بررسی های تست
//        assertThat(response.getBody().getData()).isNotNull();
//    }

    //معادل متد signin برای securityUser میباشد
    @Test
    @Order(4)
    void createBearerTokenTest() {
        //ایجاد مدل درخواست
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(this.MODULE_API + "/create-token");
        builder.queryParam("grant_type", GrantTypeEnum.CLIENT_CREDENTIAL.getValue());
        builder.queryParam("scope", "all");

        String auth = "pourya123" + ":" + "12345";
        headers.set("X-Authorization", "Basic " + new String(Base64.encodeBase64(
                auth.getBytes(StandardCharsets.UTF_8))));

        //ارسال درخواست
        ResponseEntity<ClientResponseDto<BearerTokenDto>> response = this.testRestTemplate.exchange(builder.build().toUri(), HttpMethod.POST, new HttpEntity<>(headers), new ParameterizedTypeReference<>() {
        });
        //بررسی پاسخ
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        //بررسی های تست
        assertThat(response.getBody().getData()).isNotNull();
    }

//    public static void main(String[] args) {
//        String auth = "pourya123" + ":" + "12345";
//        System.out.println(new String(Base64.encodeBase64(
//                auth.getBytes(StandardCharsets.UTF_8))));
//    }
}