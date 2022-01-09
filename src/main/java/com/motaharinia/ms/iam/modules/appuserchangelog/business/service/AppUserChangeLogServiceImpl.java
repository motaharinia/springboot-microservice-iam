package com.motaharinia.ms.iam.modules.appuserchangelog.business.service;


import com.motaharinia.ms.iam.modules.appuser.persistence.orm.AppUser;
import com.motaharinia.ms.iam.modules.appuserchangelog.business.enumeration.AppUserChangeTypeEnum;
import com.motaharinia.ms.iam.modules.appuserchangelog.orm.AppUserChangeLog;
import com.motaharinia.ms.iam.modules.appuserchangelog.orm.AppUserChangeLogRepository;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * کلاس پیاده سازی سرویس  لاگ تغییرات اطلاعات کاربر برنامه فرانت
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class AppUserChangeLogServiceImpl implements AppUserChangeLogService {

    private final AppUserChangeLogRepository appUserChangeLogRepository;

    public AppUserChangeLogServiceImpl(AppUserChangeLogRepository appUserChangeLogRepository) {
        this.appUserChangeLogRepository = appUserChangeLogRepository;
    }

    /**
     * متد ثبت لاگ تغییرات
     *
     * @param appUser        انتیتی کاربر برنامه فرانت
     * @param changeTypeEnum نوع لاگ تغییرات کاربر برنامه فرانت
     * @param valueFrom      مقدار قبلی
     * @param valueTo        مقدار جدید
     */
    @Override
    public void serviceCreate(@NotNull AppUser appUser, @NotNull AppUserChangeTypeEnum changeTypeEnum, @NotNull String valueFrom, @NotNull String valueTo) {
        AppUserChangeLog appUserChangeLog = new AppUserChangeLog();
        appUserChangeLog.setAppUser(appUser);
        appUserChangeLog.setChangeTypeEnum(changeTypeEnum);
        appUserChangeLog.setValueFrom(valueFrom);
        appUserChangeLog.setValueTo(valueTo);
        appUserChangeLogRepository.save(appUserChangeLog);
    }

    /**
     * متد گرفتن تعداد لاگ های ثبت شده برای کاربر برنامه فرانت
     *
     * @param appUserId      آیدی کاربر برنامه فرانت
     * @param changeTypeEnum نوع لاگ تغییرات کاربر برنامه فرانت
     * @return خروجی:تعداد لاگهای ثبت شده برای کاربر برنامه فرانت
     */
    @Override
    public Integer serviceCountLogByAppUser(@NotNull Long appUserId, @NotNull AppUserChangeTypeEnum changeTypeEnum) {
        Optional<Integer> countOptional = appUserChangeLogRepository.readIdByAppUserId(appUserId, changeTypeEnum);
        return countOptional.orElse(0);
    }
}
