package com.motaharinia.ms.iam.modules.appuserinvitationlog.orm;

import com.motaharinia.ms.iam.modules.appuser.persistence.orm.AppUser;
import com.motaharinia.msjpautility.entity.CustomEntity;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

/**
 * کلاس انتیتی لاگ کد معرف کاربر برنامه فرانت
 */
@Entity
@Table(name = "app_user_invitation_log")
@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
public class AppUserInvitationLog extends CustomEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     *انتیتی کاربر برنامه فرانت
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "app_user_id")
    private AppUser appUser;

    /**
     * شماره موبایلی که برایش کد معرف ارسال شده است
     */
    @Column(name = "mobile_no_to")
    private String  mobileNoTo;

}
