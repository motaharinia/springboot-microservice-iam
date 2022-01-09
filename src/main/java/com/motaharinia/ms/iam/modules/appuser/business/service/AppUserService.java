package com.motaharinia.ms.iam.modules.appuser.business.service;

import com.motaharinia.ms.iam.config.security.oauth2.dto.AppUserDto;
import com.motaharinia.ms.iam.config.security.oauth2.dto.BearerTokenDto;
import com.motaharinia.ms.iam.external.captchaotp.presentation.AspectUsernameDto;
import com.motaharinia.ms.iam.external.common.ratelimit.presentation.RateRequestDto;
import com.motaharinia.ms.iam.modules.appuser.business.enumeration.AppUserGridSearchTypeEnum;
import com.motaharinia.ms.iam.modules.appuser.presentation.changepassword.ChangePasswordResponseDto;
import com.motaharinia.ms.iam.modules.appuser.presentation.dto.*;
import com.motaharinia.ms.iam.modules.appuser.presentation.dto.create.AppUserCreateRequestDto;
import com.motaharinia.ms.iam.modules.appuser.presentation.forgetpassword.ForgetPasswordCheckOtpRequestDto;
import com.motaharinia.ms.iam.modules.appuser.presentation.forgetpassword.ForgetPasswordCheckUsernameResponseDto;
import com.motaharinia.ms.iam.modules.appuser.presentation.signin.SigninCheckCredentialResponseDto;
import com.motaharinia.ms.iam.modules.appuser.presentation.signup.SignupCheckCredentialRequestDto;
import com.motaharinia.ms.iam.modules.appuser.presentation.signup.SignupCheckCredentialResponseDto;
import com.motaharinia.ms.iam.modules.appuser.presentation.signup.SignupCheckOtpRequestOtpDto;
import com.motaharinia.msjpautility.page.CustomPageResponseDto;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Pageable;

import java.util.Set;


/**
 * @author eng.motahari@gmail.com<br>
 * کلاس اینترفیس سرویس کاربر فرانت برنامه
 */
public interface AppUserService {


    //-------------------------------------------------------
    //Find methods or read methods
    //-------------------------------------------------------

    /**
     * متد جستجو با شناسه ملی
     *
     * @param nationalCode شناسه ملی
     * @return خروجی: مدل جستجو شده
     */
    AppUserDto readByNationalCode(@NotNull String nationalCode);

    /**
     * * متد جستجوی کاربر برنامه با ایدی کاربر امنیت
     *
     * @param id خروجی : انتیتی کاربر برنامه فرانت
     * @return AppUserDto
     */
    AppUserDto serviceReadById(@NotNull Long id);

    /**
     *متد جستجوی کاربر برنامه با شناسه
     *
     * @param id آیدی کاربر برنامه فرانت
     * @return AppUserValidReadDto
     */
    AppUserValidReadDto readById1(@NotNull Long id);
    /**
     * متد جستجوی کاربر برنامه با لیست شناسه
     *
     * @param idSet لیست شناسه
     * @return Set<AppUserValidReadDto> خروجی: لیست مدل جستجو شده
     * @
     */
    Set<AppUserValidReadDto> readByIds(@NotNull Set<Long> idSet);

    /**
     * متد جستجوی کاربر برنامه با لیست شناسه ملی
     *
     * @param nationalCodeSet لیست شناسه ملی
     * @return Set<AppUserValidReadDto> خروجی: لیست مدل جستجو شده
     * @
     */
    Set<AppUserValidReadDto> readByNationalCodes(@NotNull Set<String> nationalCodeSet);

    /**
     * متد جستجوی کاربر برنامه با لیست شماره موبایل
     *
     * @param mobileNoSet لیست شناسه ملی
     * @return Set<AppUserValidReadDto> خروجی: لیست مدل جستجو شده
     * @
     */
    Set<AppUserValidReadDto> readByMobileNos(@NotNull Set<String> mobileNoSet);

    /**
     * @param id شناسه کاربر برنامه فرانت
     * @return AppUserReadResponseDto خروجی:مدلی که شامل اطلاعات کامل از appUser میباشد
     */
    AppUserReadResponseDto readById(@NotNull Long id) ;

    /**
     * متد جستجو تمامی کاربران برنامه فرانت
     *
     * @param searchType  نوع سرچ
     * @param searchValue مقدار سرچ
     * @param pageable    برای صفحه بندی
     * @return CustomPageResponseDto<AppUserReadResponseDto> لیست مدل کاربر برنامه فرانت
     */
    CustomPageResponseDto<AppUserReadResponseDto> readAll(AppUserGridSearchTypeEnum searchType, String searchValue, Pageable pageable);

    /**
     *متد گرفتن تعداد کل کاربران برانامه فرانت
     *
     * @return خروجی: مدل پاسخ شامل تعداد کل کاربران
     */
    AppUserTotalCountResponseDto readTotalCount();

    /**
     *  متد گرفتن اشخاصی که در روز و ماه جاری تولدشان هست
     * @return Set<AppUserAnnualPoint> مدل پاسخ
     */
    Set<AppUserAnnualPointDto> readAllByDateOfBirth();

