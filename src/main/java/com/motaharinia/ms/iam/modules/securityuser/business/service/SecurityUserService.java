package com.motaharinia.ms.iam.modules.securityuser.business.service;


import com.motaharinia.ms.iam.config.security.oauth2.dto.AppUserDto;
import com.motaharinia.ms.iam.config.security.oauth2.dto.BackUserDto;
import com.motaharinia.ms.iam.config.security.oauth2.dto.BearerTokenDto;
import com.motaharinia.ms.iam.modules.securityuser.persistence.orm.SecurityUser;
import com.motaharinia.ms.iam.modules.securityuser.presentation.securityuser.SecurityUserReadDto;
import com.motaharinia.ms.iam.modules.securityuser.presentation.securityuser.SecurityUserUpdateDto;
import com.motaharinia.ms.iam.modules.securityuser.presentation.securityuser.roleandpermission.RoleAndPermissionReadResponseDto;
import com.motaharinia.ms.iam.modules.securityuser.presentation.securityuser.signup.SignupDto;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Optional;
import java.util.Set;

/**
 * @author eng.motahari@gmail.com<br>
 * کلاس اینترفیس سرویس کاربر امنیت
 */
public interface SecurityUserService {

    //-------------------------------------------------------
    //Find Methods Or Read Methods
    //------------------------------------------------------

    /**
     * جستجوی کاربر امنیت با کلمه کاربری و اینکه حتما کاربر فرانت سامانه باشد
     *
     * @param username کلمه کاربری
     * @return خروجی:انتیتی کاربر امنیت
     */
    Optional<SecurityUser> serviceReadByUsernameForFrontOptional(@NotNull String username);

    /**
     * جستجوی کاربر امنیت با کلمه کاربری و اینکه حتما کاربر بک سامانه باشد
     *
     * @param username کلمه کاربری
     * @return خروجی:انتیتی کاربر امنیت
     */
    Optional<SecurityUser> serviceReadByUsernameForBackOptional(@NotNull String username);

    /**
     * جستجوی کاربر امنیت با کلمه کاربری و اینکه حتما کاربر بک سامانه باشد
     *
     * @param username کلمه کاربری
     * @return خروجی:انتیتی کاربر امنیت
     */
    SecurityUser serviceReadByUsernameForBack(@NotNull String username);

    /**
     * @param username کلمه کاربری (کد ملی شخص حقیقی / شناسه ملی سازمان)
     * @param isFront  آیا لاگین برای فرانت است؟
     * @return خروجی:موبایل کاربر امنیت mobileNo
     */
    @NotNull String readByUsernameForGetMobileNo(@NotNull String username, @NotNull Boolean isFront);

    /**
     * متد بین سرویسی جستجو با  کلمه کاربری
     *
     * @param username کلمه  کاربری
     * @param isFront  آیا  برای فرانت است؟
     * @return خروجی: انتیتی جستجو شده
     */
    SecurityUserReadDto serviceReadByUsername(@NotNull String username, @NotNull Boolean isFront);

    /**
     * جستجوی کاربران امنیت با لیست آیدی های کاربر برنامه فرانت
     * @param appUserIdSet  لیست آیدی های کاربر برنامه فرانت
     * @return Set<String> لیست کلمه کاربری
     */
    Set<String> serviceReadUsernamesByAppUserIdSet(@NotNull Set<Long> appUserIdSet);

    /**
     * جستجوی کاربران امنیت با لیست آیدی های کاربر برنامه بک
     * @param backUserIdSet  لیست آیدی های کاربر برنامه بک
     * @return Set<String> لیست کلمه کاربری
     */
    Set<String> serviceReadUsernamesByBackUserIdSet(@NotNull Set<Long> backUserIdSet);
    //-------------------------------------------------------
    //signup
    //------------------------------------------------------

    /**
     * ثبت کاربر امنیت  و تولید توکن
     *
     * @param dto     مدل ثبت کاربر امنیت
     * @return خروجی: مدل توکن احراز هویت
     */
    BearerTokenDto serviceSignup(SignupDto dto) ;


