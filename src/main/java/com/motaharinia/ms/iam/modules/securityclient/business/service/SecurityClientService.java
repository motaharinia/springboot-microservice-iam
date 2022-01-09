package com.motaharinia.ms.iam.modules.securityclient.business.service;

import com.motaharinia.ms.iam.config.security.oauth2.dto.BearerTokenDto;
import com.motaharinia.ms.iam.modules.securityclient.persistence.orm.SecurityClient;
import com.motaharinia.ms.iam.modules.securityclient.presentation.SecurityClientRequestDto;
import com.motaharinia.ms.iam.modules.securityclient.presentation.SecurityClientResponseDto;
import com.motaharinia.ms.iam.modules.securityclient.presentation.UpdateSecurityClientRequestDto;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.oauth2.provider.ClientDetailsService;

/**
 * @author eng.motahari@gmail.com<br>
 * کلاس اینترفیس سرویس کاربر کلاینت
 */

public interface SecurityClientService extends ClientDetailsService {

    /**
     * client ثبت کاربر
     *
     * @param requestDto client مدل درخواست ثبت کاربر
     * @return خروجی: client مدل پاسخ
     */
    SecurityClientResponseDto create(SecurityClientRequestDto requestDto);

    /**
     * با شناسه و رمز عبور client جستجوی کاربر
     *
     * @param clientId     شناسه کاربر
     * @param clientSecret رمز عبور کاربر
     * @return خروجی: client مدل پاسخ
     */
    SecurityClientResponseDto readByIdAndSecret(@NotNull String clientId, @NotNull String clientSecret);

    /**
     * با شناسه و رمز عبور client جستجوی کاربر
     *
     * @param clientId     شناسه کاربر
     * @return خروجی: client اینتیتی پاسخ
     */
    SecurityClient serviceReadClientById(@NotNull String clientId);


    /**
     * client متد بروزرسانی کاربر
     *
     * @param requestDto client مدل درخواست بروزرسانی کاربر
     * @return خروجی: client مدل پاسخ
     */
    SecurityClientResponseDto update(UpdateSecurityClientRequestDto requestDto);

    /**
     * با شناسه و رمز عبور client حذف کاربر
     *
     * @param clientId     شناسه کاربر
     * @param clientSecret رمز عبور کاربر
     * @return خروجی: client مدل پاسخ
     */
    SecurityClientResponseDto deleteByIdAndSecret(@NotNull String clientId, @NotNull String clientSecret);

    /**
     * متد تولید اکسس توکن Bearer از شناسه کاربری و دسترسی های او
     *
     * @param grantType    نوع اعطا
     * @param scope        دامنه کاربر
     * @param clientId     شناسه کاربر
     * @param clientSecret رمز عبور کاربر
     * @return خروجی: مدل توکن
     */
    BearerTokenDto createBearerToken(@NotNull String clientId, @NotNull String clientSecret, @NotNull String grantType, @NotNull String scope);

}
