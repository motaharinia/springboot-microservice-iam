package com.motaharinia.ms.iam.modules.backuser.business;


import com.motaharinia.ms.iam.config.security.oauth2.dto.BackUserDto;
import com.motaharinia.ms.iam.config.security.oauth2.dto.BearerTokenDto;
import com.motaharinia.ms.iam.external.captchaotp.presentation.AspectUsernameDto;
import com.motaharinia.ms.iam.modules.backuser.business.enumeration.BackUserGridSearchTypeEnum;
import com.motaharinia.ms.iam.modules.backuser.presentation.changepassword.ChangePasswordResponseDto;
import com.motaharinia.ms.iam.modules.backuser.presentation.dto.*;
import com.motaharinia.ms.iam.modules.backuser.presentation.forgetpassword.ForgetPasswordCheckOtpRequestDto;
import com.motaharinia.ms.iam.modules.backuser.presentation.forgetpassword.ForgetPasswordCheckUsernameResponseDto;
import com.motaharinia.ms.iam.modules.backuser.presentation.signin.SigninCheckCredentialResponseDto;
import com.motaharinia.msjpautility.page.CustomPageResponseDto;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Pageable;

import java.util.List;


/**
 * @author eng.motahari@gmail.com<br>
 * کلاس اینترفیس سرویس کاربر بک
 */
public interface BackUserService {

    //-------------------------------------------------------
    //Find Methods Or Read Methods
    //------------------------------------------------------

    /**
     * * متد جستجوی کاربر بک با ایدی کاربر برنامه بک
     *
     * @param id آیدی کاربر برنامه بک
     * @return BackUserDto خروجی:مدل کاربر بک برنامه
     */
    BackUserDto serviceReadById(@NotNull Long id);


    //-------------------------------------------------------
    //sigin
    //------------------------------------------------------
    /**
     * متد گام اول احراز هویت(بررسی کلمه کاربری و رمز عبور)
     *
     * @param username کلمه کاربری (کد ملی شخص حقیقی / شناسه ملی سازمان)
     * @param password رمز عبور
     * @param aspectUsernameDto مدل مربوط به ست کردن نام کاربری برای @CaptchaCheck
     * @return خروجی: مدل پاسخ گام اول احراز هویت
     */
    @NotNull
    SigninCheckCredentialResponseDto signinCheckCredential(@NotNull String username, @NotNull String password, @NotNull AspectUsernameDto aspectUsernameDto);

    /**
     * متد گام دوم احراز هویت(بررسی کد تایید)
     *
     * @param username کلمه کاربری (کد ملی شخص حقیقی / شناسه ملی سازمان)
     * @param password رمز عبور
     * @param otp      کد تایید
     * @param rememberMe  مرا به خاطر بسپار
     * @return خروجی: مدل توکن
     */
    @NotNull
    BearerTokenDto signinCheckOtp(@NotNull String username, @NotNull String password, @NotNull String otp, @NotNull Boolean rememberMe);

    //-------------------------------------------------------
    //password (change,forget)
    //-------------------------------------------------------

    /**
     * متد تغییر رمز عبور
     *
     * @param currentPassword   رمز عبور فعلی
     * @param newPassword       رمز عبور جدید
     * @param newPasswordRepeat تکرار رمز عبور جدید
     * @return خروجی: مدل پاسخ تغییر رمز عبور
     */
    @NotNull
    ChangePasswordResponseDto changePassword(@NotNull String currentPassword, @NotNull String newPassword, @NotNull String newPasswordRepeat);

    /**
     * متد گام اول فراموشی رمز عبور (بررسی کلمه کاربری)
     *
     * @param username کلمه کاربری
     * @return خروجی: مدل پاسخ فراموشی رمز عبور (بررسی کلمه کاربری)
     */
    @NotNull
    ForgetPasswordCheckUsernameResponseDto forgetPasswordCheckUsername(@NotNull String username);

    /**
     * متد گام دوم فراموشی رمز عبور (بررسی کد تایید)
     *
     * @param dto مدل درخواست فراموشی رمز عبور
     * @param isFront   بررسی میکند که توکن برای کاربر برنامه تولید شود یا برای کاربر بک
     * @return خروجی: مدل توکن
     */
    @NotNull
    BearerTokenDto forgetPasswordCheckOtp(ForgetPasswordCheckOtpRequestDto dto, @NotNull Boolean isFront) ;

    //-------------------------------------------------------------
    //CRUD
    //-------------------------------------------------------------.

    /**
     * @param id شناسه کاربر برنامه بک
     * @return BackUserReadResponseDto خروجی:مدلی که شامل اطلاعات کامل از backUesr میباشد
     */
    BackUserReadResponseDto readById(Long id) ;

    /**
     * متد جستجو تمامی کاربران برنامه بک
     *
     * @param searchType نوع سرچ
     * @param searchValue مقدار سرچ
     * @param pageable     برای صفحه بندی
     * @return CustomPageResponseDto<BackUserReadResponseDto> لیست مدل کاربر برنامه بک
     */
    CustomPageResponseDto<BackUserReadResponseDto> readAll(BackUserGridSearchTypeEnum searchType , String searchValue, Pageable pageable);

    /**
     * متد جستجو با csv آیدی ها
     * @param ids
     * @param pageable
     * @return
     */
    List<BackUserMinimalReadResponseDto> readByIds(String ids, Pageable pageable);

    /**
     * متد ثبت کاربر برنامه بک
     *
     * @param dto     کلاس مدل ثبت کاربر برمامه بک
     * @return خروجی: مدل پاسخ کاربر برنامه بک
     */
    @NotNull
    BackUserResponseDto create(@NotNull BackUserCreateRequestDto dto) ;


    /**
     * متد ویرایش کاربر برنامه بک
     *
     * @param dto مدل ویرایش اطلاعات  کاربر برنامه بک
     * @return خروجی: مدل کاربر برنامه بک BackUserUpdateRequestDto
     */
    BackUserResponseDto update(@NotNull BackUserUpdateRequestDto dto);

    /**
     * فعال یا غیرفعال کردن کاربران
     * @param invalid فعال/ غیرفعال
     * @param ids رشته شناسه کاربران بک بصورت csv
     */
    void invalid(@NotNull Boolean invalid,@NotNull String ids);

    /**
     * متد حذف کاربر برنامه بک
     *
     * @param ids     رشته شناسه کاربران بک بصورت csv
     */
    void delete(@NotNull String ids);

}
