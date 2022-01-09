package com.motaharinia.ms.iam.modules.securityuser.persistence.orm;
/**
 * انتیتی توکن امنیت
 */

import com.motaharinia.ms.iam.modules.securityuser.business.enumeration.SecurityTokenInvalidTypeEnum;
import com.motaharinia.msjpautility.entity.CustomEntity;
import com.motaharinia.msjpautility.entity.DbColumnDefinition;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "security_user_token" , uniqueConstraints = {
        @UniqueConstraint(columnNames = {"refresh_token"})})
@Getter
@Setter
@EqualsAndHashCode
public class SecurityUserToken extends CustomEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * کلمه کاربری
     */
    @Column(name = "username")
    private String username;

    /**
     * آدرس آی پی کاربر
     */
    @Column(name = "ip_address")
    private String ipAddress;

    /**
     * اکسس توکن
     */
    @Column(name = "access_token", columnDefinition = "TEXT")
    private String accessToken;

    /**
     * تاریخ صدور اکسس توکن
     */
    @Column(name = "issued_at" , columnDefinition = DbColumnDefinition.COLUMN_DEFINITION_TIMESTAMP)
    private LocalDateTime issuedAt;

    /**
     * تاریخ انقضا اکسس توکن
     */
    @Column(name = "expired_at" , columnDefinition = DbColumnDefinition.COLUMN_DEFINITION_TIMESTAMP)
    private LocalDateTime expiredAt;

    /**
     * نام مرورگر
     */
    @Column(name = "browser")
    private String browser;

    /**
     * نسخه مرورگر
     */
    @Column(name = "browser_version")
    private String browserVersion;

    /**
     * سیستم عامل
     */
    @Column(name = "operating_system")
    private String operatingSystem;

    /**
     * نوع دستگاه
     */
    @Column(name = "device_type")
    private String deviceType;


    /**
     * رفرش توکن
     */
    @Column(name = "refresh_token")
    private String refreshToken;

    /**
     * تاریخ انقضای رفرش توکن
     */
    @Column(name = "refresh_token_expired_at", columnDefinition = DbColumnDefinition.COLUMN_DEFINITION_TIMESTAMP)
    private LocalDateTime refreshTokenExpiredAt;

    /**
     * تاریخ غیرفعال شدن
     */
    @Column(name = "invalid_date", columnDefinition = DbColumnDefinition.COLUMN_DEFINITION_TIMESTAMP)
    private LocalDateTime invalidDate;

    /**
     * دلیل غیرفعال شدن
     */
    @Column(name = "invalid_enum")
    @Enumerated(EnumType.STRING)
    SecurityTokenInvalidTypeEnum invalidEnum;

    /**
     * آیا رمز عبور به خاطر سپرده شود
     */
    @Column(name = "remember_me")
    private Boolean rememberMe;

    /**
     * آیدی ارجاع به خودش
     * زمانی که توکن جدید تولید میشود اگر از توکن دیگری استفاده کرده باشد آیدی توکن استفاده شده در این فیلد قرار میگیرد
     */
    @Column(name = "reference_id")
    private Long referenceId;

//    /**
//     * است یا نه؟ client credential آیا توکن مربوط به
//     * */
//    @Column(name = "is_client_credential")
//    private Boolean isClientCredential = false;

    /**
     *آیا توکن برای فرانت است؟
     */
    @Column(name = "is_front")
    private Boolean isFront = true;

}
