package com.motaharinia.ms.iam.schedule;

import com.motaharinia.ms.iam.modules.securityclient.business.service.SecurityClientTokenService;
import com.motaharinia.ms.iam.modules.securityuser.business.service.SecurityUserTokenService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduleService {

    private final SecurityUserTokenService securityUserTokenService;
    private final SecurityClientTokenService securityClientTokenService;

    public ScheduleService(SecurityUserTokenService securityUserTokenService, SecurityClientTokenService securityClientTokenService) {
        this.securityUserTokenService = securityUserTokenService;
        this.securityClientTokenService = securityClientTokenService;
    }

    //ساعت 1 بامداد فعال میشود
    @Scheduled(cron = "0 0 1 * * ?")
    public void cronJobSch() {
        securityUserTokenService.scheduleInvalidRefreshTokenByExpiration();
        securityClientTokenService.scheduleInvalidRefreshTokenByExpiration();
    }

    //هر 1 دقیقه فعال میشود
    @Scheduled(cron = "0 0/1 * * * ?")
    public void cronJobSch1() {
        securityUserTokenService.scheduleReportOnlineUsers();
    }
}
