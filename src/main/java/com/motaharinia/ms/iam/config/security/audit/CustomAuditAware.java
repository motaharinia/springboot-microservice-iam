package com.motaharinia.ms.iam.config.security.audit;

import com.motaharinia.ms.iam.config.security.oauth2.dto.LoggedInUserDto;
import com.motaharinia.ms.iam.config.security.oauth2.resource.ResourceUserTokenProvider;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.AuditorAware;

import java.util.Optional;

/**
 * @author eng.motahari@gmail.com<br>
 * کلاس بازرسی انتیتی که خروجی آن کاربر لاگین کننده است و در GenericEntity از آن استفاده میشود<br>
 * فیلدهایی از انتیتی های که انوتیشنهای CreatedBy و UpdatedBy با این خروجی برای بازرسی های آینده پر میشوند
 */

@Slf4j
public class CustomAuditAware implements AuditorAware<Long> {

    /**
     * کلاس مدیریت توکن ها در ResourceServer
     */
    private final ResourceUserTokenProvider resourceUserTokenProvider;

    public CustomAuditAware(ResourceUserTokenProvider resourceUserTokenProvider) {
        this.resourceUserTokenProvider = resourceUserTokenProvider;
    }

    @Override
    public @NotNull Optional<Long> getCurrentAuditor() {
        Optional<LoggedInUserDto> loggedInUserDtoOptional = resourceUserTokenProvider.getLoggedInDto();
        if (loggedInUserDtoOptional.isPresent()) {
            return Optional.of(loggedInUserDtoOptional.get().getId());
        } else {
            return Optional.of(0L);
        }

    }
}
