package com.motaharinia.ms.iam.modules.dev.presentation.backcall;

import com.motaharinia.ms.iam.config.security.oauth2.dto.BearerTokenDto;
import com.motaharinia.ms.iam.external.co.CaptchaOtpConsumer;
import com.motaharinia.ms.iam.modules.appuser.business.service.AppUserService;
import com.motaharinia.ms.iam.modules.backuser.business.BackUserService;
import com.motaharinia.ms.iam.modules.securityuser.business.service.SecurityUserService;
import com.motaharinia.ms.iam.modules.securityuser.persistence.orm.SecurityUser;
import com.motaharinia.msutility.custom.customdto.ClientResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@RestController
@Slf4j
@RequestMapping("/api/v1.0/back/dev")
public class DevController {

    @Autowired
    private CaptchaOtpConsumer captchaOtpConsumer;

    @Value("${app.security.test-activated:false}")
    private boolean securityTestActivated;

    private final AppUserService appUserService;
    private final BackUserService backUserService;
    private final SecurityUserService securityUserService;
    private final DiscoveryClient discoveryClient;

    public DevController(AppUserService appUserService, BackUserService backUserService, SecurityUserService securityUserService, DiscoveryClient discoveryClient) {
        this.appUserService = appUserService;
        this.backUserService = backUserService;
        this.securityUserService = securityUserService;
        this.discoveryClient = discoveryClient;
    }


    /**
     * call in ( userpanel(IamExternalCallService) )
     */
    @GetMapping("/app-user/token/{username}")
    public ClientResponseDto<BearerTokenDto> appUserToken(@PathVariable String username) {
        if (!securityTestActivated) {
            return null;
        }

        Optional<SecurityUser> securityUserOptional = securityUserService.serviceReadByUsernameForFrontOptional(username);
        if(securityUserOptional.isPresent()){
            SecurityUser securityUser=securityUserOptional.get();
            return new ClientResponseDto<>( securityUserService.createBearerToken(securityUser.getId(),false,appUserService.serviceReadById(securityUser.getAppUserId()),new HashMap<>()),"");
        }else{
            return null;
        }

    }

    /**
     * call in ( userpanel(IamExternalCallService) )
     */
    @GetMapping("/back-user/token/{username}")
    public ClientResponseDto<BearerTokenDto> backUserToken(@PathVariable String username) {
        if (!securityTestActivated) {
            return null;
        }

        Optional<SecurityUser> securityUserOptional = securityUserService.serviceReadByUsernameForBackOptional(username);
        if(securityUserOptional.isPresent()){
            SecurityUser securityUser=securityUserOptional.get();
            return new ClientResponseDto<>(securityUserService.createBearerToken(securityUser.getId(),false,backUserService.serviceReadById(securityUser.getBackUserId()),new HashMap<>()), "");
        }else{
            return null;
        }

    }

    @GetMapping("/read-instance")
    public String readInstances() {
        StringBuilder stringBuilder=new StringBuilder();
        if (this.discoveryClient == null) {
            log.info("Discovery client is null");
        } else {
            log.info("Discovery client is not null");
            List<String> serviceIdList = this.discoveryClient.getServices();
            for (String serviceId : serviceIdList) {
                List<ServiceInstance> serviceInstanceList = this.discoveryClient.getInstances(serviceId);
                for (ServiceInstance serviceInstance : serviceInstanceList) {
                    stringBuilder.append("<hr><br>serviceId:"+ serviceId + " <br>serviceInstance.getInstanceId():" + serviceInstance.getInstanceId() +" <br>host-port: http://" + serviceInstance.getHost() + ":" + serviceInstance.getPort());
                }
            }
        }
        return stringBuilder.toString();
    }

    @GetMapping("/feign-call-test")
    public String feignCallTest() {
        return "captchaOtpConsumer.test():" + captchaOtpConsumer.test();
    }
}