    //-------------------------------------------------------
    //sign in
    //-------------------------------------------------------
    /**
     *  بررسی کاربر امنیت در لاگین-مرحله checkCredential
     *
     * @param username کلمه کاربری (کد ملی شخص حقیقی / شناسه ملی سازمان)
     * @param password رمز عبور
     * @param isFront آیا کاربر برنامه فرانت است؟
     */
    SecurityUserReadDto serviceSigninCheckCredential(@NotNull String username, @NotNull String password, @NotNull Boolean isFront);
    /**
     * بررسی کاربر امنیت-در مرحله checkOtp و تولید توکن
     *
     * @param username کلمه کاربری (کد ملی شخص حقیقی / شناسه ملی سازمان)
     * @param appUserDto  مدل کاربر برنامه فرانت
     * @param backUserDto مدل کاربر برنامه بک
     * @param rememberMe  مرا به خاطر بسپار
     * @return خروجی: مدل توکن احراز هویت
     */
    @NotNull
    BearerTokenDto serviceSigninGenerateToken(@NotNull String username, AppUserDto appUserDto, BackUserDto backUserDto, @NotNull Boolean rememberMe) ;

    //-------------------------------------------------------
    //password (change,forget)
    //-------------------------------------------------------

    /**
     * متد تغییر رمز عبور برای کاربری که لاگین است
     *
     * @param currentPassword رمز عبور فعلی
     * @param newPassword     رمز عبور جدید
     * @return خروجی: کلمه کاربری
     */
    @NotNull
    String serviceChangePassword(@NotNull String currentPassword, @NotNull String newPassword);

    /**
     * فراموشی رمز عبور  و تولید توکن
     *
     * @param username    کلمه کاربری (کد ملی شخص حقیقی / شناسه ملی سازمان)
     * @param newPassword رمز عبور جدید
     * @param appUserDto     مدل کاربر برنامه فرانت
     * @param backUserDto     مدل کاربر برنامه بک
     * @param rememberMe  مرا به خاطر بسپار
     * @return خروجی: مدل توکن احراز هویت
     */
    @NotNull BearerTokenDto serviceForgetPassword(@NotNull String username, @NotNull String newPassword, AppUserDto appUserDto, BackUserDto backUserDto, @NotNull Boolean rememberMe) ;

    //-------------------------------------------------------
    //generate Token methods
    //-------------------------------------------------------

    /**
     * متد تولید توکن احراز هویت برای فرانت
     *
     * @param securityUserId           شناسه کاربر امنیت
     * @param rememberMe             به یاد داشتن
     * @param appUserDto             مدل کاربر فرانت برنامه
     * @param additionalClaimHashMap مپ کلیم های اضافی توکن
     * @return خروجی: مدل توکن احراز هویت
     */
    @NotNull
    BearerTokenDto createBearerToken(Long securityUserId, Boolean rememberMe, AppUserDto appUserDto, HashMap<String, Object> additionalClaimHashMap) ;


    /**
     * متد تولید توکن احراز هویت برای بک
     *
     * @param securityUserId           شناسه کاربر امنیت
     * @param rememberMe             به یاد داشتن
     * @param backUserDto            مدل کاربر بک برنامه
     * @param additionalClaimHashMap مپ کلیم های اضافی توکن
     * @return خروجی: مدل توکن احراز هویت
     */
    @NotNull
    BearerTokenDto createBearerToken(Long securityUserId, Boolean rememberMe, BackUserDto backUserDto, HashMap<String, Object> additionalClaimHashMap) ;

    // -------------------------------------------------------
    //CRUD
    //-------------------------------------------------------

    /**
     * ویرایش کاربر امنیت
     * @param dto   مدل ویرایش اطلاعات کاربر امنیت
     */
    void serviceUpdate(SecurityUserUpdateDto dto);

    /**
     * جستجو دسترسی ها و نقش کاربری کاربر برنامه بک
     *
     * @param backUserId شناسه کاربری کاربر برنامه بک
     * @return خروجی: مدل دسترسی ها و نقش های کاربری که برای کاربر برنامه فرانت یا بک UpdateRoleAndPermissionResponseDto
     */
    RoleAndPermissionReadResponseDto serviceReadRoleAndPermissionForBack(@NotNull Long backUserId);

    /**
     * حذف کاربر امنیت
     *
     * @param backUserId شناسه کاربری کاربر برنامه بک
     */
    void serviceDeleteForBack(@NotNull Long backUserId);

    /**
     * فعال و غیرفعال کردن کاربر امنیت
     *
     * @param appUserId شناسه کاربری کاربر برنامه فرانت
     * @param invalid    فعال یا غیرفعال
     */
    void serviceInvalidForFront(@NotNull Long appUserId, @NotNull Boolean invalid);

    /**
     * فعال و غیرفعال کردن کاربر امنیت
     *
     * @param backUserId شناسه کاربری کاربر برنامه بک
     * @param invalid    فعال یا غیرفعال
     */
    void serviceInvalidForBack(@NotNull Long backUserId, @NotNull Boolean invalid);


}
