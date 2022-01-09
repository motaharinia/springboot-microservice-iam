package com.motaharinia.ms.iam.modules.appuserinvitationlog.business.service;


import com.motaharinia.ms.iam.modules.appuser.persistence.orm.AppUser;
import org.jetbrains.annotations.NotNull;

/**
 * کلاس اینترفیس سرویس  لاگ کد معرف کاربر برنامه فرانت
 */
public interface AppUserInvitationLogService {

    /**
     * متد ثبت لاگ کد معرف
     *
     * @param appUser انتیتی کاربر برنامه فرانت
     * @param mobileNoTo شماره موبایلی که برایش کد معرف ارسال میشود
     */
    void serviceCreate(@NotNull AppUser appUser, @NotNull String mobileNoTo);

    /**
     * متد گرفتن تعداد لاگ های ثبت شده برای کاربر برنامه فرانت
     * @param appUserId آیدی کاربر برنامه فرانت
     * @return خروجی:تعداد لاگهای ثبت شده برای کاربر برنامه فرانت
     */
    Integer serviceCountLogByAppUser(@NotNull Long appUserId);

}
