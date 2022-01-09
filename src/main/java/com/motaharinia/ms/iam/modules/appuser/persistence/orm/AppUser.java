package com.motaharinia.ms.iam.modules.appuser.persistence.orm;

import com.motaharinia.ms.iam.config.security.oauth2.enumeration.GenderEnum;
import com.motaharinia.ms.iam.modules.geo.persistence.orm.GeoCity;
import com.motaharinia.msjpautility.entity.CustomEntity;
import com.motaharinia.msjpautility.entity.DbColumnDefinition;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDate;

/**
 * کلاس انتیتی کاربر برنامه فرانت
 */
@Entity
@Table(name = "app_user", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"national_code", "mobile_no", "invitation_code"})})
@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
public class AppUser extends CustomEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * نام
     */
    @Column(name = "first_name", length = 30)
    private String firstName;

    /**
     * نام خانوادگی
     */
    @Column(name = "last_name", length = 30)
    private String lastName;

    /**
     * شناسه ملی(شناسه یونیک کاربر)
     */
    @Column(name = "national_code", length = 10)
    private String nationalCode;

    /**
     * تلفن همراه
     */
    @NotNull
    @Column(name = "mobile_no", nullable = false, length = 11)
    private String mobileNo;

    /**
     * آدرس پست الکترونیک
     */
    @Column(name = "email_address", length = 50)
    private String emailAddress;

    /**
     * جنسیت
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "gender")
    private GenderEnum gender;

    /**
     * تاریخ تولد
     */
    @Column(name = "date_of_birth" , columnDefinition = DbColumnDefinition.COLUMN_DEFINITION_DATE)
    private LocalDate dateOfBirth;


    /**
     * کد معرف شخصی یونیک که در زمان ثبت نام پر میشود
     */
    @Column(name = "invitation_code")
    private String invitationCode;

    /**
     * کد پستی
     */
    @Column(name = "postal_code")
    private String postalCode;
    /**
     * نشانی منزل
     */
    @Column(name = "address")
    private String address;
    /**
     * شهر
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "geo_city_id")
    private GeoCity geoCity;


}
