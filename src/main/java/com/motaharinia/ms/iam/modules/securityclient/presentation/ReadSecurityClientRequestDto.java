package com.motaharinia.ms.iam.modules.securityclient.presentation;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * @author eng.motahari@gmail.com<br>
 * کلاس مدل جستجوی کاربر کلاینت
 */
@Data
public class ReadSecurityClientRequestDto {
    /**
     *کلمه کاربری
     */
    @NotEmpty
    private String clientId;
    /**
     *رمز عبور
     */
    private String clientSecret;
}
