package com.motaharinia.ms.iam.modules.securityuser.business.service;

import com.motaharinia.ms.iam.config.security.oauth2.dto.BearerTokenDto;
import com.motaharinia.ms.iam.config.security.oauth2.dto.LoggedInUserDto;
import com.motaharinia.ms.iam.modules.securityuser.business.enumeration.SecurityTokenInvalidTypeEnum;
import com.motaharinia.ms.iam.modules.securityuser.business.enumeration.SecurityUserInvalidTokenEnum;
import com.motaharinia.ms.iam.modules.securityuser.presentation.securityusertoken.SecurityUserTokenDto;
import com.motaharinia.msjpautility.page.CustomPageResponseDto;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Pageable;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Set;

/**
 * @author eng.motahari@gmail.com<br>
 * کلاس اینترفیس سرویس توکن امنیت
 */

public interface SecurityUserTokenService {


    /**
     * جستجو توکن امنیت از تاریخ تا تاریخ موردنظر
     *
     * @param fromDate از تاریخ
     * @param toDate   تا تاریخ
     * @param pageable اطلاعات صفحه بندی
     * @return CustomPageResponseDto<SecurityTokenDto>
     */
    CustomPageResponseDto<SecurityUserTokenDto> readAll(@NotNull LocalDateTime fromDate, @NotNull LocalDateTime toDate, @NotNull Pageable pageable);

    /**
     * متد تولید توکن برای فرانت
     *
     * @param loggedInUserDto        مدل کاربر لاگین شده
     * @param rememberMe             آیا به خاطر بماند؟
     * @param additionalClaimHashMap هش مپ اطلاعات دیگر مورد نیاز در توکن
     * @param referenceId            زمانی که توکن جدید تولید میشود اگر از توکن دیگری استفاده کرده باشد آیدی توکن استفاده شده در این فیلد قرار میگیرد
     * @param isFront            آیا توکن برای فرانت است؟
     * @return خروجی:مدل تولید توکن احراز هویت
     */
    @NotNull
    BearerTokenDto createBearerToken(LoggedInUserDto loggedInUserDto, boolean rememberMe, HashMap<String, Object> additionalClaimHashMap, Long referenceId,Boolean isFront);


    /**
     * ایجاد توکن جدید
     *
     * @param refreshToken       رفرش توکن
     * @param httpServletRequest سرولت ریکوئست
     * @return خروجی:مدل تولید توکن احراز هویت
     */
    BearerTokenDto renewToken(String refreshToken, HttpServletRequest httpServletRequest);

    /**
     * مشاهده سشن های فعال کاربر لاگین شده
     * @param pageable اطلاعات صفحه بندی
     * @return CustomPageResponseDto<SecurityTokenDto> خروجی: لیست مدل توکن امنیت
     */
    CustomPageResponseDto<SecurityUserTokenDto> readAllActiveSessionByCurrentUser(Pageable pageable);

    /**
     * متد kill کردن رفرش توکن
     *
     * @param refreshToken رفرش توکن
     */
    void terminate(String refreshToken);

    /**
     * خارج شدن از حساب کاربری
     *
     * @param httpServletRequest سرولت ریکوئست
     */
    void logout(HttpServletRequest httpServletRequest);

    /**
     * متد غیر فعال کردن توکن با کلمه کاربری
     * این متد بصورت درون سرویسی و از ماژول هایی مانند AppUser , BackUse,SecurityRole,SecurityPermission در زمان غیرفعال شدن یا حذف شدن فراخوانی میشوند
     * @param usernameSet لیست کلمه کاربری
     * @param securityTokenInvalidTypeEnum علت غیرفعال شدن توکن
     * @param securityUserInvalidTokenEnum کدام توکن کاربر باید غیرفعال شوند؟ توکن های بک کاربر یا توکن های فرانت کاربر یا هردو
     */
    void serviceInvalid(Set<String> usernameSet, SecurityTokenInvalidTypeEnum securityTokenInvalidTypeEnum, SecurityUserInvalidTokenEnum securityUserInvalidTokenEnum);

    /**
     * غیرفعال کردن توکن هایی که  تاریخ انقضای توکنشان به اتمام رسیده است
     */
    void scheduleInvalidRefreshTokenByExpiration();

    /**
     * به روز رسانی تعداد کاربران آنلاین در ردیس
     *  زمانی که توکن جدید ایجاد میشود در ردیس ثبت میشود و زمانی که توکنی غیرفعال میشود از ردیس حذف میشود
     * @param username نام کاربری
     * @param id شناسه SecurityToken
     * @param expiredAt تاریخ انقضا
     * @param isCreate آیا عملیات ثبت در ردیس انجام شود یا حذف از ردیس؟
     */
    void updateCountOfOnlineUsers(@NotNull String username, @NotNull Long id, @NotNull LocalDateTime expiredAt, @NotNull Boolean isCreate);

    /**
     * متد اسکجل جهت پاک کردن توکن هایی که تاریخ انقضایشان (expiredAt) به اتمام رسیده است از ردیس
     */
    void scheduleReportOnlineUsers();
}
