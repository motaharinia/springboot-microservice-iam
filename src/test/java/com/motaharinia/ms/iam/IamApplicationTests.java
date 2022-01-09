package com.motaharinia.ms.iam;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author eng.motahari@gmail.com<br>
 * کلاس اصلی اجرای اپلیکیشن
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("dev")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Slf4j
class IamApplicationTests {

    /**
     * پورت نشانی وب ماژول
     */
    @LocalServerPort
    private Integer PORT;
    /**
     * نشانی وب ماژول
     */
    private String MODULE_API;


    @Value("${spring.application.name}")
    private String springApplicationName;

    @Autowired
    private TestRestTemplate testRestTemplate;

    /**
     * این متد مقادیر پیش فرض را قبل از اجرای تمامی متدهای تست این کلاس مقداردهی اولیه میکند
     */
    @BeforeAll
    void beforeAll() {
        //تنظیم زبان لوکیل پروژه روی پارسی
        Locale.setDefault(new Locale("fa", "IR"));
        this.MODULE_API = "http://localhost:" + PORT + "/";
    }

    @Test
    void contextLoads() {
        String response = testRestTemplate.getForObject(MODULE_API, String.class);
        assertThat(response).isEqualTo(springApplicationName);
    }

}
