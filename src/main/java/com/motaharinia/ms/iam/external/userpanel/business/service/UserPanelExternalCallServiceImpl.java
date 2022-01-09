package com.motaharinia.ms.iam.external.userpanel.business.service;

import com.motaharinia.ms.iam.external.captchaotp.business.service.CaptchaOtpExternalServiceImpl;
import com.motaharinia.ms.iam.external.userpanel.presentation.dto.DashboardDto;
import com.motaharinia.msutility.custom.customdto.ClientResponseDto;
import com.motaharinia.msutility.tools.rest.RestTools;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

@Slf4j
@Service
public class UserPanelExternalCallServiceImpl implements UserPanelExternalCallService {

    private final RestTemplate restTemplate;
    private final Environment environment;

    @Value("${app.ms-user-panel.base-url}")
    private String userPanelBaseName;


    public UserPanelExternalCallServiceImpl(@LoadBalanced RestTemplate restTemplate, Environment environment) {
        this.restTemplate = restTemplate;
        this.environment = environment;
    }

    /**
     * این متد هدر پیش فرض برای درخواست rest را تولید میکند
     *
     * @return خروجی: هدر پیش فرض برای درخواست rest
     */
    private HttpHeaders getHeaders() {
        //ساخت هدر درخواست
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        return httpHeaders;
    }

    /**
     * متد ارسال تعداد کاربر برنامه فرانت آآنلاین در سامانه
     *
     * @param dashboardDto کلاس مدل داشبورد
     * @return DashboardDto خروجی: کلاس مدل داشبورد
     */
    @Override
    public DashboardDto dashboard(@NotNull DashboardDto dashboardDto) {
        String requestUrl =  userPanelBaseName + environment.getRequiredProperty("app.ms-user-panel.dashboard");
        //فراخوانی سرویس
        ClientResponseDto<DashboardDto> response = RestTools.call(restTemplate, HttpMethod.POST, requestUrl, "REQ-5008", this.getHeaders(), dashboardDto, new ParameterizedTypeReference<>() {
        }, CaptchaOtpExternalServiceImpl.class);
        return response.getData();
    }
}
