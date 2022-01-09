package com.motaharinia.ms.iam.config.security.audit;


import com.motaharinia.ms.iam.config.security.oauth2.resource.ResourceUserTokenProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;


/**
 * @author eng.motahari@gmail.com
 * کلاس تنظیمات بازرسی انتیتی ها
 */
@Configuration
public class AuditConfig {

    /**
     * کلاس مدیریت توکن ها در ResourceServer
     */
    private final ResourceUserTokenProvider resourceUserTokenProvider;

    public AuditConfig(ResourceUserTokenProvider resourceUserTokenProvider) {
        this.resourceUserTokenProvider = resourceUserTokenProvider;
    }

    @Bean
    public AuditorAware<Long> auditorAware() {
        return new CustomAuditAware(resourceUserTokenProvider);
    }
}
