package com.motaharinia.ms.iam.modules.appuserchangelog.orm;

import com.motaharinia.ms.iam.modules.appuser.persistence.orm.AppUser;
import com.motaharinia.ms.iam.modules.appuserchangelog.business.enumeration.AppUserChangeTypeEnum;
import com.motaharinia.msjpautility.entity.CustomEntity;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

/**
 * کلاس انتیتی لاگ تغییرات اطلاعات کاربر برنامه فرانت
 */
@Entity
@Table(name = "app_user_change_log")
@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
public class AppUserChangeLog extends CustomEntity implements Serializable {

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
     * نوع فیلدی که تغییر کرده است
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "change_type_enum")
    private AppUserChangeTypeEnum changeTypeEnum;

    /**
     * مقدار قبلی
     */
    @Column(name = "value_from")
    private String  valueFrom;

    /**
     * مقدار جدید
     */
    @Column(name = "value_to")
    private String  valueTo;

}
