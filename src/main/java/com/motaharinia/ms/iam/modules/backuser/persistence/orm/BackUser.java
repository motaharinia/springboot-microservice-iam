package com.motaharinia.ms.iam.modules.backuser.persistence.orm;

import com.motaharinia.ms.iam.config.security.oauth2.enumeration.GenderEnum;
import com.motaharinia.msjpautility.entity.CustomEntity;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * کلاس انتیتی کاربر برنامه بک
 */
@Entity
@Table(name = "back_user", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"national_code"})})
@Getter
@Setter
@EqualsAndHashCode
public class BackUser extends CustomEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * نام
     */
    @NotNull
    @Column(name = "first_name", nullable = false)
    private String firstName;

    /**
     * نام خانوادگی
     */
    @NotNull
    @Column(name = "last_name", nullable = false)
    private String lastName;

    /**
     * شناسه ملی(شناسه یونیک کاربر)
     */
    @Column(name = "national_code")
    private String nationalCode;

    /**
     * تلفن همراه
     */
    @Column(name = "mobile_no")
    private String mobileNo;

    /**
     * آدرس پست الکترونیک
     */
    @Column(name = "email_address")
    private String emailAddress;

    /**
     * جنسیت
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "gender")
    private GenderEnum gender;

}
