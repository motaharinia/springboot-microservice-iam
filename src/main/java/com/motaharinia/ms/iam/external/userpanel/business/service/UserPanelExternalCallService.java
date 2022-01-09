package com.motaharinia.ms.iam.external.userpanel.business.service;

import com.motaharinia.ms.iam.external.userpanel.presentation.dto.DashboardDto;
import org.jetbrains.annotations.NotNull;

/**
 * کلاس سرویس پروژه مدیریت کاربران
 */
public interface UserPanelExternalCallService {
    /**
     * متد ارسال تعداد کاربر برنامه فرانت آآنلاین در سامانه
     *
     * @param dashboardDto  کلاس مدل داشبورد
     * @return DashboardDto خروجی: کلاس مدل داشبورد
     */
    DashboardDto dashboard(@NotNull DashboardDto dashboardDto);

}
