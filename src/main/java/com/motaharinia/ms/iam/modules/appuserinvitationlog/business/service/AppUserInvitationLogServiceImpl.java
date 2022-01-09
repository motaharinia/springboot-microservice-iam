package com.motaharinia.ms.iam.modules.appuserinvitationlog.business.service;


import com.motaharinia.ms.iam.modules.appuser.persistence.orm.AppUser;
import com.motaharinia.ms.iam.modules.appuserinvitationlog.orm.AppUserInvitationLog;
import com.motaharinia.ms.iam.modules.appuserinvitationlog.orm.AppUserInvitationLogRepository;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * کلاس پیاده سازی سرویس  لاگ کد معرف کاربر برنامه فرانت
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class AppUserInvitationLogServiceImpl implements AppUserInvitationLogService {

    private final AppUserInvitationLogRepository appUserInvitationLogRepository;

    public AppUserInvitationLogServiceImpl(AppUserInvitationLogRepository appUserInvitationLogRepository) {
        this.appUserInvitationLogRepository = appUserInvitationLogRepository;
    }


    /**
     * متد ثبت لاگ کد معرف
     *
     * @param appUser    انتیتی کاربر برنامه فرانت
     * @param mobileNoTo شماره موبایلی که برایش کد معرف ارسال میشود
     */
    @Override
    public void serviceCreate(@NotNull AppUser appUser, @NotNull String mobileNoTo) {
        AppUserInvitationLog appUserInvitationLog = new AppUserInvitationLog();
        appUserInvitationLog.setAppUser(appUser);
        appUserInvitationLog.setMobileNoTo(mobileNoTo);
        appUserInvitationLogRepository.save(appUserInvitationLog);
    }

    /**
     * متد گرفتن تعداد لاگ های ثبت شده برای کاربر برنامه فرانت
     *
     * @param appUserId آیدی کاربر برنامه فرانت
     * @return خروجی:تعداد لاگهای ثبت شده برای کاربر برنامه فرانت
     */
    @Override
    public Integer serviceCountLogByAppUser(@NotNull Long appUserId) {
        Optional<Integer> countOptional = appUserInvitationLogRepository.readIdByAppUserId(appUserId);
        return countOptional.orElse(0);
    }
}
