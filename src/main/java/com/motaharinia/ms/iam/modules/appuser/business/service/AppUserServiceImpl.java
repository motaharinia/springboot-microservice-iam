package com.motaharinia.ms.iam.modules.appuser.business.service;


import com.motaharinia.ms.iam.config.security.oauth2.dto.AppUserDto;
import com.motaharinia.ms.iam.config.security.oauth2.dto.BearerTokenDto;
import com.motaharinia.ms.iam.config.security.oauth2.dto.LoggedInUserDto;
import com.motaharinia.ms.iam.config.security.oauth2.resource.ResourceUserTokenProvider;
import com.motaharinia.ms.iam.external.captchaotp.business.service.CaptchaCheck;
import com.motaharinia.ms.iam.external.captchaotp.business.service.CaptchaOtpExternalService;
import com.motaharinia.ms.iam.external.captchaotp.presentation.AspectUsernameDto;
import com.motaharinia.ms.iam.external.common.enumeration.SourceProjectEnum;
import com.motaharinia.ms.iam.external.common.ratelimit.presentation.RateRequestDto;
import com.motaharinia.ms.iam.external.notification.business.service.NotificationExternalService;
import com.motaharinia.ms.iam.external.pointtracker.business.enumaration.OperationEnum;
import com.motaharinia.ms.iam.external.pointtracker.business.service.PointTrackerExternalCallService;
import com.motaharinia.ms.iam.external.pointtracker.presentation.dto.AddPointBalanceDto;
import com.motaharinia.ms.iam.modules.appuser.business.enumeration.AppUserGridSearchTypeEnum;
import com.motaharinia.ms.iam.modules.appuser.business.exception.AppUserException;
import com.motaharinia.ms.iam.modules.appuser.business.mapper.AppUserMapper;
import com.motaharinia.ms.iam.modules.appuser.persistence.orm.AppUser;
import com.motaharinia.ms.iam.modules.appuser.persistence.orm.AppUserRepository;
import com.motaharinia.ms.iam.modules.appuser.presentation.changepassword.ChangePasswordResponseDto;
import com.motaharinia.ms.iam.modules.appuser.presentation.dto.*;
import com.motaharinia.ms.iam.modules.appuser.presentation.dto.create.AppUserCreateRequestDto;
import com.motaharinia.ms.iam.modules.appuser.presentation.forgetpassword.ForgetPasswordCheckOtpRequestDto;
import com.motaharinia.ms.iam.modules.appuser.presentation.forgetpassword.ForgetPasswordCheckUsernameResponseDto;
import com.motaharinia.ms.iam.modules.appuser.presentation.signin.SigninCheckCredentialResponseDto;
import com.motaharinia.ms.iam.modules.appuser.presentation.signup.SignupCheckCredentialRequestDto;
import com.motaharinia.ms.iam.modules.appuser.presentation.signup.SignupCheckCredentialResponseDto;
import com.motaharinia.ms.iam.modules.appuser.presentation.signup.SignupCheckOtpRequestOtpDto;
import com.motaharinia.ms.iam.modules.appuserchangelog.business.enumeration.AppUserChangeTypeEnum;
import com.motaharinia.ms.iam.modules.appuserchangelog.business.service.AppUserChangeLogService;
import com.motaharinia.ms.iam.modules.appuserinvitationlog.business.service.AppUserInvitationLogService;
import com.motaharinia.ms.iam.modules.fso.FsoSetting;
import com.motaharinia.ms.iam.modules.fso.business.service.FsoService;
import com.motaharinia.ms.iam.modules.geo.Business.service.GeoService;
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
import com.motaharinia.msutility.tools.string.RandomGenerationTypeEnum;
import com.motaharinia.msutility.tools.string.StringTools;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author eng.motahari@gmail.com<br>
 * کلاس پیاده سازی سرویس کاربر فرانت برنامه
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class AppUserServiceImpl implements AppUserService {

    private final AppUserRepository appUserRepository;
    private final NotificationExternalService notificationExternalService;
    private final CaptchaOtpExternalService captchaOtpExternalService;
    private final SecurityUserService securityUserService;
    private final AppUserMapper appUserMapper;
    private final SecurityUserTokenService securityUserTokenService;
    private final PointTrackerExternalCallService pointTrackerExternalCallService;
    private final GeoService geoService;
    private final FsoService fsoService;
    private final ResourceUserTokenProvider resourceUserTokenProvider;
    private final AppUserInvitationLogService appUserInvitationLogService;
    private final AppUserChangeLogService appUserChangeLogService;

    private static final String BUSINESS_EXCEPTION_APP_USER_IS_EXISTED = "BUSINESS_EXCEPTION.APP_USER_IS_EXISTED";
    public static final String BUSINESS_EXCEPTION_APP_USER_ID_NOT_FOUND = "BUSINESS_EXCEPTION.APP_USER_ID_NOT_FOUND";
    private static final String BUSINESS_EXCEPTION_APP_USER_NATIONAL_CODE_NOT_FOUND = "BUSINESS_EXCEPTION.APP_USER_NATIONAL_CODE_NOT_FOUND";
    private static final String BUSINESS_EXCEPTION_APP_USER_REPEAT_PASSWORD_NOT_EQUAL = "BUSINESS_EXCEPTION.SECURITY_USER_REPEAT_PASSWORD_NOT_EQUAL";
    private static final String BUSINESS_EXCEPTION_APP_USER_IS_INVALID = "BUSINESS_EXCEPTION.APP_USER_IS_INVALID";
    private static final String BUSINESS_EXCEPTION_APP_USER_USERNAME_NOT_FOUND = "BUSINESS_EXCEPTION.APP_USER_USERNAME_NOT_FOUND";
    private static final String BUSINESS_EXCEPTION_APP_USER_NATIONAL_CODE_IS_DUPLICATE = "BUSINESS_EXCEPTION.APP_USER_NATIONAL_CODE_IS_DUPLICATE";
    private static final String BUSINESS_EXCEPTION_APP_USER_MOBILE_NO_IS_DUPLICATE = "BUSINESS_EXCEPTION.APP_USER_MOBILE_NO_IS_DUPLICATE";
    private static final String BUSINESS_EXCEPTION_APP_USER_NUMBER_OF_ALLOWED_INVITATION_CODE_IS_INCORRECT = "BUSINESS_EXCEPTION.APP_USER_NUMBER_OF_ALLOWED_INVITATION_CODE_IS_INCORRECT";
    public static final String BUSINESS_EXCEPTION_APP_USER_USER_NOT_LOGGED_IN = "BUSINESS_EXCEPTION.USER_NOT_LOGGED_IN";
    private static final String BUSINESS_EXCEPTION_APP_USER_INVITATION_CODE_IS_INVALID = "BUSINESS_EXCEPTION.APP_USER_INVITATION_CODE_IS_INVALID";
    private static final String BUSINESS_EXCEPTION_APP_USER_NUMBER_OF_ALLOWED_MOBILE_NO_IS_INCORRECT = "BUSINESS_EXCEPTION.APP_USER_NUMBER_OF_ALLOWED_MOBILE_NO_IS_INCORRECT";
    public static final String BUSINESS_EXCEPTION_APP_USER_HAS_ERROR_IN_CREATE_BATCH = "BUSINESS_EXCEPTION.PP_USER_HAS_ERROR_IN_CREATE_BATCH";


    private static final String NOTIFICATION_APP_USER_SIGNUP_OTP = "NOTIFICATION.APP_USER_SIGNUP_OTP";
    private static final String NOTIFICATION_APP_USER_SIGNIN_OTP = "NOTIFICATION.APP_USER_SIGNIN_OTP";
    private static final String NOTIFICATION_APP_USER_FORGET_PASSWORD_OTP = "NOTIFICATION.APP_USER_FORGET_PASSWORD_OTP";
    private static final String NOTIFICATION_APP_USER_INVITATION_CODE = "NOTIFICATION_APP_USER_INVITATION_CODE";

    //--------------------------keys
    private static final String OTP_MOBILE_SIGNUP_APP_USER = "OTP_MOBILE_SIGNUP_APP_USER-";
    private static final String OTP_MOBILE_SIGNIN_APP_USER = "OTP_MOBILE_SIGNIN_APP_USER-";
    private static final String OTP_MOBILE_FORGET_PASSWORD_APP_USER = "OTP_MOBILE_FORGET_PASSWORD_APP_USER-";


    /**
     * اگر مقدار true باشد کد فعالسازی در زمان تست توسعه دهندگان بدون نیاز به ارسال پیامک از طریق مدل خروجی داده میشود
     * و درخواستهای DevController فعال میشود
     */
    @Value("${app.security.test-activated:false}")
    private boolean securityTestActivated;

    @Value("${app.ms-captcha-otp.otp-length}")
    private Integer otpLength;

    @Value("${app.ms-captcha-otp.otp-ttl-seconds}")
    private Long otpTtlSeconds;

    @Value("${app.user.add.point-with-invitation-code-in-signup}")
    private Long addPointWithInvitationCodeInSignup;

    /**
     * تعداد مجاز ارسال کد معرف برای دوستان
     * میتوانیم از داخل  سایت برای 5 نفر به صورت رایگان پیامک ارسال نماییم
     */
    @Value("${app.user.number-of-allowed-invitation-code}")
    private int numberOfAllowedInvitationCode;

    /**
     * تعداد مجاز تغییر شماره موبایل  توسط خود appUser
     */
    @Value("${app.user.number-of-allowed-changed-mobile-no}")
    private int numberOfAllowedMobileNo;

    /**
     * تعداد مجاز تغییر کد پستی توسط خود appUser
     */
    @Value("${app.user.number-of-allowed-changed-postal-code}")
    private int numberOfAllowedPostalCode;

    public AppUserServiceImpl(AppUserRepository appUserRepository, NotificationExternalService notificationExternalService, CaptchaOtpExternalService captchaOtpExternalService, SecurityUserService securityUserService, AppUserMapper appUserMapper, SecurityUserTokenService securityUserTokenService, PointTrackerExternalCallService pointTrackerExternalCallService, GeoService geoService, FsoService fsoService, ResourceUserTokenProvider resourceUserTokenProvider, AppUserInvitationLogService appUserInvitationLogService, AppUserChangeLogService appUserChangeLogService) {
        this.appUserRepository = appUserRepository;
        this.notificationExternalService = notificationExternalService;
        this.captchaOtpExternalService = captchaOtpExternalService;
        this.securityUserService = securityUserService;
        this.appUserMapper = appUserMapper;
        this.securityUserTokenService = securityUserTokenService;
        this.pointTrackerExternalCallService = pointTrackerExternalCallService;
        this.geoService = geoService;
        this.fsoService = fsoService;
        this.resourceUserTokenProvider = resourceUserTokenProvider;
        this.appUserInvitationLogService = appUserInvitationLogService;
        this.appUserChangeLogService = appUserChangeLogService;
    }


    //-------------------------------------------------------
    ////Find methods or read methods
    //------------------------------------------------------


    /**
     * متد جستجو با شناسه ملی
     *
     * @param nationalCode شناسه ملی
     * @return خروجی: مدل جستجو شده
     */
    @Override
    public AppUserDto readByNationalCode(@NotNull String nationalCode) {
        return appUserMapper.toDto(appUserRepository.findByNationalCode(nationalCode).orElseThrow(() -> new AppUserException(nationalCode, BUSINESS_EXCEPTION_APP_USER_NATIONAL_CODE_NOT_FOUND, "nationalCode:" + nationalCode)));
    }

    /**
     * * متد جستجوی کاربر برنامه با ایدی کاربر امنیت
     *
     * @param id آیدی کاربر برنامه فرانت
     * @return AppUserDto
     */
    @Override
    public AppUserDto serviceReadById(@NotNull Long id) {
        //جستجو کاربر برنامه
        return appUserMapper.toDto(appUserRepository.findById(id).orElseThrow(() -> new AppUserException(id.toString(), BUSINESS_EXCEPTION_APP_USER_ID_NOT_FOUND, "id:" + id)));
    }


    /**
     * متد جستجوی کاربر برنامه با شناسه
     *
     * @param id آیدی کاربر برنامه فرانت
     * @return AppUserValidReadDto
     */
    @Override
    public AppUserValidReadDto readById1(@NotNull Long id) {
        //جستجو کاربر برنامه
        return appUserMapper.toAppUserValidReadDto(appUserRepository.findByIdAndInvalidIsFalse(id).orElseThrow(() -> new AppUserException(id.toString(), BUSINESS_EXCEPTION_APP_USER_ID_NOT_FOUND, "id:" + id)));
    }

    /**
     * متد جستجوی کاربر برنامه با لیست شناسه
     *
     * @param idSet لیست شناسه
     * @return Set<AppUserValidReadDto> خروجی: لیست مدل جستجو شده
     */
    @Override
    public Set<AppUserValidReadDto> readByIds(@NotNull Set<Long> idSet) {
        //return appUserMapper.toAppUserValidReadDtoSet(appUserRepository.findByIdInAndInvalidIsFalse(idSet));
        return appUserRepository.findByIdInAndInvalidIsFalse(idSet).stream().map(appUserMapper::toAppUserValidReadDto).collect(Collectors.toSet());
    }

    /**
     * متد جستجوی کاربر برنامه با لیست شناسه ملی
     *
     * @param nationalCodeSet لیست شناسه ملی
     * @return Set<AppUserValidReadDto> خروجی: لیست مدل جستجو شده
     */
    @Override
    public Set<AppUserValidReadDto> readByNationalCodes(@NotNull Set<String> nationalCodeSet) {
        //return appUserMapper.toAppUserValidReadDtoSet(appUserRepository.findByNationalCodeInAndInvalidIsFalse(nationalCodeSet));
        return appUserRepository.findByNationalCodeInAndInvalidIsFalse(nationalCodeSet).stream().map(appUserMapper::toAppUserValidReadDto).collect(Collectors.toSet());
    }

    /**
     * متد جستجوی کاربر برنامه با لیست شماره موبایل
     *
     * @param mobileNoSet لیست شناسه ملی
     * @return Set<AppUserValidReadDto> خروجی: لیست مدل جستجو شده
     */
    @Override
    public Set<AppUserValidReadDto> readByMobileNos(@NotNull Set<String> mobileNoSet) {
        //return appUserMapper.toAppUserValidReadDtoSet(appUserRepository.findByMobileNoInAndInvalidIsFalse(mobileNoSet));
        return appUserRepository.findByMobileNoInAndInvalidIsFalse(mobileNoSet).stream().map(appUserMapper::toAppUserValidReadDto).collect(Collectors.toSet());

    }

    //-------------------------------------------------------
    //signup
    //------------------------------------------------------


    /**
     * متد گام اول ثبت نام(بررسی کلمه کاربری و  رمزعبور)
     *
     * @param dto               کلاس مدل درخواست گام اول ثبت نام(بررسی کلمه کاربری و  رمز عبور)
     * @param aspectUsernameDto مدل مربوط به ست کردن نام کاربری برای @CaptchaCheck
     * @return خروجی: مدل پاسخ گام دوم ثبت نام(بررسی رمزعبور)
     */
    @Override
    @NotNull
    //بعنوان ویزیتور وقتی من فیلد "کپچا" را برای ۵ بار در طول ۵ دقیقه اشتباه وارد کنم آنگاه برای مدت ۱۰ دقیقه بلاک می شوم
    @CaptchaCheck(tryCount = 5, tryTtlInMinutes = 5, banTtlInMinutes = 10)
    public SignupCheckCredentialResponseDto signupCheckCredential(@NotNull SignupCheckCredentialRequestDto dto, @NotNull AspectUsernameDto aspectUsernameDto) {

        //بررسی صحت تکرار رمز عبور
        if (!(dto.getPassword().equals(dto.getPasswordRepeat()))) {
            throw new AppUserException(dto.getUsername(), BUSINESS_EXCEPTION_APP_USER_REPEAT_PASSWORD_NOT_EQUAL, "");
        }

        //بررسی تکراری نبودن شناسه ملی (کلمه کاربری)
        Optional<AppUser> appUserOptional = appUserRepository.findByNationalCode(dto.getUsername());
        if (appUserOptional.isPresent()) {
            throw new AppUserException(dto.getUsername(), BUSINESS_EXCEPTION_APP_USER_IS_EXISTED, "");
        }

        //بررسی تکراری نبودن موبایل
        Long id = appUserRepository.readIdByMobileNo(dto.getMobileNo());
        if (!ObjectUtils.isEmpty(id)) {
            throw new AppUserException(dto.getMobileNo(), BUSINESS_EXCEPTION_APP_USER_MOBILE_NO_IS_DUPLICATE + "::" + dto.getMobileNo(), "");
        }


        //هنگامی که پیامک ارسال شود تا ۳ دقیقه زمان برای اکسپایر شدن داشته باشد و تا این تایم درخواست دیگری نباید برای ارسال پیامک زده شود
        //تولید و ارسال کد تایید موبایل به کاربر
        String otp = captchaOtpExternalService.otpCreate(SourceProjectEnum.MS_IAM, OTP_MOBILE_SIGNUP_APP_USER + dto.getUsername(), otpLength, otpTtlSeconds).getValue();
        notificationExternalService.send(SourceProjectEnum.MS_IAM, dto.getMobileNo(), NOTIFICATION_APP_USER_SIGNUP_OTP + "::" + otp);


        //اگر به صورت تستی سامانه اجرا میشود کد تایید برای راحتی توسعه دهنده در پاسخ وب خروجی داده بشود در غیر این صورت خالی بشود
        if (!securityTestActivated) {
            notificationExternalService.send(SourceProjectEnum.MS_IAM, dto.getMobileNo(), NOTIFICATION_APP_USER_SIGNUP_OTP + "::" + otp);
            otp = "";
        }

        return new SignupCheckCredentialResponseDto(otp);
    }

    /**
     * متد گام دوم ثبت نام(بررسی کد تایید داخلی)
     *
     * @param dto کلاس مدل درخواست گام دوم ثبت نام
     * @return خروجی: مدل توکن
     */
    @Override
    public @NotNull BearerTokenDto signupCheckOtp(@NotNull SignupCheckOtpRequestOtpDto dto) {

        //بررسی صحت تکرار رمز عبور
        if (!(dto.getPassword().equals(dto.getPasswordRepeat()))) {
            throw new AppUserException(dto.getUsername(), BUSINESS_EXCEPTION_APP_USER_REPEAT_PASSWORD_NOT_EQUAL, "");
        }

        //بررسی کد تایید
        captchaOtpExternalService.otpCheck(SourceProjectEnum.MS_IAM, OTP_MOBILE_SIGNUP_APP_USER + dto.getUsername(), dto.getOtp(), "signupCheckOtp", dto.getUsername(), 200, 1, 1);

        //هنگام ثبت نام در صورتی که کد معرف داشته باشیم میتوانیم در قسمت دوم ثبت نام آن را وارد نماییم و با آن ثبت نام را تکمیل کنیم و بعد از تکمیل ثبت نام ،به معرف امتیاز داده میشود
        //جستجو آیدی با کد معرف
        if (!ObjectUtils.isEmpty(dto.getInvitationCode())) {
            Long appUserIdIntroducer = appUserRepository.readIdByInvitationCode(dto.getInvitationCode()).orElseThrow(() -> new AppUserException(dto.getUsername(), BUSINESS_EXCEPTION_APP_USER_INVITATION_CODE_IS_INVALID, ""));
            //دادن امتیاز به معرف
            pointTrackerExternalCallService.addPointToUser(new AddPointBalanceDto(List.of(appUserIdIntroducer), addPointWithInvitationCodeInSignup, OperationEnum.ADD_POINT_FOR_INTRODUCER_IN_SIGN_UP));
        }

        //بررسی تکراری نبودن شناسه ملی (کلمه کاربری)
        Optional<AppUser> appUserOptional = appUserRepository.findByNationalCode(dto.getUsername());
        if (appUserOptional.isPresent()) {
            throw new AppUserException(dto.getUsername(), BUSINESS_EXCEPTION_APP_USER_IS_EXISTED, "");
        }

        //بررسی تکراری نبودن موبایل
        Long id = appUserRepository.readIdByMobileNo(dto.getMobileNo());
        if (!ObjectUtils.isEmpty(id)) {
            throw new AppUserException(dto.getMobileNo(), BUSINESS_EXCEPTION_APP_USER_MOBILE_NO_IS_DUPLICATE + "::" + dto.getMobileNo(), "");
        }

        //ایجاد کاربر سامانه
        AppUser appUser = appUserMapper.toEntity(dto);

        //ایجاد کد معرف شخصی برای دادن به دوستان دیگر
        final String invitationCode = this.generateInvitationCodeForAppUser();
        appUser.setInvitationCode(invitationCode);
        appUserRepository.save(appUser);

        //جهت پر کردن فیلد آیدی و تاریخ ایجاد جهت ایجاد توکن
        AppUserDto appUserDto = appUserMapper.toDto(appUser);

        //ایجاد کاربر امنیت
        BearerTokenDto bearerTokenDto = securityUserService.serviceSignup(new SignupDto
                (new SecurityUserCreateRequestDto(dto.getUsername(), dto.getPassword(), appUserDto.getMobileNo(), null, appUser.getId(), null)
                        , null, null, appUserDto, dto.getRememberMe(), true));

        // ارسال کد معرف شخصی
        notificationExternalService.send(SourceProjectEnum.MS_IAM, appUserDto.getMobileNo(), NOTIFICATION_APP_USER_INVITATION_CODE + "::" + invitationCode);

        return bearerTokenDto;
    }

    /**
     * تولید کد معرف برای کاربر برنامه فرانت
     *
     * @return
     */
    private String generateInvitationCodeForAppUser() {
        boolean checkNotExistInvitationCode = true;
        String invitationCode = "";
        while (checkNotExistInvitationCode) {
            invitationCode = StringTools.generateRandomString(RandomGenerationTypeEnum.LATIN_CHARACTERS_NUMBERS, 6, true);
            if (appUserRepository.readIdByInvitationCode(invitationCode).isEmpty())
                checkNotExistInvitationCode = false;
        }
        return invitationCode;
    }


    //-------------------------------------------------------
    //signin
    //------------------------------------------------------

    /**
     * متد گام اول احراز هویت(بررسی کلمه کاربری و رمز عبور)
     *
     * @param rateRequestDto مدل درخواست برای بررسی محدودیت بازدید
     * @param username       کلمه کاربری (کد ملی شخص حقیقی / شناسه ملی سازمان)
     * @param password       رمز عبور
     * @return خروجی: مدل پاسخ گام اول احراز هویت
     */
    @Override
    // به عنوان ویزیتور وقتی "کپچا" را برای ۵ بار در طول ۵ دقیقه اشتباه وارد کنم آنگاه برای مدت ۱۵ دقیقه بلاک می شوم
    @CaptchaCheck(tryCount = 5, tryTtlInMinutes = 5, banTtlInMinutes = 15)
    public @NotNull SigninCheckCredentialResponseDto signinCheckCredential(@NotNull RateRequestDto rateRequestDto, @NotNull String username, @NotNull String password) {

        //بررسی کاربر امنیت
        SecurityUserReadDto securityUserReadDto = securityUserService.serviceSigninCheckCredential(username, password, true);

        //بررسی غیرفعال بودن یا عدم نمایش بودن کاربر برنامه فرانت
        List<Object[]> appUserInvalidAndHidden = appUserRepository.readInvalidById(securityUserReadDto.getAppUserId());
        if (appUserInvalidAndHidden.isEmpty()) {
            throw new AppUserException(username, BUSINESS_EXCEPTION_APP_USER_USERNAME_NOT_FOUND, "username:" + username);
        }
        if (((Boolean) appUserInvalidAndHidden.get(0)[0]) || ((Boolean) appUserInvalidAndHidden.get(0)[1])) {
            throw new AppUserException(username, BUSINESS_EXCEPTION_APP_USER_IS_INVALID, "username:" + username);
        }

        //هنگامی که پیامک ارسال شود تا ۳ دقیقه زمان برای اکسپایر شدن داشته باشد و تا این تایم درخواست دیگری نباید برای ارسال پیامک زده شود
        String otp = captchaOtpExternalService.otpCreate(SourceProjectEnum.MS_IAM, OTP_MOBILE_SIGNIN_APP_USER + username, otpLength, otpTtlSeconds).getValue();
        notificationExternalService.send(SourceProjectEnum.MS_IAM, securityUserReadDto.getMobileNo(), NOTIFICATION_APP_USER_SIGNIN_OTP + "::" + otp);
        //اگر به صورت تستی سامانه اجرا میشود کد تایید برای راحتی توسعه دهنده در پاسخ وب خروجی داده بشود در غیر این صورت خالی بشود
        if (!securityTestActivated) {
            otp = "";
        }

        return new SigninCheckCredentialResponseDto(otp);
    }

    /**
     * متد گام دوم احراز هویت(بررسی کد تایید)
     *
     * @param username کلمه کاربری (کد ملی شخص حقیقی / شناسه ملی سازمان)
     * @param password رمز عبور
     * @param otp      کد تایید
     * @return خروجی: مدل توکن
     */
    @Override
    public @NotNull BearerTokenDto signinCheckOtp(@NotNull AspectUsernameDto aspectUsernameDto, @NotNull String username, @NotNull String password, @NotNull String otp, @NotNull Boolean rememberMe) {

        //بررسی کد تایید
        captchaOtpExternalService.otpCheck(SourceProjectEnum.MS_IAM, OTP_MOBILE_SIGNIN_APP_USER + username, otp, "signinCheckOtp", username, 3, 3, 5);

        //جستجو آیدی کاربر برنامه فرانت با کلمه کاربری
        SecurityUserReadDto securityUserReadDto = securityUserService.serviceSigninCheckCredential(username, password, true);

        //جستجوی appUser
        AppUser appUser = appUserRepository.findById(securityUserReadDto.getAppUserId()).orElseThrow(() -> new AppUserException(username, BUSINESS_EXCEPTION_APP_USER_USERNAME_NOT_FOUND, "username:" + username));

        //بررسی غیرفعال بودن یا عدم نمایش بودن کاربر برنامه فرانت
        if ((appUser.getInvalid()) || (appUser.getHidden())) {
            throw new AppUserException(username, BUSINESS_EXCEPTION_APP_USER_IS_INVALID, "username:" + username);
        }

        //تبدیل انتیتی به مدل کاربر برنامه فرانت
        AppUserDto appUserDto = appUserMapper.toDto(appUser);

        //بررسی کاربر امنیت و تولید توکن
        return securityUserService.serviceSigninGenerateToken(username, appUserDto, null, rememberMe);

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
            throw new SecurityUserException("", BUSINESS_EXCEPTION_APP_USER_REPEAT_PASSWORD_NOT_EQUAL, "");
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
        String mobileNo = securityUserService.readByUsernameForGetMobileNo(username, true);

        //تولید و ارسال کد تایید موبایل به کاربر
        String otp = captchaOtpExternalService.otpCreate(SourceProjectEnum.MS_IAM, OTP_MOBILE_FORGET_PASSWORD_APP_USER + username, otpLength, otpTtlSeconds).getValue();
        notificationExternalService.send(SourceProjectEnum.MS_IAM, mobileNo, NOTIFICATION_APP_USER_FORGET_PASSWORD_OTP + "::" + otp);

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
     * @param isFront بررسی میکند که توکن برای کاربر برنامه تولید شود یا برای کاربر فرانت
     * @return خروجی: مدل توکن
     */
    @CaptchaCheck(banTtlInMinutes = 10, tryTtlInMinutes = 5, tryCount = 5)
    @Override
    public @NotNull BearerTokenDto forgetPasswordCheckOtp(@NotNull AspectUsernameDto aspectUsernameDto, @NotNull ForgetPasswordCheckOtpRequestDto dto, @NotNull Boolean isFront) {

        //بررسی صحت تکرار رمز عبور
        if (!dto.getNewPassword().equals(dto.getNewPasswordRepeat())) {
            throw new AppUserException(dto.getUsername(), BUSINESS_EXCEPTION_APP_USER_REPEAT_PASSWORD_NOT_EQUAL, "username:" + dto.getUsername());
        }

        //بررسی کد تایید
        captchaOtpExternalService.otpCheck(SourceProjectEnum.MS_IAM, OTP_MOBILE_FORGET_PASSWORD_APP_USER + dto.getUsername(), dto.getOtp(), "forgetPasswordCheckOtp", dto.getUsername(), 3, 3, 5);

        //جستجو آیدی کاربر برنامه فرانت با کلمه کاربری
        SecurityUserReadDto securityUserReadDto = securityUserService.serviceReadByUsername(dto.getUsername(), isFront);

        // ساختن مدل برای تولید توکن
        AppUserDto appUserDto = appUserMapper.toDto(appUserRepository.findById(securityUserReadDto.getAppUserId()).orElseThrow(() -> new AppUserException("", BUSINESS_EXCEPTION_APP_USER_ID_NOT_FOUND, "")));

        //تغییر رمز عبور کاربر امنیت
        return securityUserService.serviceForgetPassword(dto.getUsername(), dto.getNewPassword(), appUserDto, null, dto.getRememberMe());
    }

    //-------------------------------------------------------------
    //invite friends
    //-------------------------------------------------------------

    /**
     * متد دعوت دوستان به ثبت نام در سامانه با استفاده از کد معرف
     *
     * @param dto کلاس مدل دعوت از دوستان
     * @return Boolean
     */
    public Boolean inviteFriendByInvitationCode(@NotNull InviteFriendRequestDto dto) {

        //گرفتن کلمه کاربری شخص لاگین شده
        LoggedInUserDto loggedInUserDto = resourceUserTokenProvider.getLoggedInDto().orElseThrow(() -> new AppUserException("", BUSINESS_EXCEPTION_APP_USER_USER_NOT_LOGGED_IN, ""));
        String username = loggedInUserDto.getUsername();

        //جستجو با شناسه ملی
        AppUser appUser = appUserRepository.findById(loggedInUserDto.getAppUserId()).orElseThrow(() -> new AppUserException("", BUSINESS_EXCEPTION_APP_USER_ID_NOT_FOUND, ""));

        //محاسبه تعداد مجاز شماره موبایل برای ارسال کد معرف
        int countLog = appUserInvitationLogService.serviceCountLogByAppUser(appUser.getId());
        int checkCountInvitationCode = countLog + dto.getMobileNoToSet().size();

        //چک میکند تعداد شماره موبایل وارد شده دوستان از حد مجاز بیشتر نباشد
        if (checkCountInvitationCode > numberOfAllowedInvitationCode) {
            //تعداد باقی مانده از شماره موبایل هایی که کاربر میتواند برایشان کد معرف ارسال کند
            int remainNumberOfAllowedInvitationCode = numberOfAllowedInvitationCode - countLog;
            throw new AppUserException(username, BUSINESS_EXCEPTION_APP_USER_NUMBER_OF_ALLOWED_INVITATION_CODE_IS_INCORRECT + "::" + (Math.max(remainNumberOfAllowedInvitationCode, 0)), "");
        }

        //اگر شماره موبایل دوستان در سامانه باشد خطا داده شود
        for (String mobileNoTo : dto.getMobileNoToSet()) {
            if (!ObjectUtils.isEmpty(appUserRepository.readIdByMobileNo(mobileNoTo)))
                throw new AppUserException(username, BUSINESS_EXCEPTION_APP_USER_NUMBER_OF_ALLOWED_INVITATION_CODE_IS_INCORRECT, "");
            //ثبت لاگ
            appUserInvitationLogService.serviceCreate(appUser, mobileNoTo);
        }

        //ارسال پیامک - درصورت نبودن خطا در انتها پیامک ارسال میشود
        dto.getMobileNoToSet().forEach(mobileNoTo -> {
            //پیامک کد معرف به شماره موبایل دوستان
            notificationExternalService.send(SourceProjectEnum.MS_IAM, mobileNoTo, NOTIFICATION_APP_USER_FORGET_PASSWORD_OTP + "::" + appUser.getInvitationCode());
        });

        return true;
    }


    /**
     * متدی که چک میکند تغییر شماره تلفن همراه و  درج کد پستی به طور کلی فقط باید ۱۰ بار امکان پذیر باشد
     *
     * @param appUser        انتیتی کاربر برنامه فرانت
     * @param changeTypeEnum نوع لاگ تغییرات کاربر برنامه فرانت
     * @param valueFrom      مقدار قبلی
     * @param valueTo        مقدار جدید
     */
    private void serviceCheckCountChanges(@NotNull AppUser appUser, @NotNull AppUserChangeTypeEnum changeTypeEnum, @NotNull String valueFrom, @NotNull String valueTo) {
        //گرفتن تعداد مجاز اعمال تغییرات با توجه به نوع تغییرات
        int numberOfAllowed;
        switch (changeTypeEnum) {
            case MOBILE_NO:
                numberOfAllowed = numberOfAllowedMobileNo;
                break;
            case POSTAL_CODE:
                numberOfAllowed = numberOfAllowedPostalCode;
                break;
            default:
                numberOfAllowed = 10;
        }

        if (!valueFrom.equals(valueTo)) {
            //گرفتن تعداد لاگ تغییرات
            int countLog = appUserChangeLogService.serviceCountLogByAppUser(appUser.getId(), changeTypeEnum);
            if (countLog > numberOfAllowed) {
                //تعداد باقی مانده از تغییرات شماره موبایل
                int remainNumberOfAllowedInvitationCode = numberOfAllowed - countLog;
                throw new AppUserException(appUser.getNationalCode(), BUSINESS_EXCEPTION_APP_USER_NUMBER_OF_ALLOWED_MOBILE_NO_IS_INCORRECT + "::" + (Math.max(remainNumberOfAllowedInvitationCode, 0)), "");
            }
            appUserChangeLogService.serviceCreate(appUser, changeTypeEnum, valueFrom, valueTo);
        }
    }

    //-------------------------------------------------------------
    //CRUD
    //-------------------------------------------------------------

    /**
     * @param id شناسه کاربر برنامه فرانت
     * @return AppUserReadResponseDto خروجی:مدلی که شامل اطلاعات کامل از appUser میباشد
     */
    @Override
    public AppUserReadResponseDto readById(@NotNull Long id) {
        //جستجو کاربر برنامه
        AppUser appUser = appUserRepository.findById(id).orElseThrow(() -> new AppUserException(id.toString(), BUSINESS_EXCEPTION_APP_USER_ID_NOT_FOUND, "id:" + id));
        //پر کردن اطلاعات کاربر برنامه و ست کردن تصویر پروفایل
        return readFso(appUserMapper.toAppUserReadResponseDto(appUser));
    }

    /**
     * متد گرفتن تعداد کل کاربران برانامه فرانت
     *
     * @return خروجی: مدل پاسخ شامل تعداد کل کاربران
     */
    @Override
    public AppUserTotalCountResponseDto readTotalCount() {
        AppUserTotalCountResponseDto dto = new AppUserTotalCountResponseDto();
        dto.setTotalCount(appUserRepository.findAll().size());
        return dto;
    }


    /**
     * متد جستجو تمامی کاربران برنامه فرانت
     *
     * @param searchType  نوع سرچ
     * @param searchValue مقدار سرچ
     * @param pageable    برای صفحه بندی
     * @return CustomPageResponseDto<AppUserReadResponseDto> لیست مدل کاربر برنامه فرانت
     */
    @Override
    public CustomPageResponseDto<AppUserReadResponseDto> readAll(AppUserGridSearchTypeEnum searchType, String searchValue, Pageable pageable) {

        //امتیاز و نوع سطح همه کاربران از پروژه point tracker گرفته میشود
        List<Long> idList = appUserRepository.readAllId();
        List<GetUsersPointDto> getUsersPointDtoList = pointTrackerExternalCallService.readUsersPoint(new HashSet<>(idList));

        Page<AppUser> appUserPage = null;
        if (!ObjectUtils.isEmpty(searchType) && !ObjectUtils.isEmpty(searchValue)) {
            switch (searchType) {
                case FIRSTNAME:
                    appUserPage = appUserRepository.findAllByFirstNameContaining(searchValue, pageable);
                    break;
                case LASTNAME:
                    appUserPage = appUserRepository.findAllByLastNameContaining(searchValue, pageable);
                    break;
                case NATIONAL_CODE:
                    appUserPage = appUserRepository.findAllByNationalCodeContaining(searchValue, pageable);
                    break;
            }
        } else {
            appUserPage = appUserRepository.findAll(pageable);
        }

        if (!ObjectUtils.isEmpty(appUserPage)) {
            Page<AppUserReadResponseDto> finalPage = appUserPage.map(appUser -> {
                //تبدیل انتیتی به مدل
                AppUserReadResponseDto dto = appUserMapper.toAppUserReadResponseDto(appUser);
                //جستجوی امتیاز و سطح کاربر موردنظر از لیست
                Optional<GetUsersPointDto> getUsersPointDtoOptional = getUsersPointDtoList.stream().filter(getUsersPointDto -> getUsersPointDto.getNationalCode().equals(appUser.getNationalCode())).findFirst();
                getUsersPointDtoOptional.ifPresent(dto::setGetUsersPointDto);
                return dto;
            });

            return new CustomPageResponseDto<>(finalPage);
        }
        return null;
    }


    /**
     * متد ثبت کاربر برنامه فرانت
     *
     * @param dto                کلاس مدل ثبت کاربر برمامه ثبت
     * @param sendInvitationCode آیا کد معرف sms بشود یا نه
     * @return خروجی: مدل پاسخ کاربر برنامه ثبت
     */
    public @NotNull AppUserResponseDto create(@NotNull AppUserCreateRequestDto dto, @NotNull Boolean sendInvitationCode) {
        //بررسی صحت تکرار رمز عبور
        if (!(dto.getPassword().equals(dto.getPasswordRepeat()))) {
            throw new AppUserException(dto.getUsername(), BUSINESS_EXCEPTION_APP_USER_REPEAT_PASSWORD_NOT_EQUAL, "");
        }

        //بررسی تکراری نبودن شناسه ملی (کلمه کاربری)
        if (!ObjectUtils.isEmpty(appUserRepository.readIdByNationalCode(dto.getUsername()))) {
            throw new AppUserException(dto.getUsername(), BUSINESS_EXCEPTION_APP_USER_NATIONAL_CODE_IS_DUPLICATE + "::" + dto.getUsername(), "");
        }

        //بررسی تکراری نبودن موبایل
        if (!ObjectUtils.isEmpty(appUserRepository.readIdByMobileNo(dto.getAppUserDto().getMobileNo()))) {
            throw new AppUserException(dto.getUsername(), BUSINESS_EXCEPTION_APP_USER_MOBILE_NO_IS_DUPLICATE + "::" + dto.getAppUserDto().getMobileNo(), "");
        }

        //ایجاد کاربر سامانه
        //کپی کردن اطلاعات مدل در انتیتی
        AppUser appUser = appUserMapper.toEntity(dto);
        if (!ObjectUtils.isEmpty(dto.getGeoCityId()))
            appUser.setGeoCity(geoService.serviceReadById(dto.getGeoCityId()));
        //ایجاد کد معرف شخصی برای دادن به دوستان دیگر
        final String invitationCode = this.generateInvitationCodeForAppUser();
        appUser.setInvitationCode(invitationCode);
        appUserRepository.save(appUser);
        //جهت پر شدن فیلدهای آیدی و تاریخ ایجاد و اینولید توسط مپر
        dto.setAppUserDto(appUserMapper.toDto(appUser));

        //ایجاد کاربر امنیت - توکن جدید تولید نمیشود
        securityUserService.serviceSignup(new SignupDto(
                new SecurityUserCreateRequestDto(dto.getUsername(), dto.getPassword(), dto.getAppUserDto().getMobileNo(), dto.getAppUserDto().getEmailAddress(), appUser.getId(), null)
                , null, null, null, null, false));

        // ارسال کد دعوت شخصی
        if (Boolean.TRUE.equals(sendInvitationCode)) {
            notificationExternalService.send(SourceProjectEnum.MS_IAM, dto.getAppUserDto().getMobileNo(), NOTIFICATION_APP_USER_INVITATION_CODE + "::" + invitationCode);
        }


        return new AppUserResponseDto(appUser.getId());
    }

    /**
     * متد ویرایش کاربر برنامه فرانت
     *
     * @param dto مدل ویرایش اطلاعات  کاربر برنامه فرانت
     * @return خروجی: مدل کاربر برنامه فرانتAppUserResponseDto
     */
    @Override
    @NotNull
    public AppUserResponseDto update(@NotNull AppUserUpdateRequestDto dto) {
        //جستجو کاربر برنامه
        AppUser appUser = appUserRepository.findById(dto.getId()).orElseThrow(() -> new AppUserException("", BUSINESS_EXCEPTION_APP_USER_ID_NOT_FOUND, ""));
        return new AppUserResponseDto(this.serviceUpdate(appUser, dto, false).getId());
    }

    /**
     * متد ویرایش اطلاعات پروفایل کاربر برنامه فرانت توسط خودش
     *
     * @param dto مدل ویرایش اطلاعات  کاربر برنامه فرانت
     * @return خروجی: مدل کاربر برنامه فرانتAppUserResponseDto
     */
    @Override
    public @NotNull AppUserResponseDto updateProfile(@NotNull AppUserUpdateProfileRequestDto dto) {
        //گرفتن شناسه کاربری شخص لاگین شده
        LoggedInUserDto loggedInUserDto = resourceUserTokenProvider.getLoggedInDto().orElseThrow(() -> new AppUserException("", BUSINESS_EXCEPTION_APP_USER_USER_NOT_LOGGED_IN, ""));

        //جستجو کاربر برنامه
        AppUser appUser = appUserRepository.findById(loggedInUserDto.getAppUserId()).orElseThrow(() -> new AppUserException("", BUSINESS_EXCEPTION_APP_USER_ID_NOT_FOUND, ""));

        AppUserUpdateRequestDto appUserUpdateRequestDto = appUserMapper.toAppUserUpdateRequestDto(dto);
        appUserUpdateRequestDto.setId(appUser.getId());
        appUserUpdateRequestDto.setUsername(appUser.getNationalCode());
        appUserUpdateRequestDto.getAppUserDto().setNationalCode(appUser.getNationalCode());
        return new AppUserResponseDto(this.serviceUpdate(appUser, appUserUpdateRequestDto, false).getId());
    }

    /**
     * متد مشترک ویرایش اطلاعات کاربر برنامه فرانت
     * این متد هم در ویرایش پروفایل توسط خود کاربر لاگین شده و هم در ویرایش اطلاعات توسط ادمین پنل فراخوانی میشود
     * در ویرایش پروفایل(کاربر فرانت) امکان ویرایش شناسه ملی (کلمه کاربری) وجود ندارد و محدودیت در ویرایش شماره همراه و کدپستی وجود دارد
     *
     * @param appUser انتیتی کاربر برنامه فرانت
     * @param dto     مدل ویرایش اطلاعات
     * @param isFront آیا کاربر فرانت است؟
     * @return خروجی:انتیتی کاربر برنامه فرانت
     */
    public @NotNull AppUser serviceUpdate(@NotNull AppUser appUser, @NotNull AppUserUpdateRequestDto dto, @NotNull Boolean isFront) {

        //وقتی کاربر غیر فعال است نمی شود کاربر را ویرایش نمود
        if (Boolean.TRUE.equals(appUser.getInvalid())) {
            throw new AppUserException(appUser.getNationalCode(), BUSINESS_EXCEPTION_APP_USER_IS_INVALID, "");
        }

        //بررسی تکراری نبودن شناسه ملی (کلمه کاربری)
        if (!ObjectUtils.isEmpty(dto.getUsername())) {
            Long id = appUserRepository.readIdByNationalCode(dto.getUsername());
            if (id != null && !id.equals(dto.getId())) {
                throw new AppUserException(dto.getUsername(), BUSINESS_EXCEPTION_APP_USER_NATIONAL_CODE_IS_DUPLICATE + "::" + dto.getUsername(), "");
            }
        }

        //بررسی تکراری نبودن شماره موبایل
        Long id = appUserRepository.readIdByMobileNo(dto.getAppUserDto().getMobileNo());
        if (id != null && !id.equals(appUser.getId())) {
            throw new AppUserException(appUser.getNationalCode(), BUSINESS_EXCEPTION_APP_USER_MOBILE_NO_IS_DUPLICATE + "::" + dto.getAppUserDto().getMobileNo(), "");
        }

        //محدودیت در ویرایش شماره همراه و کدپستی برای کاربر برنامه فرانت در api ویرایش پروفایل وجود دارد
        if (Boolean.TRUE.equals(isFront)) {
            this.serviceCheckCountChanges(appUser, AppUserChangeTypeEnum.MOBILE_NO, appUser.getMobileNo(), dto.getAppUserDto().getMobileNo());
            this.serviceCheckCountChanges(appUser, AppUserChangeTypeEnum.POSTAL_CODE, appUser.getPostalCode(), dto.getPostalCode());
        }

        //کپی کردن اطلاعات مدل در انتیتی
        appUserMapper.toEntity(dto, appUser);
        if (!ObjectUtils.isEmpty(dto.getGeoCityId())) {
            appUser.setGeoCity(geoService.serviceReadById(dto.getGeoCityId()));
        } else {
            appUser.setGeoCity(null);
        }
        appUserRepository.save(appUser);

        //ویرایش کاربر امنیت
        securityUserService.serviceUpdate(new SecurityUserUpdateDto(dto.getAppUserDto().getMobileNo(), dto.getUsername(), null, dto.getAppUserDto().getEmailAddress(), appUser.getId(), null, null, null));


        return appUser;
    }


    /**
     * متد فعال یا غیرقعال کردن کاربران برنامه فرانت
     *
     * @param invalid فعال/ غیرفعال
     * @param ids     رشته شناسه کاربران فرانت بصورت csv
     */
    @Override
    public void invalid(@NotNull Boolean invalid, @NotNull String ids) {
        //تبدیل csv آیدیها به لیست
        Set<Long> idSet = Stream.of(ids.split(",")).map(Long::parseLong).collect(Collectors.toSet());

        //جستجوی کاربران فرانت با شناسه کاربری
        List<AppUser> appUserList = appUserRepository.findByIdIn(idSet, null);

        // فعال یا غیرفعال شدن
        appUserList.forEach(appUser -> {
            appUser.setInvalid(invalid);
            appUserRepository.save(appUser);
            // فعال یا غیرقعال کردن کاربر امنیت
            securityUserService.serviceInvalidForFront(appUser.getId(), invalid);
        });


        //اگر غیرفعال میشود باید توکن کاربران فرانت موردنظر هم غیرفعال شوند
        if (invalid) {
            //جستجوی کلمه کاربری با شناسه کاربر فرانت جهت غیرفعال کردن توکن کاربرانی که غیرفعال میشوند
            securityUserTokenService.serviceInvalid(securityUserService.serviceReadUsernamesByAppUserIdSet(idSet), SecurityTokenInvalidTypeEnum.SECURITY_USER_INVALID, SecurityUserInvalidTokenEnum.JUST_FRONT);
        }

    }

    /**
     * متد گرفتن اشخاصی که در روز و ماه جاری تولدشان هست
     *
     * @return Set<AppUserAnnualPoint> مدل پاسخ
     */
    @Override
    public Set<AppUserAnnualPointDto> readAllByDateOfBirth() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("-MM-dd");
        return appUserRepository.readAllByDateOfBirth(LocalDate.now().format(formatter)).stream().map(appUserMapper::toAppUserAnnualPointDto).collect(Collectors.toSet());
    }

    /**
     * متد گرفتن اشخاصی که در روز و ماه جاری ثبت نام کرده اند
     *
     * @return Set<AppUserAnnualPoint> مدل پاسخ
     */
    @Override
    public Set<AppUserAnnualPointDto> readAllByDateOfSignUp() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("-MM-dd");
        return appUserRepository.readAllByDateOfSignUp(LocalDate.now().format(formatter)).stream().map(appUserMapper::toAppUserAnnualPointDto).collect(Collectors.toSet());
    }


    private AppUserReadResponseDto readFso(AppUserReadResponseDto dto) {
        dto.setProfileImageFileList(fsoService.readFileViewDtoList(FsoSetting.MS_IAM_APP_USER_PROFILE_IMAGE, dto.getId()));
        return dto;
    }


}
