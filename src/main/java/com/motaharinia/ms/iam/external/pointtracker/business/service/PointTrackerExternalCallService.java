package com.motaharinia.ms.iam.external.pointtracker.business.service;


import com.motaharinia.ms.iam.external.pointtracker.presentation.dto.AddPointBalanceDto;
import com.motaharinia.ms.iam.external.pointtracker.presentation.dto.AddPointResponseDto;
import com.motaharinia.ms.iam.modules.appuser.presentation.dto.GetUsersPointDto;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;

/**
 * کلاس سرویس پروژه مدیریت کاربران
 */
public interface PointTrackerExternalCallService {
    /**
     *  متد  گرفتن اطلاعات امتیاز و نوع سطح هر کاربر برنامه فرانت
     *
     * @param idSet لیست کدملی
     * @return List<GetUsersPointDto خروجی: لیست مدل جستجو شده
     */
    List<GetUsersPointDto> readUsersPoint(@NotNull Set<Long> idSet);

    /**
     * متد برای دادن امتیاز برای کاربران
     * @param dto مدل دادن امتیاز
     * @return AddPointBalanceDto کلاس مدل
     */
    AddPointResponseDto addPointToUser(@NotNull AddPointBalanceDto dto);

}
