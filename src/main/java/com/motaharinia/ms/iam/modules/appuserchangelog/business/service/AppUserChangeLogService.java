package com.motaharinia.ms.iam.modules.appuserchangelog.business.service;


import com.motaharinia.ms.iam.modules.appuser.persistence.orm.AppUser;
import com.motaharinia.ms.iam.modules.appuserchangelog.business.enumeration.AppUserChangeTypeEnum;
import org.jetbrains.annotations.NotNull;

/**
 * کلاس اینترفیس سرویس  لاگ تغییرات اطلاعات کاربر برنامه فرانت
 */
public interface AppUserChangeLogService {

    /**
     * متد ثبت لاگ تغییرات
     *
     * @param appUser انتیتی کاربر برنامه فرانت
     * @param changeTypeEnum نوع لاگ تغییرات کاربر برنامه فرانت
     * @param valueFrom مقدار قبلی
     * @param valueTo مقدار جدید
     */
    void serviceCreate(@NotNull AppUser appUser, @NotNull AppUserChangeTypeEnum changeTypeEnum, @NotNull String valueFrom, @NotNull String valueTo);

    /**
     * متد گرفتن تعداد لاگ های ثبت شده برای کاربر برنامه فرانت
     * @param appUserId آیدی کاربر برنامه فرانت
     @param changeTypeEnum نوع لاگ تغییرات کاربر برنامه فرانت
     * @return خروجی:تعداد لاگهای ثبت شده برای کاربر برنامه فرانت
     */
    Integer serviceCountLogByAppUser(@NotNull Long appUserId, @NotNull AppUserChangeTypeEnum changeTypeEnum);

}
