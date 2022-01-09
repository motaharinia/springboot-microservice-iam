//package com.motaharinia.ms.iam.modules.appuser.presentation;
//
//import com.motaharinia.ms.iam.modules.appuser.business.service.AppUserQrCodeService;
//import BearerTokenDto;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.messaging.handler.annotation.DestinationVariable;
//import org.springframework.messaging.handler.annotation.Payload;
//import org.springframework.messaging.handler.annotation.SendTo;
//import org.springframework.stereotype.Controller;
//
///**
// * @author eng.motahari@gmail.com<br>
// * کلاس کنترلر QrCode کاربر برنامه فرانت
// */
//
//@Slf4j
//@Controller
//public class AppUserQrCodeSocketController {
//
//    private final AppUserQrCodeService appUserQrCodeService ;
//
//    public AppUserQrCodeSocketController(AppUserQrCodeService appUserQrCodeService) {
//        this.appUserQrCodeService = appUserQrCodeService;
//    }
//
//
//    //@MessageMapping("/getToken/{uuid}")
//    @SendTo("/topic/getToken/{frontSessionId}")
//    public BearerTokenDto getTokenSendTo(@DestinationVariable String frontSessionId, @Payload BearerTokenDto bearerTokenDto) {
//        log.info("--- AppUserQrCodeSocketController.getTokenSendTo frontSessionId:{} signinQrCodeDto:{}", frontSessionId , bearerTokenDto.toString());
//        return bearerTokenDto;
//    }
//}
