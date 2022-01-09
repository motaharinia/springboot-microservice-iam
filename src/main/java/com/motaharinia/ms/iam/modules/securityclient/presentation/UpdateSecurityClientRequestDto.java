package com.motaharinia.ms.iam.modules.securityclient.presentation;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.HashSet;
import java.util.Set;

/**
 * @author pourya <br>
 * کلاس مدل درخواست بروزرسانی کاربر کلاینت
 */
@Data
public class UpdateSecurityClientRequestDto {
    /**
     *عنوان کاربری
     */
    @NotEmpty
    private String clientTitle;
    /**
     *کلمه کاربری
     */
    @NotEmpty
    private String clientId;
    /**
     *کلمه کاربری جدید
     */
    @NotEmpty
    private String newClientId;
    /**
     *منابعی که این سرویس گیرنده می تواند به آنها دسترسی داشته باشد. در صورت خالی بودن تماس ها می تواند نادیده گرفته شود.
     */
    private Set<String> resourceIdSet = new HashSet<>();
    /**
     *این که آیا برای احراز هویت این کلاینت نیاز به رمز عبور است یا خیر.
     */
    private Boolean secretRequired = true;
    /**
     *رمز عبور
     */
    private String clientSecret;
    /**
     *رمز عبور جدید
     */
    private String newClientSecret;
    /**
     *اختیاراتی را که به کلاینت OAuth اعطا شده است برمی گرداند.
     */
    private Set<String> authoritySet = new HashSet<>();

}
