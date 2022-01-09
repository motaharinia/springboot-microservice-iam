package com.motaharinia.ms.iam.modules.securityclient.persistence.orm;

import com.motaharinia.msjpautility.entity.CustomEntity;
import com.motaharinia.msjpautility.entity.CustomStringSetAttributeConverter;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * @author eng.motahari@gmail.com<br>
 * کلاس انتیتی کاربر کلاینت
 */
@Entity
@Table(name = "security_client", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"cilent_id"})})
@Data
@EqualsAndHashCode(callSuper = true)
public class SecurityClient extends CustomEntity {
    /**
     * شناسه
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    /**
     *عنوان کاربری
     */
    @Column(name = "cilent_title")
    private String clientTitle;
    /**
     *کلمه کاربری
     */
    @Column(name = "cilent_id")
    private String clientId;
    /**
     *منابعی که این سرویس گیرنده می تواند به آنها دسترسی داشته باشد. در صورت خالی بودن تماس ها می تواند نادیده گرفته شود.
     */
    @Column(name = "resource_id_set_csv", columnDefinition = "TEXT")
    @Convert(converter = CustomStringSetAttributeConverter.class)
    private Set<String> resourceIdSet = new HashSet<>();
    /**
     *این که آیا برای احراز هویت این کلاینت نیاز به رمز عبور است یا خیر.
     */
    @Column(name = "secret_required")
    private Boolean secretRequired = true;
    /**
     *رمز عبور
     */
    @Column(name = "cilent_secret")
    private String clientSecret;
    /**
     *این که آیا این سرویس گیرنده محدود به محدوده خاصی است یا خیر. اگر نادرست باشد ، محدوده درخواست احراز هویت نادیده گرفته می شود
     */
    @Column(name = "scoped")
    private Boolean scoped;
    /**
     *محدوده این کلاینت. اگر سرویس گیرنده محدوده ای ندارد خالی است.
     */
    @Column(name = "scope_set_csv", columnDefinition = "TEXT")
    @Convert(converter = CustomStringSetAttributeConverter.class)
    private Set<String> scopeSet = new HashSet<>();
    /**
     *انواع دسترسی ها که این کلاینت برای آنها مجاز است.
     */
    @Column(name = "authorized_grant_type_set_csv", columnDefinition = "TEXT")
    @Convert(converter = CustomStringSetAttributeConverter.class)
    private Set<String> authorizedGrantTypeSet = new HashSet<>();
    /**
     *URI تغییر مسیر از پیش تعریف شده برای استفاده این کلاینت در حین اعطای دسترسی "authorization_code".
     */
    @Column(name = "registered_redirect_uri_set_csv", columnDefinition = "TEXT")
    @Convert(converter = CustomStringSetAttributeConverter.class)
    private Set<String> registeredRedirectUriSet = new HashSet<>();
    /**
     *اختیاراتی را که به کلاینت OAuth اعطا شده است برمی گرداند.
     */
    @Column(name = "authority_set_csv", columnDefinition = "TEXT")
    @Convert(converter = CustomStringSetAttributeConverter.class)
    private Set<String> authoritySet = new HashSet<>();
    /**
     *مدت اعتبار توکن دسترسی برای این کلاینت.
     */
    @Column(name = "access_token_validity_seconds")
    private Integer accessTokenValiditySeconds;
    /**
     *مدت اعتبار توکن رفرش برای این کلاینت.
     */
    @Column(name = "refresh_token_validity_seconds")
    private Integer refreshTokenValiditySeconds;
    /**
     * آیا کلاینت برای محدوده خاصی به تأیید کاربر نیاز دارد یا خیر.
     */
    @Column(name = "auto_approve")
    private Boolean autoApprove;
}
