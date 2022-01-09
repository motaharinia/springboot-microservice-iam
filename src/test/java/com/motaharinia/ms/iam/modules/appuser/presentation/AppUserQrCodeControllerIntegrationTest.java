package com.motaharinia.ms.iam.modules.appuser.presentation;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import com.motaharinia.ms.iam.config.security.oauth2.dto.AppUserDto;
import com.motaharinia.ms.iam.config.security.oauth2.dto.BearerTokenDto;
import com.motaharinia.ms.iam.modules.appuser.business.service.AppUserService;
import com.motaharinia.ms.iam.modules.securityuser.business.service.SecurityUserService;
import com.motaharinia.ms.iam.modules.securityuser.presentation.securityuser.SecurityUserReadDto;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.ObjectUtils;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.CompletableFuture;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("dev")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Slf4j
class AppUserQrCodeControllerIntegrationTest {
    /**
     * پورت رندوم تست
     */
    @LocalServerPort
    private Integer PORT;
    /**
     * نشانی وب ماژول
     */
    private String MODULE_API;
    private String URL;
    private String SUBSCRIBE_GET_TOKEN_SEND_TO_ENDPOINT;
    private String FRONT_SESSION_ID;

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

    //private static final String tokenUsername = "0063234114";
    private static final String tokenUsername = "0083419004";
    private static String qrCode = "";

    /**
     * این متد مقادیر پیش فرض قبل از هر تست این کلاس تست را مقداردهی اولیه میکند
     */
    @BeforeEach
    void beforeEach() {

    }

    private CompletableFuture<BearerTokenDto> completableFuture;


    /**
     * این متد مقادیر پیش فرض را قبل از اجرای تمامی متدهای تست این کلاس مقداردهی اولیه میکند
     */
    @BeforeAll
    void beforeAll() {
        //تنظیم زبان لوکیل پروژه روی پارسی
        Locale.setDefault(new Locale("fa", "IR"));
        //مسیر پیش فرض ماژول
        this.MODULE_API = "http://localhost:" + PORT + "/api/v1.0/app-user-qr-code";
        //تنظیم مسیر پیش فرض سوکت
        this.URL = "ws://localhost:" + PORT + "/websocket";
        //تنظیم مسیر سابسکرایب
        this.SUBSCRIBE_GET_TOKEN_SEND_TO_ENDPOINT = "/topic/getToken";
        //مقدار ارسال شده در پارامتر
        FRONT_SESSION_ID = "/123456";
        completableFuture = new CompletableFuture<>();
    }

    private HttpHeaders getHeaders(String tokenUsername) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        if (!ObjectUtils.isEmpty(tokenUsername)) {
            SecurityUserReadDto securityUserReadDto = securityUserService.serviceReadByUsername(tokenUsername, true);
            AppUserDto appUserDto = appUserService.serviceReadById(securityUserReadDto.getAppUserId());
            BearerTokenDto bearerTokenDto = securityUserService.createBearerToken(securityUserReadDto.getSecurityUserId(), false, appUserDto, new HashMap<>());
            headers.set("Authorization", "Bearer " + bearerTokenDto.getAccessToken());
        }
        return headers;
    }


    /**
     * متد تولید qrcode
     */
    @Test
    @Order(1)
    void generateQrCodeTest() {
        try {
            //ارسال درخواست
            ResponseEntity<byte[]> response = this.testRestTemplate.exchange(this.MODULE_API + FRONT_SESSION_ID, HttpMethod.GET, new HttpEntity<>(""), new ParameterizedTypeReference<>() {
            });

            //بررسی پاسخ
            assertThat(response).isNotNull();
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getHeaders().getContentLength()).isGreaterThan(0);

            //تبدیل عکس بارکد به متن بارکد
            byte[] imageByteArray = response.getBody();
            BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(new BufferedImageLuminanceSource(ImageIO.read(new ByteArrayInputStream(imageByteArray)))));
            Result result = new MultiFormatReader().decode(binaryBitmap);

            qrCode = result.getText();
        } catch (Exception exception) {
            log.error("Exception: ", exception);
            fail("Exception ", exception);
        }
    }


    /**
     * متد چک کردنqrcode و تولید توکن بصورت سوکت
     */
    @Test
    @Order(5)
    void checkQrCodeToSendTokenTest() {
        try {

            WebSocketStompClient stompClient = new WebSocketStompClient(new SockJsClient(createTransportClient()));
            stompClient.setMessageConverter(new MappingJackson2MessageConverter());

            StompSession stompSession = stompClient.connect(URL, new StompSessionHandlerAdapter() {
            }).get(1, SECONDS);

            stompSession.subscribe(SUBSCRIBE_GET_TOKEN_SEND_TO_ENDPOINT + FRONT_SESSION_ID, new StompFrameHandler() {
                @Override
                public Type getPayloadType(StompHeaders stompHeaders) {
                    return BearerTokenDto.class;
                }

                @Override
                public void handleFrame(StompHeaders stompHeaders, Object payload) {
                    completableFuture.complete((BearerTokenDto) payload);
                }
            });

            //ارسال درخواست
            ResponseEntity response = this.testRestTemplate.exchange(this.MODULE_API + "/" + qrCode, HttpMethod.POST, new HttpEntity<>(getHeaders(tokenUsername)), new ParameterizedTypeReference<Void>() {
            });
            //بررسی پاسخ
            assertThat(response).isNotNull();
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            BearerTokenDto bearerTokenDto = completableFuture.get(30, SECONDS);
            assertThat(bearerTokenDto).isNotNull();


        } catch (Exception exception) {
            log.error("Exception", exception);
            fail("Exception ", exception);
        }
    }

    private List<Transport> createTransportClient() {
        List<Transport> transports = new ArrayList<>(1);
        transports.add(new WebSocketTransport(new StandardWebSocketClient()));
        return transports;
    }

}
