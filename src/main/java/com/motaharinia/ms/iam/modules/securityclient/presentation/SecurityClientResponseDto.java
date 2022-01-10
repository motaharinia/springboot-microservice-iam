package com.motaharinia.ms.iam.modules.securityclient.presentation;

import lombok.Data;

import java.util.HashSet;
import java.util.Set;

/**
 * @author eng.motahari@gmail.com <br>
 * کلاس مدل کاربر کلاینت
 */
@Data
public class SecurityClientResponseDto {
    /**
     *شناسه
     */
    private Long id;
    /**
     *عنوان کاربری
     */
    private String clientTitle;
    /**
     *کلمه کاربری
     */
    private String clientId;
    /**
     *منابعی که این سرویس گیرنده می تواند به آنها دسترسی داشته باشد. در صورت خالی بودن تماس ها می تواند نادیده گرفته شود.
     */
    private Set<String> resourceIdSet = new HashSet<>();
    /**
     *این که آیا برای احراز هویت این کلاینت نیاز به رمز عبور است یا خیر.
     */
    private Boolean secretRequired = true;
    /**
     *این که آیا این سرویس گیرنده محدود به محدوده خاصی است یا خیر. اگر نادرست باشد ، محدوده درخواست احراز هویت نادیده گرفته می شود
     */
    private Boolean scoped;
    /**
     *محدوده این کلاینت. اگر سرویس گیرنده محدوده ای ندارد خالی است.
     */
    private Set<String> scopeSet = new HashSet<>();
    /**
     *انواع دسترسی ها که این کلاینت برای آنها مجاز است.
     */
    private Set<String> authorizedGrantTypeSet = new HashSet<>();
    /**
     *URI تغییر مسیر از پیش تعریف شده برای استفاده این کلاینت در حین اعطای دسترسی "authorization_code".
     */
    private Set<String> registeredRedirectUriSet = new HashSet<>();
    /**
     *اختیاراتی را که به کلاینت OAuth اعطا شده است برمی گرداند.
     */
    private Set<String> authoritySet = new HashSet<>();

}
