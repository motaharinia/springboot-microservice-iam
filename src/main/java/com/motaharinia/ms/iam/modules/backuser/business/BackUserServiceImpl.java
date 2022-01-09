package com.motaharinia.ms.iam.modules.backuser.business;


import com.motaharinia.ms.iam.config.security.oauth2.dto.BackUserDto;
import com.motaharinia.ms.iam.config.security.oauth2.dto.BearerTokenDto;
import com.motaharinia.ms.iam.config.security.oauth2.dto.LoggedInUserDto;
import com.motaharinia.ms.iam.config.security.oauth2.resource.ResourceUserTokenProvider;
import com.motaharinia.ms.iam.external.captchaotp.business.service.CaptchaCheck;
import com.motaharinia.ms.iam.external.captchaotp.business.service.CaptchaOtpExternalService;
import com.motaharinia.ms.iam.external.captchaotp.presentation.AspectUsernameDto;
import com.motaharinia.ms.iam.external.common.enumeration.SourceProjectEnum;
import com.motaharinia.ms.iam.external.notification.business.service.NotificationExternalService;
import com.motaharinia.ms.iam.modules.appuser.business.exception.AppUserException;
import com.motaharinia.ms.iam.modules.backuser.business.enumeration.BackUserGridSearchTypeEnum;
import com.motaharinia.ms.iam.modules.backuser.business.exception.BackUserException;
import com.motaharinia.ms.iam.modules.backuser.business.mapper.BackUserMapper;
import com.motaharinia.ms.iam.modules.backuser.persistence.orm.BackUser;
import com.motaharinia.ms.iam.modules.backuser.persistence.orm.BackUserRepository;
import com.motaharinia.ms.iam.modules.backuser.presentation.changepassword.ChangePasswordResponseDto;
import com.motaharinia.ms.iam.modules.backuser.presentation.dto.*;
import com.motaharinia.ms.iam.modules.backuser.presentation.forgetpassword.ForgetPasswordCheckOtpRequestDto;
import com.motaharinia.ms.iam.modules.backuser.presentation.forgetpassword.ForgetPasswordCheckUsernameResponseDto;
import com.motaharinia.ms.iam.modules.backuser.presentation.signin.SigninCheckCredentialResponseDto;
import com.motaharinia.ms.iam.modules.securityuser.business.enumeration.SecurityTokenInvalidTypeEnum;
import com.motaharinia.ms.iam.modules.securityuser.business.enumeration.SecurityUserInvalidTokenEnum;
import com.motaharinia.ms.iam.modules.securityuser.business.exception.SecurityUserException;
import com.motaharinia.ms.iam.modules.securityuser.business.service.SecurityUserService;
import com.motaharinia.ms.iam.modules.securityuser.business.service.SecurityUserTokenService;
import com.motaharinia.ms.iam.modules.securityuser.presentation.securityuser.SecurityUserCreateRequestDto;
import com.motaharinia.ms.iam.modules.securityuser.presentation.securityuser.SecurityUserReadDto;
import com.motaharinia.ms.iam.modules.securityuser.presentation.securityuser.SecurityUserUpdateDto;
import com.motaharinia.ms.iam.modules.securityuser.presentation.securityuser.signup.SignupDto;
import com.motaharinia.msjpautility.page.CustomPageResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Maryam
 * کلاس پیاده سازی سرویس کاربر بک
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class BackUserServiceImpl implements BackUserService {

    private final BackUserRepository backUserRepository;
    private final SecurityUserService securityUserService;
    private final BackUserMapper backUserMapper;
    private final NotificationExternalService notificationExternalService;
    private final CaptchaOtpExternalService captchaOtpExternalService;
    private final SecurityUserTokenService securityUserTokenService;
    private final ResourceUserTokenProvider resourceUserTokenProvider;

    private static final String BUSINESS_EXCEPTION_BACK_USER_ID_NOT_FOUND = "BUSINESS_EXCEPTION.BACK_USER_ID_NOT_FOUND";
    private static final String BUSINESS_EXCEPTION_BACK_USER_USERNAME_NOT_FOUND = "BUSINESS_EXCEPTION.BACK_USER_USERNAME_NOT_FOUND";
    private static final String BUSINESS_EXCEPTION_BACK_USER_IS_INVALID = "BUSINESS_EXCEPTION.BACK_USER_IS_INVALID";
    private static final String BUSINESS_EXCEPTION_BACK_USER_IS_EXISTED = "BUSINESS_EXCEPTION.BACK_USER_IS_EXISTED";
    private static final String BUSINESS_EXCEPTION_BACK_USER_REPEAT_PASSWORD_NOT_EQUAL = "BUSINESS_EXCEPTION.SECURITY_USER_REPEAT_PASSWORD_NOT_EQUAL";
    private static final String BUSINESS_EXCEPTION_BACK_USER_NOT_LOGGED_IN = "BUSINESS_EXCEPTION.USER_NOT_LOGGED_IN";
    private static final String BUSINESS_EXCEPTION_BACK_USER_NOT_ACCESS_HIMSELF = "BUSINESS_EXCEPTION.USER_NOT_ACCESS_HIMSELF";
    private static final String BUSINESS_EXCEPTION_BACK_USER_MUST_BE_INVALID = "BUSINESS_EXCEPTION.BACK_USER_MUST_BE_INVALID";


    private static final String NOTIFICATION_BACK_USER_SIGNIN_OTP = "NOTIFICATION.BACK_USER_SIGNIN_OTP";
    private static final String NOTIFICATION_BACK_USER_FORGET_PASSWORD_OTP = "NOTIFICATION.BACK_USER_FORGET_PASSWORD_OTP";

    //--------------------------keys
    private static final String OTP_MOBILE_SIGNIN_BACK_USER = "otp-mobile-signin-backUser-";
    private static final String OTP_MOBILE_FORGET_PASSWORD_BACK_USER = "otp-mobile-forgetpassword-backUser-";


    public BackUserServiceImpl(BackUserRepository backUserRepository, SecurityUserService securityUserService, BackUserMapper backUserMapper, NotificationExternalService notificationExternalService, CaptchaOtpExternalService captchaOtpExternalService, SecurityUserTokenService securityUserTokenService, ResourceUserTokenProvider resourceUserTokenProvider) {
        this.backUserRepository = backUserRepository;
        this.securityUserService = securityUserService;
        this.backUserMapper = backUserMapper;
        this.notificationExternalService = notificationExternalService;
        this.captchaOtpExternalService = captchaOtpExternalService;
        this.securityUserTokenService = securityUserTokenService;
        this.resourceUserTokenProvider = resourceUserTokenProvider;
    }

    @Value("${app.ms-captcha-otp.otp-length}")
    private Integer otpLength;

    @Value("${app.ms-captcha-otp.otp-ttl-seconds}")
    private Long otpTtlSeconds;

    /**
     * اگر مقدار true باشد کد فعالسازی در زمان تست توسعه دهندگان بدون نیاز به ارسال پیامک از طریق مدل خروجی داده میشود
     * و درخواستهای DevController فعال میشود
     */
    @Value("${app.security.test-activated:false}")
    private boolean securityTestActivated;

    //-------------------------------------------------------
    //Read Methods
    //------------------------------------------------------

    /**
     * * متد جستجوی کاربر بک با ایدی کاربر امنیت
     *
     * @param id آیدی کاربر امنیت
     * @return BackUserDto خروجی:مدل کاربر بک برنامه
     */
    @Override
    public BackUserDto serviceReadById(@NotNull Long id) {
        //جستجو کاربر بک
        return backUserMapper.toDto(backUserRepository.findById(id).orElseThrow(() -> new BackUserException(id.toString(), BUSINESS_EXCEPTION_BACK_USER_ID_NOT_FOUND, "id:" + id)));
    }


    //-------------------------------------------------------
    //signin
    //------------------------------------------------------

    /**
     * متد گام اول احراز هویت(بررسی کلمه کاربری و رمز عبور)
     *
     * @param username          کلمه کاربری (کد ملی شخص حقیقی / شناسه ملی سازمان)
     * @param password          رمز عبور
     * @param aspectUsernameDto مدل مربوط به ست کردن نام کاربری برای @CaptchaCheck
     * @return خروجی: مدل پاسخ گام اول احراز هویت
     */
    @Override
    //برای ۱۵ بار وارد کردن کپچا مدت ۱۰ دقیقه اجازه وارد کردن کپچا نداشته باشد کاربر باید ۱۰ دقیقه بلاک باشد
    @CaptchaCheck(tryCount = 15, tryTtlInMinutes = 10, banTtlInMinutes = 10)
    public @NotNull SigninCheckCredentialResponseDto signinCheckCredential(@NotNull String username, @NotNull String password, @NotNull AspectUsernameDto aspectUsernameDto) {

        //بررسی کاربر امنیت
        SecurityUserReadDto securityUserReadDto = securityUserService.serviceSigninCheckCredential(username, password, false);

        //بررسی غیرفعال بودن یا عدم نمایش بودن کاربر برنامه بک
        List<Object[]> backUserInvalidAndHidden = backUserRepository.readInvalidById(securityUserReadDto.getBackUserId());
        if (backUserInvalidAndHidden.isEmpty()) {
            throw new BackUserException(username, BUSINESS_EXCEPTION_BACK_USER_USERNAME_NOT_FOUND, "username:" + username);
        }
        if (((Boolean) backUserInvalidAndHidden.get(0)[0]) || ((Boolean) backUserInvalidAndHidden.get(0)[1])) {
            throw new BackUserException(username, BUSINESS_EXCEPTION_BACK_USER_IS_INVALID, "username:" + username);
        }

        //هنگامی که پیامک ارسال شود تا ۳ دقیقه زمان برای اکسپایر شدن داشته باشد و تا این تایم درخواست دیگری نباید برای ارسال پیامک زده شود
        //تولید و ارسال کد تایید موبایل به کاربر
        String otp = captchaOtpExternalService.otpCreate(SourceProjectEnum.MS_IAM, OTP_MOBILE_SIGNIN_BACK_USER + username, otpLength, otpTtlSeconds).getValue();
        notificationExternalService.send(SourceProjectEnum.MS_IAM, securityUserReadDto.getMobileNo(), NOTIFICATION_BACK_USER_SIGNIN_OTP + "::" + otp);

        //اگر به صورت تستی سامانه اجرا میشود کد تایید برای راحتی توسعه دهنده در پاسخ وب خروجی داده بشود در غیر این صورت خالی بشود
        if (!securityTestActivated) {
            otp = "";
        }

        return new SigninCheckCredentialResponseDto(otp);
    }

    /**
     * متد گام دوم احراز هویت(بررسی کد تایید)
     *
     * @param username   کلمه کاربری (کد ملی شخص حقیقی / شناسه ملی سازمان)
     * @param password   رمز عبور
     * @param otp        کد تایید
     * @param rememberMe مرا به خاطر بسپار
     * @return خروجی: مدل توکن
     */
    @Override
    public @NotNull BearerTokenDto signinCheckOtp(@NotNull String username, @NotNull String password, @NotNull String otp, @NotNull Boolean rememberMe) {

        //بررسی کد تایید
        captchaOtpExternalService.otpCheck(SourceProjectEnum.MS_IAM, OTP_MOBILE_SIGNIN_BACK_USER + username, otp, "signinCheckOtp", username, 200, 1, 1);

        //بررسی کاربر امنیت
        SecurityUserReadDto securityUserReadDto = securityUserService.serviceSigninCheckCredential(username, password, false);

        //جستجوی backUser
        BackUser backUser = backUserRepository.findById(securityUserReadDto.getBackUserId()).orElseThrow(() -> new BackUserException(username, BUSINESS_EXCEPTION_BACK_USER_USERNAME_NOT_FOUND, "username:" + username));

        //بررسی غیرفعال بودن یا عدم نمایش بودن کاربر برنامه بک
        if ((backUser.getInvalid()) || (backUser.getHidden())) {
            throw new BackUserException(username, BUSINESS_EXCEPTION_BACK_USER_IS_INVALID, "username:" + username);
        }

        //تبدیل انتیتی به مدل کاربر برنامه بک
        BackUserDto backUserDto = backUserMapper.toDto(backUser);

        //بررسی کاربر امنیت و تولید توکن
        return securityUserService.serviceSigninGenerateToken(username, null, backUserDto, rememberMe);
    }

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
    @Override
    public @NotNull ChangePasswordResponseDto changePassword(@NotNull String currentPassword, @NotNull String newPassword, @NotNull String newPasswordRepeat) {

        //بررسی صحت تکرار رمز عبور
        if (!(newPassword.equals(newPasswordRepeat))) {
            throw new SecurityUserException("", BUSINESS_EXCEPTION_BACK_USER_REPEAT_PASSWORD_NOT_EQUAL, "");
        }

        return new ChangePasswordResponseDto(securityUserService.serviceChangePassword(currentPassword, newPassword));
    }

    /**
     * متد گام اول فراموشی رمز عبور (بررسی کلمه کاربری)
     *
     * @param username کلمه کاربری
     * @return خروجی: مدل پاسخ فراموشی رمز عبور (بررسی کلمه کاربری)
     */
    @Override
    public @NotNull ForgetPasswordCheckUsernameResponseDto forgetPasswordCheckUsername(@NotNull String username) {
        //جستجو با کلمه کاربری و گرفتن موبایل
        String mobileNo = securityUserService.readByUsernameForGetMobileNo(username, false);

        //تولید و ارسال کد تایید موبایل به کاربر
        String otp = captchaOtpExternalService.otpCreate(SourceProjectEnum.MS_IAM, OTP_MOBILE_FORGET_PASSWORD_BACK_USER + username, otpLength, otpTtlSeconds).getValue();
        notificationExternalService.send(SourceProjectEnum.MS_IAM, mobileNo, NOTIFICATION_BACK_USER_FORGET_PASSWORD_OTP + "::" + otp);

        //اگر به صورت تستی سامانه اجرا میشود کد تایید برای راحتی توسعه دهنده در پاسخ وب خروجی داده بشود در غیر این صورت خالی بشود
        if (!securityTestActivated) {
            otp = "";
        }

        return new ForgetPasswordCheckUsernameResponseDto(otp);
    }

    /**
     * متد گام دوم فراموشی رمز عبور (ریست کردن رمز عبور یا کد تایید)
     *
     * @param dto     مدل درخواست فراموشی رمز عبور
     * @param isFront بررسی میکند که توکن برای کاربر برنامه تولید شود یا برای کاربر بک
     * @return خروجی: مدل توکن
     */
    @Override
    public @NotNull BearerTokenDto forgetPasswordCheckOtp(ForgetPasswordCheckOtpRequestDto dto, @NotNull Boolean isFront) {

        //بررسی صحت تکرار رمز عبور
        if (!dto.getNewPassword().equals(dto.getNewPasswordRepeat())) {
            throw new BackUserException(dto.getUsername(), BUSINESS_EXCEPTION_BACK_USER_REPEAT_PASSWORD_NOT_EQUAL, "username:" + dto.getUsername());
        }

        //بررسی کد تایید
        captchaOtpExternalService.otpCheck(SourceProjectEnum.MS_IAM, OTP_MOBILE_FORGET_PASSWORD_BACK_USER + dto.getUsername(), dto.getOtp(), "forgetPasswordCheckOtp", dto.getUsername(), 200, 1, 1);

        //جستجو آیدی کاربر برنامه بک با کلمه کاربری
        SecurityUserReadDto securityUserReadDto = securityUserService.serviceReadByUsername(dto.getUsername(), false);

        //ساختن مدل برای تولید توکن
        BackUserDto backUserDto = backUserMapper.toDto(backUserRepository.findById(securityUserReadDto.getBackUserId()).orElseThrow(() -> new BackUserException("", BUSINESS_EXCEPTION_BACK_USER_ID_NOT_FOUND, "")));

        //تغییر رمز عبور کاربر امنیت
        return securityUserService.serviceForgetPassword(dto.getUsername(), dto.getNewPassword(), null, backUserDto, dto.getRememberMe());
    }

    //-------------------------------------------------------------
    //CRUD
    //-------------------------------------------------------------


    /**
     * @param id شناسه کاربر برنامه بک
     * @return BackUserReadResponseDto خروجی:مدلی که شامل اطلاعات کامل از کاربر برنامه بک میباشد
     */
    @Override
    public BackUserReadResponseDto readById(Long id) {
        //جستجو کاربر برنامه
        BackUser backUser = backUserRepository.findById(id).orElseThrow(() -> new BackUserException(id.toString(), BUSINESS_EXCEPTION_BACK_USER_ID_NOT_FOUND, "id:" + id));
        //پر کردن اطلاعات کاربر برنامه
        BackUserReadResponseDto dto = backUserMapper.toBackUserReadResponseDto(backUser);
        //پر کردن نقش و دسترسی کاربر
        dto.setRoleAndPermissionDto(securityUserService.serviceReadRoleAndPermissionForBack(backUser.getId()));
        return dto;
    }

    /**
     * متد جستجو با csv آیدی ها
     *
     * @param ids      شناسه csv
     * @param pageable صفحه بندی
     * @return List<BackUserMinimalReadResponseDto> لیست مدل
     */
    @Override
    public List<BackUserMinimalReadResponseDto> readByIds(String ids, Pageable pageable) {
        Set<Long> idSet = Stream.of(ids.split(",")).map(Long::parseLong).collect(Collectors.toSet());
        List<BackUser> backUserList = backUserRepository.findByIdIn(idSet, pageable);
        return backUserList.stream().map(backUserMapper::toBackUserMinimalReadResponseDto).collect(Collectors.toList());
    }

    /**
     * متد جستجو تمامی کاربران برنامه بک
     *
     * @param searchType  نوع سرچ
     * @param searchValue مقدار سرچ
     * @param pageable    برای صفحه بندی
     * @return CustomPageResponseDto<BackUserReadResponseDto> لیست مدل کاربر برنامه بک
     */
    @Override
    public CustomPageResponseDto<BackUserReadResponseDto> readAll(BackUserGridSearchTypeEnum searchType, String searchValue, Pageable pageable) {
        Page<BackUser> backUserPage = null;
        if (!ObjectUtils.isEmpty(searchType) && !ObjectUtils.isEmpty(searchValue)) {
            switch (searchType) {
                case LASTNAME:
                    backUserPage = backUserRepository.findAllByLastNameContaining(searchValue, pageable);
                    break;
                case MOBILE_NO:
                    backUserPage = backUserRepository.findAllByMobileNoContaining(searchValue, pageable);
                    break;
                case NATIONAL_CODE:
                    backUserPage = backUserRepository.findAllByNationalCodeContaining(searchValue, pageable);
                    break;
            }
        } else {
            backUserPage = backUserRepository.findAll(pageable);
        }

        if (!ObjectUtils.isEmpty(backUserPage)) {
            Page<BackUserReadResponseDto> finalPage = backUserPage.map(backUser -> {
                //تبدیل انتیتی به مدل
                BackUserReadResponseDto dto = backUserMapper.toBackUserReadResponseDto(backUser);
                //ست کردن دسترسی ها و نقش های کاربری
                dto.setRoleAndPermissionDto(securityUserService.serviceReadRoleAndPermissionForBack(backUser.getId()));
                return dto;
            });
            return new CustomPageResponseDto<>(finalPage);
        }
        return null;

    }


    /**
     * متد ثبت کاربر برنامه بک
     *
     * @param dto کلاس مدل ثبت کاربر برمامه بک
     * @return خروجی: مدل پاسخ کاربر برنامه بک
     */
    @Override
    public @NotNull BackUserResponseDto create(@NotNull BackUserCreateRequestDto dto) {

        //بررسی تکراری نبودن شناسه ملی (کلمه کاربری)
        if (org.apache.commons.lang3.ObjectUtils.isNotEmpty(backUserRepository.readIdByNationalCode(dto.getUsername()))) {
            throw new BackUserException(dto.getUsername(), BUSINESS_EXCEPTION_BACK_USER_IS_EXISTED, "");
        }

        //ایجاد کاربر بک سامانه
        //کپی کردن اطلاعات مدل در انتیتی
        BackUser backUser = backUserMapper.toEntity(dto);
        backUserRepository.save(backUser);

        //ایجاد کاربر امنیت - توکن جدید تولید نمیشود
        securityUserService.serviceSignup(new SignupDto(
                new SecurityUserCreateRequestDto(dto.getUsername(), dto.getPassword(), dto.getBackUserDto().getMobileNo(), dto.getBackUserDto().getEmailAddress(), null, backUser.getId())
                , dto.getSecurityRoleIdSet(), dto.getSecurityPermissionIncludeIdSet(), null, null, false));

        return new BackUserResponseDto(backUser.getId());
    }

    /**
     * متد ویرایش کاربر برنامه بک
     *
     * @param dto مدل ویرایش اطلاعات  کاربر برنامه بک
     * @return خروجی: مدل کاربر برنامه بک BackUserUpdateRequestDto
     */
    @Override
    public BackUserResponseDto update(@NotNull BackUserUpdateRequestDto dto) {
        //جستجو کاربر برنامه
        BackUser backUser = backUserRepository.findById(dto.getId()).orElseThrow(() -> new BackUserException("", BUSINESS_EXCEPTION_BACK_USER_ID_NOT_FOUND, ""));

        //بررسی تکراری نبودن شناسه ملی (کلمه کاربری)
        Long id = backUserRepository.readIdByNationalCode(dto.getUsername());
        if (id != null && !id.equals(dto.getId())) {
            throw new AppUserException(dto.getUsername(), BUSINESS_EXCEPTION_BACK_USER_IS_EXISTED + "::" + dto.getUsername(), "");
        }


        //کپی کردن اطلاعات مدل در انتیتی
        backUserMapper.toEntity(dto, backUser);
        backUserRepository.save(backUser);

        securityUserService.serviceUpdate(new SecurityUserUpdateDto(dto.getBackUserDto().getMobileNo(), dto.getUsername(), dto.getPassword(), dto.getBackUserDto().getEmailAddress(), null, backUser.getId(), dto.getSecurityRoleIdSet(), dto.getSecurityPermissionIncludeIdSet()));

        return new BackUserResponseDto(backUser.getId());
    }


    /**
     * متد فعال یا غیرقعال کردن کاربران برنامه بک
     *
     * @param invalid فعال/ غیرفعال
     * @param ids     رشته شناسه کاربران بک بصورت csv
     */
    @Override
    public void invalid(@NotNull Boolean invalid, @NotNull String ids) {
        //تبدیل csv آیدیها به لیست
        Set<Long> idSet = Stream.of(ids.split(",")).map(Long::parseLong).collect(Collectors.toSet());
        //گرفتن شناسه کاربر بک لاگین شده
        LoggedInUserDto loggedInUserDto = resourceUserTokenProvider.getLoggedInDto().orElseThrow(() -> new BackUserException("", BUSINESS_EXCEPTION_BACK_USER_NOT_LOGGED_IN, ""));
        //بررسی جهت اینکه کاربر لاگین شده اجازه فعال یا غیرفعال کردن خودش را ندارد
        if (idSet.contains(loggedInUserDto.getBackUserId()))
            throw new BackUserException("", BUSINESS_EXCEPTION_BACK_USER_NOT_ACCESS_HIMSELF, "");


        //جستجوی کاربران بک با شناسه کاربری
        List<BackUser> backUserList = backUserRepository.findByIdIn(idSet, null);

        //اگر شناسه کاربری یافت نشد خطا صادر شود
        if (backUserList.isEmpty())
            throw new BackUserException("", BUSINESS_EXCEPTION_BACK_USER_ID_NOT_FOUND, "");

        // فعال یا غیرفعال شدن
        backUserList.forEach(backUser -> {
            backUser.setInvalid(invalid);
            backUserRepository.save(backUser);
            // فعال یا غیرقعال کردن کاربر امنیت
            securityUserService.serviceInvalidForBack(backUser.getId(), invalid);
        });


        //اگر غیرفعال میشود باید توکن کاربران بک موردنظر هم غیرفعال شوند
        if (invalid) {
            //جستجوی کلمه کاربری با شناسه کاربر بک جهت غیرفعال کردن توکن کاربرانی که غیرفعال میشوند
            securityUserTokenService.serviceInvalid(securityUserService.serviceReadUsernamesByBackUserIdSet(idSet), SecurityTokenInvalidTypeEnum.SECURITY_USER_INVALID, SecurityUserInvalidTokenEnum.JUST_BACK);
        }

    }

    /**
     * متد حذف کاربر برنامه بک
     *
     * @param ids رشته شناسه کاربران بک بصورت csv
     */
    @Override
    public void delete(@NotNull String ids) {
//        //جستجو کاربر برنامه بک با کد ملی
//        BackUser backUser = backUserRepository.findById(id).orElseThrow(() -> new BackUserException(id.toString(), BUSINESS_EXCEPTION_BACK_USER_ID_NOT_FOUND, "id:" + id));
//        backUserRepository.delete(backUser);
//        //حذف کاربر امنیت
//        securityUserService.serviceDeleteForBack(backUser.getId());

        //تبدیل csv آیدیها به لیست
        Set<Long> idSet = Stream.of(ids.split(",")).map(Long::parseLong).collect(Collectors.toSet());
        //گرفتن شناسه کاربر بک لاگین شده
        LoggedInUserDto loggedInUserDto = resourceUserTokenProvider.getLoggedInDto().orElseThrow(() -> new BackUserException("", BUSINESS_EXCEPTION_BACK_USER_NOT_LOGGED_IN, ""));
        //بررسی جهت اینکه کاربر لاگین شده اجازه فعال یا غیرفعال کردن خودش را ندارد
        if (idSet.contains(loggedInUserDto.getBackUserId()))
            throw new BackUserException("", BUSINESS_EXCEPTION_BACK_USER_NOT_ACCESS_HIMSELF, "");


        //جستجوی کاربران بک با شناسه کاربری
        List<BackUser> backUserList = backUserRepository.findByIdIn(idSet, null);

        //اگر شناسه کاربری یافت نشد خطا صادر شود
        if (backUserList.isEmpty())
            throw new BackUserException("", BUSINESS_EXCEPTION_BACK_USER_ID_NOT_FOUND, "");

        //حذف کاربر امنیت
        backUserList.forEach(backUser -> {
            //شرط لازم برای حذف کاربر ، غیرفعال بودن کاربر است
            if (!backUser.getInvalid())
                throw new BackUserException("", BUSINESS_EXCEPTION_BACK_USER_MUST_BE_INVALID, "");

            backUserRepository.delete(backUser);
            //حذف کاربر امنیت
            securityUserService.serviceDeleteForBack(backUser.getId());
        });

    }

}
