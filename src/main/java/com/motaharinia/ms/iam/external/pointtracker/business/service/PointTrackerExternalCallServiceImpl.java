package com.motaharinia.ms.iam.external.pointtracker.business.service;

import com.motaharinia.ms.iam.external.captchaotp.business.service.CaptchaOtpExternalServiceImpl;
import com.motaharinia.ms.iam.external.pointtracker.presentation.dto.AddPointBalanceDto;
import com.motaharinia.ms.iam.external.pointtracker.presentation.dto.AddPointResponseDto;
import com.motaharinia.ms.iam.modules.appuser.presentation.dto.GetUsersPointDto;
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
import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class PointTrackerExternalCallServiceImpl implements PointTrackerExternalCallService {

    private final RestTemplate restTemplate;
    private final Environment environment;

    @Value("${app.ms-point-tracker.base-url}")
    private String pointTrackerBaseName;


    public PointTrackerExternalCallServiceImpl(@LoadBalanced RestTemplate restTemplate, Environment environment) {
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
     *  متد  گرفتن اطلاعات امتیاز و نوع سطح هر کاربر برنامه فرانت
     *
     * @param idSet لیست کدملی
     * @return List<GetUsersPointDto خروجی: لیست مدل جستجو شده
     */
    @Override
    public List<GetUsersPointDto> readUsersPoint(@NotNull Set<Long> idSet) {
        String requestUrl =  pointTrackerBaseName + environment.getRequiredProperty("app.ms-point-tracker.read-users-point");
        //فراخوانی سرویس
        ClientResponseDto<List<GetUsersPointDto>> response = RestTools.call(restTemplate, HttpMethod.POST, requestUrl, "REQ-9001", this.getHeaders(), idSet, new ParameterizedTypeReference<>() {
        }, CaptchaOtpExternalServiceImpl.class);
        return response.getData();
    }

    /**
     * متد برای دادن امتیاز برای کاربران
     * @param dto مدل دادن امتیاز
     * @return AddPointBalanceDto کلاس مدل
     */
    @Override
    public AddPointResponseDto addPointToUser(@NotNull AddPointBalanceDto dto) {
        String requestUrl =  pointTrackerBaseName + environment.getRequiredProperty("app.ms-point-tracker.add-point-to-users");
        //فراخوانی سرویس
        ClientResponseDto<AddPointResponseDto> response = RestTools.call(restTemplate, HttpMethod.POST, requestUrl, "REQ-9003", this.getHeaders(), dto, new ParameterizedTypeReference<>() {
        }, CaptchaOtpExternalServiceImpl.class);
        return response.getData();
    }
}
