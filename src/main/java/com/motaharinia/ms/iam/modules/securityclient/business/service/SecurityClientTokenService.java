package com.motaharinia.ms.iam.modules.securityclient.business.service;

import com.motaharinia.ms.iam.config.security.oauth2.dto.BearerTokenDto;
import com.motaharinia.ms.iam.config.security.oauth2.dto.LoggedInClientDto;
import com.motaharinia.ms.iam.modules.securityclient.presentation.securityclienttoken.SecurityClientTokenDto;
import com.motaharinia.msjpautility.page.CustomPageResponseDto;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Pageable;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.HashMap;

/**
 * @author eng.motahari@gmail.com<br>
 * کلاس اینترفیس سرویس توکن امنیت
 */

public interface SecurityClientTokenService {


    /**
     * جستجو توکن امنیت از تاریخ تا تاریخ موردنظر
     *
     * @param fromDate از تاریخ
     * @param toDate   تا تاریخ
     * @param pageable اطلاعات صفحه بندی
     * @return CustomPageResponseDto<SecurityTokenDto>
     */
    CustomPageResponseDto<SecurityClientTokenDto> readAll(@NotNull LocalDateTime fromDate, @NotNull LocalDateTime toDate, @NotNull Pageable pageable);


    /**
     * متد تولید توکن برای فرانت
     *
     * @param loggedInClientDto      مدل کلاینت لاگین شده
     * @param additionalClaimHashMap هش مپ اطلاعات دیگر مورد نیاز در توکن
     * @param expiresIn              تاریخ انقضای توکن
     * @param refreshTokenExpiredAt  تاریخ انقضای ریفرش توکن
     * @return خروجی:مدل تولید توکن احراز هویت
     */
    @NotNull
    BearerTokenDto createClientBearerToken(LoggedInClientDto loggedInClientDto, HashMap<String, Object> additionalClaimHashMap, long expiresIn, long refreshTokenExpiredAt);

    /**
     * ایجاد توکن جدید
     *
     * @param refreshToken       رفرش توکن
     * @param httpServletRequest سرولت ریکوئست
     * @return خروجی:مدل تولید توکن احراز هویت
     */
    BearerTokenDto renewToken(String refreshToken, HttpServletRequest httpServletRequest);


    /**
     * غیرفعال کردن توکن هایی که  تاریخ انقضای توکنشان به اتمام رسیده است
     */
    void scheduleInvalidRefreshTokenByExpiration();

}