    /**
     *  متد گرفتن اشخاصی که در روز و ماه جاری ثبت نام کرده اند
     * @return Set<AppUserAnnualPoint> مدل پاسخ
     */
    Set<AppUserAnnualPointDto> readAllByDateOfSignUp();

    //-------------------------------------------------------
    //sign up
    //-------------------------------------------------------


    /**
     * متد گام اول ثبت نام(بررسی کلمه کاربری و  رمزعبور)
     *
     * @param dto  کلاس مدل درخواست گام اول ثبت نام(بررسی کلمه کاربری و  رمز عبور)
     * @param aspectUsernameDto مدل مربوط به ست کردن نام کاربری برای @CaptchaCheck
     * @return خروجی: مدل پاسخ گام دوم ثبت نام(بررسی رمزعبور)
     */
    @NotNull
    SignupCheckCredentialResponseDto signupCheckCredential(@NotNull SignupCheckCredentialRequestDto dto, @NotNull AspectUsernameDto aspectUsernameDto) ;

    /**
     * متد گام دوم ثبت نام(بررسی کد تایید داخلی)
     *
     * @param dto     کلاس مدل درخواست گام دوم ثبت نام
     * @return خروجی: مدل توکن
     */
    @NotNull
    BearerTokenDto signupCheckOtp(@NotNull SignupCheckOtpRequestOtpDto dto);

    //-------------------------------------------------------
    //sign in
    //-------------------------------------------------------

    /**
     * متد گام اول احراز هویت(بررسی کلمه کاربری و رمز عبور)
     *
     * @param rateRequestDto مدل درخواست برای بررسی محدودیت بازدید
     * @param username کلمه کاربری (کد ملی شخص حقیقی / شناسه ملی سازمان)
     * @param password رمز عبور
     * @return خروجی: مدل پاسخ گام اول احراز هویت
     */
    @NotNull
    SigninCheckCredentialResponseDto signinCheckCredential(@NotNull RateRequestDto rateRequestDto, @NotNull String username, @NotNull String password);

    /**
     * متد گام دوم احراز هویت(بررسی کد تایید)
     *
     * @param username کلمه کاربری (کد ملی شخص حقیقی / شناسه ملی سازمان)
     * @param password رمز عبور
     * @param otp      کد تایید
     * @return خروجی: مدل توکن
     */
    @NotNull
    BearerTokenDto signinCheckOtp(@NotNull AspectUsernameDto aspectUsernameDto, @NotNull String username, @NotNull String password, @NotNull String otp, @NotNull Boolean rememberMe) ;

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
     * @param isFront           بررسی میکند که توکن برای کاربر برنامه تولید شود یا برای کاربر فرانت
     * @return خروجی: مدل توکن
     */
    @NotNull
    BearerTokenDto forgetPasswordCheckOtp(@NotNull AspectUsernameDto aspectUsernameDto, ForgetPasswordCheckOtpRequestDto dto, @NotNull Boolean isFront) ;

    //-------------------------------------------------------------
    //invite friends
    //-------------------------------------------------------------
    /**
     *متد دعوت دوستان به ثبت نام در سامانه با استفاده از کد دعوت خود
     * @param dto  کلاس مدل دعوت از دوستان
     * @return Boolean
     */
    Boolean inviteFriendByInvitationCode(@NotNull InviteFriendRequestDto dto);

    //-------------------------------------------------------------
    //updateProfile
    //-------------------------------------------------------------
    /**
     * متد ویرایش اطلاعات پروفایل کاربر برنامه فرانت توسط خودش
     *
     * @param dto مدل ویرایش اطلاعات  کاربر برنامه فرانت
     * @return خروجی: مدل کاربر برنامه فرانتAppUserResponseDto
     */
    @NotNull AppUserResponseDto updateProfile(@NotNull AppUserUpdateProfileRequestDto dto);


    /**
     * متد ثبت کاربر برنامه فرانت
     *
     * @param dto        کلاس مدل ثبت کاربر برمامه ثبت
     * @param sendInvitationCode        آیا کد دعوت برای کاربر sms بشود یا نه
     * @return خروجی: مدل پاسخ کاربر برنامه ثبت
     */
    @NotNull AppUserResponseDto create(@NotNull AppUserCreateRequestDto dto, @NotNull Boolean sendInvitationCode) ;

    /**
     * متد ویرایش کاربر برنامه فرانت
     *
     * @param dto مدل ویرایش اطلاعات  کاربر برنامه فرانت
     * @return خروجی: مدل کاربر برنامه فرانتAppUserResponseDto
     */
    @NotNull AppUserResponseDto update(@NotNull AppUserUpdateRequestDto dto);


    /**
     * فعال یا غیرفعال کردن کاربران
     * @param invalid فعال/ غیرفعال
     * @param ids رشته شناسه کاربران فرانت بصورت csv
     */
    void invalid(@NotNull Boolean invalid,@NotNull String ids);

}
