package com.motaharinia.ms.iam.modules.securityuser.business.service;


import com.motaharinia.ms.iam.config.caching.CachingConfiguration;
import com.motaharinia.ms.iam.config.security.PasswordTools;
import com.motaharinia.ms.iam.config.security.oauth2.dto.AppUserDto;
import com.motaharinia.ms.iam.config.security.oauth2.dto.BackUserDto;
import com.motaharinia.ms.iam.config.security.oauth2.dto.BearerTokenDto;
import com.motaharinia.ms.iam.config.security.oauth2.dto.LoggedInUserDto;
import com.motaharinia.ms.iam.config.security.oauth2.resource.ResourceUserTokenProvider;
import com.motaharinia.ms.iam.modules.appuser.business.exception.AppUserException;
import com.motaharinia.ms.iam.modules.backuser.business.exception.BackUserException;
import com.motaharinia.ms.iam.modules.securityuser.business.enumeration.SecurityTokenInvalidTypeEnum;
import com.motaharinia.ms.iam.modules.securityuser.business.enumeration.SecurityUserInvalidTokenEnum;
import com.motaharinia.ms.iam.modules.securityuser.business.exception.SecurityUserException;
import com.motaharinia.ms.iam.modules.securityuser.business.mapper.SecurityPermissionMapper;
import com.motaharinia.ms.iam.modules.securityuser.business.mapper.SecurityRoleMapper;
import com.motaharinia.ms.iam.modules.securityuser.business.mapper.SecurityUserMapper;
import com.motaharinia.ms.iam.modules.securityuser.persistence.orm.SecurityPermission;
import com.motaharinia.ms.iam.modules.securityuser.persistence.orm.SecurityRole;
import com.motaharinia.ms.iam.modules.securityuser.persistence.orm.SecurityUser;
import com.motaharinia.ms.iam.modules.securityuser.persistence.orm.SecurityUserRepository;
import com.motaharinia.ms.iam.modules.securityuser.presentation.securityuser.SecurityUserReadDto;
import com.motaharinia.ms.iam.modules.securityuser.presentation.securityuser.SecurityUserUpdateDto;
import com.motaharinia.ms.iam.modules.securityuser.presentation.securityuser.roleandpermission.RoleAndPermissionReadResponseDto;
import com.motaharinia.ms.iam.modules.securityuser.presentation.securityuser.roleandpermission.RoleAndPermissionUpdateRequestDto;
import com.motaharinia.ms.iam.modules.securityuser.presentation.securityuser.roleandpermission.SecurityPermissionDto;
import com.motaharinia.ms.iam.modules.securityuser.presentation.securityuser.roleandpermission.SecurityRoleDto;
import com.motaharinia.ms.iam.modules.securityuser.presentation.securityuser.signup.SignupDto;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AccountStatusUserDetailsChecker;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author eng.motahari@gmail.com<br>
 * کلاس پیاده سازی سرویس کاربر امنیت
 */
@Service
@Slf4j
@Transactional(rollbackFor = Exception.class)
public class SecurityUserServiceImpl implements UserDetailsService, SecurityUserService {

    /**
     * فیلدی جهت چک کردن انجام عملیات برای کاربران ال دپ می باشد و
     * اگر مقدارش ترو باشد در کاربران ال دپ میتوانند در سامانه لاگین کنند
     */
    @Value("${app.ldap-activated}")
    private boolean ldapActivated;

    private final SecurityUserRepository securityUserRepository;
    private final SecurityUserMapper mapper;
    private final SecurityRoleMapper securityRoleMapper;
    private final SecurityPermissionMapper securityPermissionMapper;
    private final SecurityRoleService securityRoleService;
    private final SecurityPermissionService securityPermissionService;
    private final SecurityUserTokenService securityUserTokenService;
    private final RedissonClient redissonClient;

    /**
     * کلاس مدیریت توکن ها در ResourceServer
     */
    private final ResourceUserTokenProvider resourceUserTokenProvider;


    private static final String BUSINESS_EXCEPTION_SECURITY_USER_USERNAME_NOT_FOUND = "BUSINESS_EXCEPTION.SECURITY_USER_USERNAME_NOT_FOUND";
    private static final String BUSINESS_EXCEPTION_SECURITY_USER_USERNAME_OR_PASSWORD_IS_INVALID = "BUSINESS_EXCEPTION.SECURITY_USER_USERNAME_OR_PASSWORD_IS_INVALID";
    private static final String BUSINESS_EXCEPTION_SECURITY_USER_PASSWORD_IS_INVALID = "BUSINESS_EXCEPTION.SECURITY_USER_PASSWORD_IS_INVALID";
    private static final String BUSINESS_EXCEPTION_SECURITY_USER_USERNAME_IS_INVALID = "BUSINESS_EXCEPTION.SECURITY_USER_USERNAME_IS_INVALID";
    private static final String BUSINESS_EXCEPTION_SECURITY_USER_NOT_LOGGED_IN = "BUSINESS_EXCEPTION.SECURITY_USER_NOT_LOGGED_IN";
    private static final String BUSINESS_EXCEPTION_SECURITY_USER_CHANGE_PASSWORD_SAME_WITH_CURRENT = "BUSINESS_EXCEPTION.SECURITY_USER_CHANGE_PASSWORD_SAME_WITH_CURRENT";
    private static final String BUSINESS_EXCEPTION_SECURITY_USER_BACK_USER_ID_NOT_FOUND = "BUSINESS_EXCEPTION.SECURITY_USER_BACK_USER_ID_NOT_FOUND";
    private static final String BUSINESS_EXCEPTION_SECURITY_USER_APP_USER_ID_NOT_FOUND = "BUSINESS_EXCEPTION.SECURITY_USER_APP_USER_ID_NOT_FOUND";
    private static final String BUSINESS_EXCEPTION_SECURITY_USER_IS_INVALID = "BUSINESS_EXCEPTION.SECURITY_USER_IS_INVALID";
    private static final String BUSINESS_EXCEPTION_SECURITY_USER_SIGNIN_RATE_LIMIT_EXCEPTION_BAN = "BUSINESS_EXCEPTION.SECURITY_USER_SIGNIN_RATE_LIMIT_EXCEPTION_BAN";
    private static final String BUSINESS_EXCEPTION_SECURITY_USER_USERNAME_IS_DUPLICATE = "BUSINESS_EXCEPTION.SECURITY_USER_USERNAME_IS_DUPLICATE";
    private static final String BUSINESS_EXCEPTION_SECURITY_USER_IS_EXISTED = "BUSINESS_EXCEPTION.SECURITY_USER_IS_EXISTED";




    public SecurityUserServiceImpl(SecurityUserRepository securityUserRepository, SecurityUserMapper mapper, SecurityRoleMapper securityRoleMapper, SecurityPermissionMapper securityPermissionMapper, SecurityRoleService securityRoleService, SecurityPermissionService securityPermissionService, SecurityUserTokenService securityUserTokenService, RedissonClient redissonClient, ResourceUserTokenProvider resourceUserTokenProvider) {
        this.securityUserRepository = securityUserRepository;
        this.mapper = mapper;
        this.securityRoleMapper = securityRoleMapper;
        this.securityPermissionMapper = securityPermissionMapper;
        this.securityRoleService = securityRoleService;
        this.securityPermissionService = securityPermissionService;
        this.securityUserTokenService = securityUserTokenService;
        this.redissonClient = redissonClient;
        this.resourceUserTokenProvider = resourceUserTokenProvider;
    }

    //-------------------------------------------------------
    //UserDetailsService
    //------------------------------------------------------
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SecurityUser securityUser = securityUserRepository.findByUsername(username).orElseThrow(() -> new BadCredentialsException("Bad credentials"));
        new AccountStatusUserDetailsChecker().check(securityUser);
        return securityUser;
    }

    //-------------------------------------------------------
    //Find Methods Or Read Methods
    //------------------------------------------------------


    /**
     * جستجوی کاربر امنیت با کلمه کاربری و اینکه کاربر فرانت سامانه باشد
     *
     * @param username کلمه کاربری
     * @return خروجی:انتیتی کاربر امنیت
     */
    public Optional<SecurityUser> serviceReadByUsernameForFrontOptional(String username) {
        return securityUserRepository.findByUsernameAndAppUserIdNotNull(username);
    }

    /**
     * جستجوی کاربر امنیت با کلمه کاربری و اینکه کاربر بک سامانه باشد
     *
     * @param username کلمه کاربری
     * @return خروجی:انتیتی کاربر امنیت
     */
    public Optional<SecurityUser> serviceReadByUsernameForBackOptional(String username) {
        return securityUserRepository.findByUsernameAndBackUserIdNotNull(username);
    }

    /**
     * جستجوی کاربر امنیت با کلمه کاربری و اینکه حتما کاربر بک سامانه باشد
     *
     * @param username کلمه کاربری
     * @return خروجی:انتیتی کاربر امنیت
     */
    @Override
    public SecurityUser serviceReadByUsernameForBack(String username) {
        return securityUserRepository.findByUsernameAndBackUserIdNotNull(username).orElseThrow(() -> new SecurityUserException(username, BUSINESS_EXCEPTION_SECURITY_USER_USERNAME_NOT_FOUND, "username:" + username));
    }


    /**
     * متد بین سرویسی جستجو با  کلمه کاربری
     *
     * @param username کلمه  کاربری
     * @param isFront  آیا  برای فرانت است؟
     * @return خروجی: انتیتی جستجو شده
     */
    @Override
    public SecurityUserReadDto serviceReadByUsername(@NotNull String username, @NotNull Boolean isFront) {
        SecurityUserReadDto securityUserReadDto;
        if (isFront) {
            securityUserReadDto = securityUserRepository.findAppUserIdByUsername(username).orElseThrow(() -> new AppUserException(username, BUSINESS_EXCEPTION_SECURITY_USER_USERNAME_NOT_FOUND, ""));
        } else {
            securityUserReadDto = securityUserRepository.findBackUserIdByUsername(username).orElseThrow(() -> new AppUserException(username, BUSINESS_EXCEPTION_SECURITY_USER_USERNAME_NOT_FOUND, ""));
        }
        return securityUserReadDto;
    }


    /**
     * جستجو کاربر امنیت با کلمه کاربری برای فرانت یا بک سایت
     *
     * @param username کلمه کاربری (کد ملی شخص حقیقی / شناسه ملی سازمان)
     * @param isFront  آیا لاگین برای فرانت است؟
     * @return خروجی:انتیتی کاربر امنیتSecurityUser
     */
    private SecurityUser readByUsername(@NotNull String username, @NotNull Boolean isFront, String errorMessage) {
        //جستجو کاربر امنیت با کلمه کاربری برای فرانت سایت
        Optional<SecurityUser> securityUserOptional;
        if (isFront) {
            securityUserOptional = this.serviceReadByUsernameForFrontOptional(username);
        } else {
            securityUserOptional = this.serviceReadByUsernameForBackOptional(username);
        }
        if (securityUserOptional.isEmpty()) {
            throw new AppUserException(username, errorMessage, "");
        }
        return securityUserOptional.get();
    }

    /**
     * @param username کلمه کاربری (کد ملی شخص حقیقی / شناسه ملی سازمان)
     * @param isFront  آیا لاگین برای فرانت است؟
     * @return خروجی:موبایل کاربر امنیت mobileNo
     */
    @Override
    @NotNull
    public String readByUsernameForGetMobileNo(@NotNull String username, @NotNull Boolean isFront) {
        return this.readByUsername(username, isFront, BUSINESS_EXCEPTION_SECURITY_USER_USERNAME_IS_INVALID).getMobileNo();
    }

    /**
     * جستجوی کاربران امنیت با لیست آیدی های کاربر برنامه فرانت
     *
     * @param appUserIdSet لیست آیدی های کاربر برنامه فرانت
     * @return Set<String> لیست کلمه کاربری
     */
    @Override
    public Set<String> serviceReadUsernamesByAppUserIdSet(@NotNull Set<Long> appUserIdSet) {
        return securityUserRepository.findByAppUserIdSet(appUserIdSet).stream().collect(Collectors.toSet());
    }

    /**
     * جستجوی کاربران امنیت با لیست آیدی های کاربر برنامه بک
     *
     * @param backUserIdSet لیست آیدی های کاربر برنامه بک
     * @return Set<String> لیست کلمه کاربری
     */
    @Override
    public Set<String> serviceReadUsernamesByBackUserIdSet(@NotNull Set<Long> backUserIdSet) {
        return securityUserRepository.findByBackUserIdSet(backUserIdSet).stream().collect(Collectors.toSet());
    }

    //-------------------------------------------------------
    //signup
    //------------------------------------------------------


    /**
     * ثبت کاربر امنیت  و تولید توکن
     *
     * @param dto مدل ثبت کاربر امنیت
     * @return خروجی: مدل توکن احراز هویت
     */
    @Override
    public BearerTokenDto serviceSignup(SignupDto dto) {
        boolean isFront = !ObjectUtils.isEmpty(dto.getSecurityUserCreateRequestDto().getAppUserId());

        Optional<SecurityUser> securityUserOptional;
        //جستجو تکراری نبودن کلمه کاربری برای کاربر برنامه فرانت
        if (isFront) {
            securityUserOptional = securityUserRepository.findByUsernameAndAppUserIdNotNull(dto.getSecurityUserCreateRequestDto().getUsername());
        } else {
            //جستجو تکراری نبودن کلمه کاربری برای کاربر برنامه بک
            securityUserOptional = securityUserRepository.findByUsernameAndBackUserIdNotNull(dto.getSecurityUserCreateRequestDto().getUsername());
        }
        if (securityUserOptional.isPresent()) {
            throw new SecurityUserException(dto.getSecurityUserCreateRequestDto().getUsername(), BUSINESS_EXCEPTION_SECURITY_USER_IS_EXISTED + "::" + dto.getSecurityUserCreateRequestDto().getUsername(), "");
        }

        //تبدیل مدل به انتیتی
        SecurityUser securityUser = mapper.toEntity(dto.getSecurityUserCreateRequestDto());

        //ست کردن پسورد
        securityUser.setPassword(PasswordTools.encode(PasswordEncoderFactories.createDelegatingPasswordEncoder(), dto.getSecurityUserCreateRequestDto().getPassword()));

        //ثبت نقشهای کاربری کاربر امنیت و ثبت دسترسی های اضافه بر نقش های کاربری
        RoleAndPermissionUpdateRequestDto roleAndPermissionUpdateRequestDto = new RoleAndPermissionUpdateRequestDto();
        roleAndPermissionUpdateRequestDto.setSecurityRoleIdAddSet(dto.getSecurityRoleIdSet());
        roleAndPermissionUpdateRequestDto.setSecurityPermissionIncludeIdAddSet(dto.getSecurityPermissionIncludeIdSet());
        if (isFront) {
            this.serviceSetRoleAndPermissionForFrontUser(securityUser, roleAndPermissionUpdateRequestDto, false);
        } else {
            this.serviceSetRoleAndPermissionForBackUser(securityUser, roleAndPermissionUpdateRequestDto, false);
        }

        //ثبت
        securityUserRepository.save(securityUser);

        //مدل تولید توکن احراز هویت
        BearerTokenDto bearerTokenDto = null;

        //برای کاربران بک و کاربران فرانتی که توسط ادمین پنل ثبت میشوند در زمان ثبت ، توکن دسترسی تولید نمیشود
        if (dto.getGenerateBearerToken()) {
            //تولید توکن برای کاربر برنامه فرانت در زمان ساین آپ
            bearerTokenDto = this.createBearerToken(securityUser, dto.getRememberMe(), dto.getAppUserDto(), new HashMap<>());
        }

        return bearerTokenDto;
    }

    //-------------------------------------------------------
    //signin
    //------------------------------------------------------

    /**
     * بررسی کاربر امنیت در لاگین-مرحله checkCredential
     *
     * @param username کلمه کاربری (کد ملی شخص حقیقی / شناسه ملی سازمان)
     * @param password رمز عبور
     * @param isFront  آیا کاربر برنامه فرانت است؟
     */
    public SecurityUserReadDto serviceSigninCheckCredential(@NotNull String username, @NotNull String password, @NotNull Boolean isFront) {

        //جستجو کاربر امنیت با کلمه کاربری
        SecurityUser securityUser = this.readByUsername(username, isFront, BUSINESS_EXCEPTION_SECURITY_USER_USERNAME_NOT_FOUND);


        //بررسی غیرفعال بودن یا عدم نمایش بودن کاربر امنیت
        if (securityUser.getInvalid() || securityUser.getHidden()) {
            throw new AppUserException(username, BUSINESS_EXCEPTION_SECURITY_USER_IS_INVALID, "");
        }

        //بررسی صحت رمز عبور برای کاربران بک و فرانت
        if (Boolean.FALSE.equals(PasswordTools.check(PasswordEncoderFactories.createDelegatingPasswordEncoder(), password, securityUser.getPassword()))) {
            //چک کردن محدودیت روی پسور
            if (Boolean.TRUE.equals(isFront)) {
                //به عنوان ویزیتور وقتی کلمه عبور به اشتباه وارد می شود تا ۱۰ بار در ۵ دقیقه برای ۱۵ دقیقه کاربر اجازه لاگین را ندارد
                this.validateSigninPasswordRateLimit(username, "serviceSigninCheckCredential", true, 10, 5, 15);
            } else {
                //روی کلمه عبور باید محدوددیت ۱۰ اشتباه در یک  ساعت در نظر گرفته شود اگر بیش از ۱۰ بار کلمه عبور  اشتباه وارد شد برای یک ساعت بلاک می شود
                this.validateSigninPasswordRateLimit(username, "serviceSigninCheckCredential", false, 10, 60, 60);
            }
            throw new AppUserException(username, BUSINESS_EXCEPTION_SECURITY_USER_USERNAME_OR_PASSWORD_IS_INVALID, "username:" + username);
        }


        return new SecurityUserReadDto(securityUser.getId(), securityUser.getAppUserId(), securityUser.getBackUserId(), securityUser.getMobileNo());
    }

    /**
     * بررسی کاربر امنیت در لاگین-مرحلهcheckOtp
     *
     * @param username    کلمه کاربری (کد ملی شخص حقیقی / شناسه ملی سازمان)
     * @param appUserDto  مدل کاربر برنامه فرانت
     * @param backUserDto مدل کاربر برنامه بک
     * @param rememberMe  مرا به خاطر بسپار
     * @return خروجی: مدل توکن احراز هویت
     */
    @Override
    public @NotNull BearerTokenDto serviceSigninGenerateToken(@NotNull String username, AppUserDto appUserDto, BackUserDto backUserDto, @NotNull Boolean rememberMe) {
        //بررسی پر بودن مدل کاربر برنامه فرانت جهت چک کردن
        Boolean isFront = !ObjectUtils.isEmpty(appUserDto);

        //جستجو کاربر امنیت با کلمه کاربری
        SecurityUser securityUser = this.readByUsername(username, isFront, BUSINESS_EXCEPTION_SECURITY_USER_USERNAME_NOT_FOUND);

        //تولید توکن احراز هویت
        BearerTokenDto bearerTokenDto;
        if (isFront) {
            //تولید توکن برای کاربر برنامه فرانت
            bearerTokenDto = this.createBearerToken(securityUser, rememberMe, appUserDto, new HashMap<>());
            //تولید توکن برای کاربر برنامه بک
        } else {
            bearerTokenDto = this.createBearerToken(securityUser, rememberMe, backUserDto, new HashMap<>());
        }

        return bearerTokenDto;

    }


    /**
     * چک کردن محدودیت برای پسورد
     *
     * @param username نام کاربری
     * @param username نام متد
     * @param username تعداد تلاش در دقیقه
     * @param username مدت زمان فاصله ی بین هر تلاش برای فراخوانی
     * @param username مدت زمان محدود شدن کاربر بلاک شده
     */
    public void validateSigninPasswordRateLimit(@NotNull String username, @NotNull String methodName, @NotNull Boolean isFront, @NotNull int tryCount, @NotNull int tryTtlInMinutes, @NotNull int banTtlInMinutes) {

        String typeUser = (isFront) ? "APP_USER" : "BACK_USER";

        String banKey = CachingConfiguration.REDIS_IAM_PASSWORD_RATE_LIMIT_BACK_USER_PREFIX + "_" + methodName + "_" + username + typeUser + "_BAN";
        String tryKey = CachingConfiguration.REDIS_IAM_PASSWORD_RATE_LIMIT_BACK_USER_PREFIX + "_" + methodName + "_" + username + typeUser + "_TRY";

        // اگر کاربر قبلا محدود شده است
        if (redissonClient.getKeys().countExists(banKey) > 0)
            throw new AppUserException(username, BUSINESS_EXCEPTION_SECURITY_USER_SIGNIN_RATE_LIMIT_EXCEPTION_BAN, "");

        RBucket<Integer> tryBucket = redissonClient.getBucket(tryKey);

        // اگر برای اولین بار متد را فراخوانی کرده است
        if (redissonClient.getKeys().countExists(tryKey) == 0) {
            tryBucket.set(1, TimeUnit.MINUTES.toSeconds(tryTtlInMinutes) - 5, TimeUnit.SECONDS);
            return;
        }

        //بررسی تعداد دفعات مجاز
        int numberOfTries = tryBucket.get() + 1;
        if (numberOfTries > tryCount) {
            RBucket<Boolean> banBucket = redissonClient.getBucket(banKey);
            //در صورتی که بیش از تعداد دفعات مجاز باشد ، کاربر را محدود میکنیم
            banBucket.set(true, TimeUnit.MINUTES.toSeconds(banTtlInMinutes) - 5, TimeUnit.SECONDS);
            throw new AppUserException(username, BUSINESS_EXCEPTION_SECURITY_USER_SIGNIN_RATE_LIMIT_EXCEPTION_BAN, "");
        } else {
            //افزایش تعداد دفعات  فراخوانی متد
            tryBucket.set(numberOfTries, redissonClient.getKeys().remainTimeToLive(tryKey), TimeUnit.MILLISECONDS);
        }
    }
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
    @Override
    public @NotNull String serviceChangePassword(@NotNull String currentPassword, @NotNull String newPassword) {
        //بررسی و دریافت اطلاعات کاربر امنیت لاگین شده
        Optional<LoggedInUserDto> loggedInUserDtoOptional = resourceUserTokenProvider.getLoggedInDto();
        if (loggedInUserDtoOptional.isEmpty()) {
            throw new SecurityUserException("", BUSINESS_EXCEPTION_SECURITY_USER_NOT_LOGGED_IN, "");
        }
        LoggedInUserDto loggedInUserDto = loggedInUserDtoOptional.get();
        SecurityUser securityUser = securityUserRepository.findById(loggedInUserDto.getId()).orElseThrow(() -> new SecurityUserException("", BUSINESS_EXCEPTION_SECURITY_USER_APP_USER_ID_NOT_FOUND, ""));

        //بررسی صحت رمز عبور فعلی
        if (Boolean.FALSE.equals(PasswordTools.check(PasswordEncoderFactories.createDelegatingPasswordEncoder(), currentPassword, securityUser.getPassword()))) {
            throw new SecurityUserException(loggedInUserDto.getUsername(), BUSINESS_EXCEPTION_SECURITY_USER_PASSWORD_IS_INVALID, "");
        }

        String newPasswordMd5 = PasswordTools.encode(PasswordEncoderFactories.createDelegatingPasswordEncoder(), newPassword);
        //بررسی عدم برابر بودن رمز عبور جدید با رمز عبور فعلی
        if (Boolean.TRUE.equals(PasswordTools.check(PasswordEncoderFactories.createDelegatingPasswordEncoder(), newPassword, securityUser.getPassword()))) {
            throw new SecurityUserException(loggedInUserDto.getUsername(), BUSINESS_EXCEPTION_SECURITY_USER_CHANGE_PASSWORD_SAME_WITH_CURRENT, "");
        }

        //ویرایش رمز عبور
        securityUser.setPassword(newPasswordMd5);
        securityUserRepository.save(securityUser);

        return loggedInUserDto.getUsername();
    }


    /**
     * فراموشی رمز عبور
     *
     * @param username    کلمه کاربری (کد ملی شخص حقیقی / شناسه ملی سازمان)
     * @param newPassword رمز عبور جدید
     * @param appUserDto  مدل کاربر برنامه فرانت
     * @param backUserDto مدل کاربر برنامه بک
     * @param rememberMe  مرا به خاطر بسپار
     * @return خروجی: مدل توکن احراز هویت
     */
    @Override
    @NotNull
    public BearerTokenDto serviceForgetPassword(@NotNull String username, @NotNull String newPassword, AppUserDto appUserDto, BackUserDto backUserDto, @NotNull Boolean rememberMe) {

        Boolean isFront = !ObjectUtils.isEmpty(appUserDto);
        SecurityUser securityUser = this.readByUsername(username, isFront, BUSINESS_EXCEPTION_SECURITY_USER_USERNAME_IS_INVALID);

        //بررسی غیرفعال بودن یا عدم نمایش بودن کاربر امنیت
        if (securityUser.getInvalid() || securityUser.getHidden()) {
            throw new AppUserException(username, BUSINESS_EXCEPTION_SECURITY_USER_IS_INVALID, "username:" + username);
        }

        //ویرایش رمز عبور
        securityUser.setPassword(PasswordTools.encode(PasswordEncoderFactories.createDelegatingPasswordEncoder(), newPassword));
        securityUserRepository.save(securityUser);

        //تولید توکن احراز هویت
        BearerTokenDto bearerTokenDto;
        if (isFront) {
            //تولید توکن برای کاربر برنامه فرانت
            bearerTokenDto = this.createBearerToken(securityUser, rememberMe, appUserDto, new HashMap<>());
        } else {
            //تولید توکن برای کاربر برنامه بک
            bearerTokenDto = this.createBearerToken(securityUser, rememberMe, backUserDto, new HashMap<>());
        }

        return bearerTokenDto;
    }


    //-------------------------------------------------------
    //generate Token methods
    //-------------------------------------------------------

    /**
     * متد تولید توکن احراز هویت برای فرانت
     *
     * @param securityUser           انتیتی کاربر امنیت
     * @param rememberMe             به یاد داشتن
     * @param appUserDto             مدل کاربر فرانت برنامه
     * @param additionalClaimHashMap مپ کلیم های اضافی توکن
     * @return JwtTokenDtoخروجی: مدل توکن احراز هویت
     */
    private @NotNull BearerTokenDto createBearerToken(SecurityUser securityUser, Boolean rememberMe, AppUserDto appUserDto, HashMap<String, Object> additionalClaimHashMap) {

        //ایجاد مدل کاربر لاگین شده
        LoggedInUserDto loggedInUserDto = new LoggedInUserDto(mapper.toDTO(securityUser), this.generateRoleList(securityUser, true), this.generatePermissionList(securityUser, true), appUserDto);

        //با استفاده از اطلاعات کاربر امنیت و لیست دسترسی هایش توکن ایجاد میکنیم
        return securityUserTokenService.createBearerToken(loggedInUserDto, rememberMe, additionalClaimHashMap, null, true);
    }

    /**
     * متد تولید توکن احراز هویت برای بک
     *
     * @param securityUser           انتیتی کاربر امنیت
     * @param rememberMe             به یاد داشتن
     * @param backUserDto            مدل کاربر بک برنامه
     * @param additionalClaimHashMap مپ کلیم های اضافی توکن
     * @return JwtTokenDtoخروجی: مدل توکن احراز هویت
     */
    private @NotNull BearerTokenDto createBearerToken(SecurityUser securityUser, Boolean rememberMe, BackUserDto backUserDto, HashMap<String, Object> additionalClaimHashMap) {

        //ایجاد مدل کاربر لاگین شده
        LoggedInUserDto loggedInUserDto = new LoggedInUserDto(mapper.toDTO(securityUser), this.generateRoleList(securityUser, false), this.generatePermissionList(securityUser, false), backUserDto);

        //با استفاده از اطلاعات کاربر امنیت و لیست دسترسی هایش توکن ایجاد میکنیم
        return securityUserTokenService.createBearerToken(loggedInUserDto, rememberMe, additionalClaimHashMap, null, false);
    }

    /**
     * متد تولید توکن احراز هویت برای فرانت
     *
     * @param securityUserId         شناسه کاربر امنیت
     * @param rememberMe             به یاد داشتن
     * @param appUserDto             مدل کاربر فرانت برنامه
     * @param additionalClaimHashMap مپ کلیم های اضافی توکن
     * @return خروجی: مدل توکن احراز هویت
     */
    @Override
    public BearerTokenDto createBearerToken(Long securityUserId, Boolean rememberMe, AppUserDto appUserDto, HashMap<String, Object> additionalClaimHashMap) {
        //جستجو کاربر امنیت با شناسه کاربر برنامه فرانت
        SecurityUser securityUser = securityUserRepository.findById(securityUserId).orElseThrow(() -> new SecurityUserException("", BUSINESS_EXCEPTION_SECURITY_USER_APP_USER_ID_NOT_FOUND, ""));

        return this.createBearerToken(securityUser, rememberMe, appUserDto, additionalClaimHashMap);
    }

    /**
     * متد تولید توکن احراز هویت برای بک
     *
     * @param securityUserId         شناسه کاربر امنیت
     * @param rememberMe             به یاد داشتن
     * @param backUserDto            مدل کاربر بک برنامه
     * @param additionalClaimHashMap مپ کلیم های اضافی توکن
     * @return خروجی: مدل توکن احراز هویت
     */
    @Override
    public BearerTokenDto createBearerToken(Long securityUserId, Boolean rememberMe, BackUserDto backUserDto, HashMap<String, Object> additionalClaimHashMap) {
        //جستجو کاربر امنیت با شناسه کاربر برنامه فرانت
        SecurityUser securityUser = securityUserRepository.findById(securityUserId).orElseThrow(() -> new SecurityUserException("", BUSINESS_EXCEPTION_SECURITY_USER_APP_USER_ID_NOT_FOUND, ""));

        return this.createBearerToken(securityUser, rememberMe, backUserDto, additionalClaimHashMap);
    }

    //-------------------------------------------------------
    //token methods
    //-------------------------------------------------------

    /**
     * متد تولید لیست نقش های کاربری
     *
     * @param securityUser کاربر امنیت
     * @param isFront      چک کردن برای پنل بک یا فرانت
     * @return خروجی: لیست نقش های کاربری
     */
    @NotNull
    private Set<String> generateRoleList(SecurityUser securityUser, Boolean isFront) {
        Set<String> securityRoleSet = new HashSet<>();
        securityUser.getSecurityRoleSet().stream().filter(r -> (!ObjectUtils.isEmpty(r.getIsFront()) && Objects.equals(r.getIsFront(), isFront) && !r.getInvalid())).forEach(role -> {
            securityRoleSet.add(role.getTitle());
        });

        return securityRoleSet;
    }

    /**
     * متد تولید لیست دسترسی های کاربر
     *
     * @param securityUser کاربر امنیت
     * @param isFront      چک کردن برای پنل بک یا فرانت
     * @return خروجی: لیست دسترسی های کاربر
     */
    @NotNull
    private Set<String> generatePermissionList(SecurityUser securityUser, Boolean isFront) {
        Set<String> securityPermissionSet = new HashSet<>();
        securityUser.getSecurityRoleSet().stream().filter(r -> (!ObjectUtils.isEmpty(r.getIsFront()) && Objects.equals(r.getIsFront(), isFront) && !r.getInvalid())).forEach(role -> {
            role.getPermissionSet().stream().filter(securityPermission -> (!ObjectUtils.isEmpty(securityPermission.getIsFront()) && Objects.equals(securityPermission.getIsFront(), isFront) && !securityPermission.getInvalid())).forEach(permission -> {
                securityPermissionSet.add(permission.getAuthority());
            });
        });

        //اضافه کردن لیست دسترسی های خاص
        securityUser.getSpecialPermissionIncludeSet().stream().filter(r -> (!ObjectUtils.isEmpty(r.getIsFront()) && Objects.equals(r.getIsFront(), isFront) && !r.getInvalid())).forEach(specialPermissionInclude -> {
            securityPermissionSet.add(specialPermissionInclude.getAuthority());
        });

        //کم کردن لیست دسترسی های خاص
        securityUser.getSpecialPermissionExcludeSet().stream().filter(r -> r.getIsFront() == (!ObjectUtils.isEmpty(r.getIsFront()) && Objects.equals(r.getIsFront(), isFront) && !r.getInvalid())).forEach(specialPermissionExclude -> {
            securityPermissionSet.remove(specialPermissionExclude.getAuthority());
        });

        return securityPermissionSet;
    }


    // -------------------------------------------------------
    //CRUD
    //-------------------------------------------------------

    /**
     * ویرایش کاربر امنیت
     *
     * @param dto مدل ویرایش اطلاعات کاربر امنیت
     */
    public void serviceUpdate(@NotNull SecurityUserUpdateDto dto) {
        boolean isFront = !ObjectUtils.isEmpty(dto.getAppUserId());

        SecurityUser securityUser;
        if (isFront) {
            securityUser = securityUserRepository.findByAppUserId(dto.getAppUserId()).orElseThrow(() -> new SecurityUserException("", BUSINESS_EXCEPTION_SECURITY_USER_APP_USER_ID_NOT_FOUND, ""));
        } else {
            securityUser = securityUserRepository.findByBackUserId(dto.getBackUserId()).orElseThrow(() -> new SecurityUserException("", BUSINESS_EXCEPTION_SECURITY_USER_BACK_USER_ID_NOT_FOUND, ""));
        }


        //-------------------------------------------------
        //چک کردن تکراری نبودن کلمه کاربری
        //-------------------------------------------------
        if (!StringUtils.isEmpty(dto.getUsername())) {
            Optional<SecurityUserReadDto> securityUserReadDto;
            //جستجو با کلمه کاربری برای کاربر برنامه فرانت
            if (isFront) {
                securityUserReadDto = securityUserRepository.findAppUserIdByUsername(dto.getUsername());
            } else {
                //جستجو با کلمه کاربری برای کاربر برنامه بک
                securityUserReadDto = securityUserRepository.findBackUserIdByUsername(dto.getUsername());
            }
            //بررسی تکراری نبودن کلمه کاربری
            if (securityUserReadDto.isPresent() && !securityUserReadDto.get().getSecurityUserId().equals(securityUser.getId())) {
                throw new BackUserException(dto.getUsername(), BUSINESS_EXCEPTION_SECURITY_USER_USERNAME_IS_DUPLICATE + "::" + dto.getUsername(), "");
            }
            securityUser.setUsername(dto.getUsername());
        }


        if (!StringUtils.isEmpty(dto.getPassword())) {
            securityUser.setPassword(PasswordTools.encode(PasswordEncoderFactories.createDelegatingPasswordEncoder(), dto.getPassword()));
        }

        securityUser.setMobileNo(dto.getMobileNo());
        securityUser.setEmailAddress(dto.getEmailAddress());
        securityUserRepository.save(securityUser);

        //ویرایش نقشهای کاربری کاربر امنیت و ویرایش دسترسی های اضافه بر نقش های کاربری
        RoleAndPermissionUpdateRequestDto roleAndPermissionUpdateRequestDto = new RoleAndPermissionUpdateRequestDto();
        roleAndPermissionUpdateRequestDto.setSecurityRoleIdRemoveSet(securityUser.getSecurityRoleSet().stream().map(SecurityRole::getId).collect(Collectors.toSet()));
        roleAndPermissionUpdateRequestDto.setSecurityRoleIdAddSet((CollectionUtils.isEmpty(dto.getSecurityRoleIdSet()) ? new HashSet<>() : dto.getSecurityRoleIdSet()));
        roleAndPermissionUpdateRequestDto.setSecurityPermissionIncludeIdRemoveSet(securityUser.getSpecialPermissionIncludeSet().stream().map(SecurityPermission::getId).collect(Collectors.toSet()));
        roleAndPermissionUpdateRequestDto.setSecurityPermissionIncludeIdAddSet((CollectionUtils.isEmpty(dto.getSecurityPermissionIncludeIdSet()) ? new HashSet<>() : dto.getSecurityPermissionIncludeIdSet()));
        //ویرایش نقش و دسترسی برای کاربر برنامه فرانت
        if (isFront) {
            this.serviceSetRoleAndPermissionForFrontUser(securityUser, roleAndPermissionUpdateRequestDto, true);
        } else {
            //ویرایش نقش و دسترسی برای کاربر برنامه بک
            this.serviceSetRoleAndPermissionForBackUser(securityUser, roleAndPermissionUpdateRequestDto, true);
        }

    }

    /**
     * حذف کاربر امنیت
     *
     * @param backUserId شناسه کاربری کاربر برنامه بک
     */
    @Override
    public void serviceDeleteForBack(@NotNull Long backUserId) {
        //با حذف securityUser سطرهای securityRoleSet و specialPermissionIncludeSet و specialPermissionExcludeSet مرتبط باهاش هم خودشان حذف میشوند
        //جستجو کاربر امنیت با شناسه کاربر برنامه بک
        SecurityUser securityUser = securityUserRepository.findByBackUserId(backUserId).orElseThrow(() -> new SecurityUserException("", BUSINESS_EXCEPTION_SECURITY_USER_BACK_USER_ID_NOT_FOUND, ""));
        securityUserRepository.delete(securityUser);
    }

    /**
     * فعال و غیرفعال کردن کاربر امنیت
     *
     * @param appUserId شناسه کاربری کاربر برنامه فرانت
     * @param invalid   فعال یا غیرفعال
     */
    @Override
    public void serviceInvalidForFront(@NotNull Long appUserId, @NotNull Boolean invalid) {
        //جستجو کاربر امنیت با شناسه کاربر برنامه فرانت
        SecurityUser securityUser = securityUserRepository.findByAppUserId(appUserId).orElseThrow(() -> new SecurityUserException("", BUSINESS_EXCEPTION_SECURITY_USER_APP_USER_ID_NOT_FOUND, ""));
        securityUser.setInvalid(invalid);
        securityUserRepository.save(securityUser);
    }

    /**
     * فعال و غیرفعال کردن کاربر امنیت
     *
     * @param backUserId شناسه کاربری کاربر برنامه بک
     * @param invalid    فعال یا غیرفعال
     */
    @Override
    public void serviceInvalidForBack(@NotNull Long backUserId, @NotNull Boolean invalid) {
        //جستجو کاربر امنیت با شناسه کاربر برنامه بک
        SecurityUser securityUser = securityUserRepository.findByBackUserId(backUserId).orElseThrow(() -> new SecurityUserException("", BUSINESS_EXCEPTION_SECURITY_USER_BACK_USER_ID_NOT_FOUND, ""));
        securityUser.setInvalid(invalid);
        securityUserRepository.save(securityUser);

    }


    /**
     * جستجو دسترسی ها و نقش کاربری کاربر برنامه بک
     *
     * @param backUserId شناسه کاربری کاربر برنامه بک
     * @return خروجی: مدل دسترسی ها و نقش های کاربری که برای کاربر برنامه فرانت یا بک UpdateRoleAndPermissionResponseDto
     */
    @Override
    public RoleAndPermissionReadResponseDto serviceReadRoleAndPermissionForBack(@NotNull Long backUserId) {

        SecurityUser securityUser;
        Set<SecurityRoleDto> securityRoleDtoSet;
        Set<SecurityPermissionDto> securityPermissionIncludeDtoSet;
        Set<SecurityPermissionDto> securityPermissionExcludeDtoSet;


        //جستجو کاربر امنیت با شناسه کاربر برنامه بک
        securityUser = securityUserRepository.findByBackUserId(backUserId).orElseThrow(() -> new SecurityUserException("", BUSINESS_EXCEPTION_SECURITY_USER_BACK_USER_ID_NOT_FOUND, ""));

        //ست کردن آیدی نقش های کاربری در مدل
        securityRoleDtoSet = securityUser.getSecurityRoleSet().stream().filter(securityRole -> !securityRole.getIsFront()).map(securityRoleMapper::toSecurityRoleDto).collect(Collectors.toSet());

        //ست کردن آیدی دسترسی هایی که برای کاربر برنامه فرانت اضافه شده است
        securityPermissionIncludeDtoSet = securityUser.getSpecialPermissionIncludeSet().stream().filter(SpecialPermissionInclude -> !SpecialPermissionInclude.getIsFront()).map(securityPermissionMapper::toSecurityPermissionDto).collect(Collectors.toSet());

        //ست کردن آیدی دسترسی هایی که از کاربر برنامه فرانت حذف شده است
        securityPermissionExcludeDtoSet = securityUser.getSpecialPermissionExcludeSet().stream().filter(SpecialPermissionExclude -> !SpecialPermissionExclude.getIsFront()).map(securityPermissionMapper::toSecurityPermissionDto).collect(Collectors.toSet());

        return new RoleAndPermissionReadResponseDto(securityRoleDtoSet, securityPermissionIncludeDtoSet, securityPermissionExcludeDtoSet);

    }

    /**
     * ویرایش دسترسی ها و نقش کاربری
     *
     * @param securityUser         انتیتی کاربر امنیت
     * @param dto                  مدل ویرایش دسترسی ها و نقش کاربری
     * @param checkTokenForInvalid آیا غیرفعال کردن توکن کاربر هم بررسی شود؟
     */
    public void serviceSetRoleAndPermissionForFrontUser(SecurityUser securityUser, RoleAndPermissionUpdateRequestDto dto, Boolean checkTokenForInvalid) {

        Set<SecurityRole> securityRoleSetDB = securityUser.getSecurityRoleSet();
        Set<SecurityPermission> securityPermissionIncludeSet = securityUser.getSpecialPermissionIncludeSet();
        Set<SecurityPermission> securityPermissionExcludeSet = securityUser.getSpecialPermissionExcludeSet();

        //چک کردن غیرفعال کردن توکن کاربر


        if (checkTokenForInvalid) {
            this.serviceCheckInvalidTokenByRole(securityUser.getUsername(), securityRoleSetDB.stream().map(SecurityRole::getId).collect(Collectors.toSet()), dto.getSecurityRoleIdAddSet());
            this.serviceCheckInvalidTokenByPermission(securityUser.getUsername(), securityPermissionIncludeSet.stream().map(SecurityPermission::getId).collect(Collectors.toSet()), dto.getSecurityPermissionIncludeIdAddSet());
            this.serviceCheckInvalidTokenByPermission(securityUser.getUsername(), securityPermissionExcludeSet.stream().map(SecurityPermission::getId).collect(Collectors.toSet()), dto.getSecurityPermissionExcludeIdAddSet());
        }

        //--------------------------------------------------------------------
        //انجام عملیات حذف و اضافه برای نقش های کاربری
        //--------------------------------------------------------------------
        //نقش های که باید حذف شوند
        if (!CollectionUtils.isEmpty(dto.getSecurityRoleIdRemoveSet())) {
            // جستجو نقش  با شناسه
            securityRoleSetDB.removeAll(dto.getSecurityRoleIdRemoveSet().stream().map(securityRoleService::serviceReadByIdForBack).collect(Collectors.toSet()));
        }
        //نقش های که باید اضافه شوند
        if (!CollectionUtils.isEmpty(dto.getSecurityRoleIdAddSet())) {
            // جستجو نقش با شناسه و وضعیت غیرفعال هم چک میکند
            securityRoleSetDB.addAll(dto.getSecurityRoleIdAddSet().stream().map(securityRoleService::serviceReadByIdAndCheckInvalidForFront).collect(Collectors.toSet()));
        }

        //--------------------------------------------------------------------
        //انجام عملیات حذف و اضافه برای دسترسی های اینکلود
        //--------------------------------------------------------------------

        //دسترسی های اینکلود که باید حذف شوند
        if (!CollectionUtils.isEmpty(dto.getSecurityPermissionIncludeIdRemoveSet())) {
            // جستجو دسترسی  با شناسه
            securityPermissionIncludeSet.removeAll(dto.getSecurityPermissionIncludeIdRemoveSet().stream().map(securityPermissionService::serviceReadByIdForFront).collect(Collectors.toSet()));
        }
        //دسترسی های اینکلود که باید اضافه شوند
        if (!CollectionUtils.isEmpty(dto.getSecurityPermissionIncludeIdAddSet())) {
            // جستجو دسترسی  با شناسه
            securityPermissionIncludeSet.addAll(dto.getSecurityPermissionIncludeIdAddSet().stream().map(securityPermissionService::serviceReadByIdForFront).collect(Collectors.toSet()));
        }

        //--------------------------------------------------------------------
        //انجام عملیات حذف و اضافه برای دسترسی های اکسکلود
        //--------------------------------------------------------------------

        //دسترسی های اکسکلود که باید حذف شوند
        if (!CollectionUtils.isEmpty(dto.getSecurityPermissionExcludeIdRemoveSet())) {
            // جستجو دسترسی  با شناسه
            securityPermissionExcludeSet.removeAll(dto.getSecurityPermissionExcludeIdRemoveSet().stream().map(securityPermissionService::serviceReadByIdForFront).collect(Collectors.toSet()));
        }
        //دسترسی های اکسکلود که باید اضافه شوند
        if (!CollectionUtils.isEmpty(dto.getSecurityPermissionExcludeIdAddSet())) {
            // جستجو دسترسی  با شناسه
            securityPermissionExcludeSet.addAll(dto.getSecurityPermissionExcludeIdAddSet().stream().map(securityPermissionService::serviceReadByIdForFront).collect(Collectors.toSet()));
        }
        securityUserRepository.save(securityUser);
    }

    /**
     * ویرایش دسترسی ها و نقش کاربری کاربر برنامه بک
     *
     * @param securityUser         انتیتی کاربر امنیت
     * @param dto                  مدل ویرایش دسترسی ها و نقش کاربری
     * @param checkTokenForInvalid آیا غیرفعال کردن توکن کاربر هم بررسی شود؟
     */

    private void serviceSetRoleAndPermissionForBackUser(SecurityUser securityUser, RoleAndPermissionUpdateRequestDto dto, Boolean checkTokenForInvalid) {
        Set<SecurityRole> securityRoleSetDB = securityUser.getSecurityRoleSet();
        Set<SecurityPermission> securityPermissionIncludeSet = securityUser.getSpecialPermissionIncludeSet();
        Set<SecurityPermission> securityPermissionExcludeSet = securityUser.getSpecialPermissionExcludeSet();

        //چک کردن غیرفعال کردن توکن کاربر
        if (checkTokenForInvalid) {
            this.serviceCheckInvalidTokenByRole(securityUser.getUsername(), securityRoleSetDB.stream().map(SecurityRole::getId).collect(Collectors.toSet()), dto.getSecurityRoleIdAddSet());
            this.serviceCheckInvalidTokenByPermission(securityUser.getUsername(), securityPermissionIncludeSet.stream().map(SecurityPermission::getId).collect(Collectors.toSet()), dto.getSecurityPermissionIncludeIdAddSet());
            this.serviceCheckInvalidTokenByPermission(securityUser.getUsername(), securityPermissionExcludeSet.stream().map(SecurityPermission::getId).collect(Collectors.toSet()), dto.getSecurityPermissionExcludeIdAddSet());
        }

        //--------------------------------------------------------------------
        //انجام عملیات حذف و اضافه برای نقش های کاربری
        //--------------------------------------------------------------------
        //نقش های که باید حذف شوند
        if (!CollectionUtils.isEmpty(dto.getSecurityRoleIdRemoveSet())) {
            // جستجو نقش  با شناسه
            securityRoleSetDB.removeAll(dto.getSecurityRoleIdRemoveSet().stream().map(securityRoleService::serviceReadByIdForBack).collect(Collectors.toSet()));
        }
        //نقش های که باید اضافه شوند
        if (!CollectionUtils.isEmpty(dto.getSecurityRoleIdAddSet())) {
            // جستجو نقش با شناسه و وضعیت غیرفعال هم چک میکند
            securityRoleSetDB.addAll(dto.getSecurityRoleIdAddSet().stream().map(securityRoleService::serviceReadByIdAndCheckInvalidForBack).collect(Collectors.toSet()));
        }

        //--------------------------------------------------------------------
        //انجام عملیات حذف و اضافه برای دسترسی های اینکلود
        //--------------------------------------------------------------------

        //دسترسی های اینکلود که باید حذف شوند
        if (!CollectionUtils.isEmpty(dto.getSecurityPermissionIncludeIdRemoveSet())) {
            // جستجو دسترسی  با شناسه
            securityPermissionIncludeSet.removeAll(dto.getSecurityPermissionIncludeIdRemoveSet().stream().map(securityPermissionService::serviceReadByIdForBack).collect(Collectors.toSet()));
        }
        //دسترسی های اینکلود که باید اضافه شوند
        if (!CollectionUtils.isEmpty(dto.getSecurityPermissionIncludeIdAddSet())) {
            // جستجو دسترسی  با شناسه
            securityPermissionIncludeSet.addAll(dto.getSecurityPermissionIncludeIdAddSet().stream().map(securityPermissionService::serviceReadByIdForBack).collect(Collectors.toSet()));
        }

        //--------------------------------------------------------------------
        //انجام عملیات حذف و اضافه برای دسترسی های اکسکلود
        //--------------------------------------------------------------------

        //دسترسی های اکسکلود که باید حذف شوند
        if (!CollectionUtils.isEmpty(dto.getSecurityPermissionExcludeIdRemoveSet())) {
            // جستجو دسترسی  با شناسه
            securityPermissionExcludeSet.removeAll(dto.getSecurityPermissionExcludeIdRemoveSet().stream().map(securityPermissionService::serviceReadByIdForBack).collect(Collectors.toSet()));
        }
        //دسترسی های اکسکلود که باید اضافه شوند
        if (!CollectionUtils.isEmpty(dto.getSecurityPermissionExcludeIdAddSet())) {
            // جستجو دسترسی  با شناسه
            securityPermissionExcludeSet.addAll(dto.getSecurityPermissionExcludeIdAddSet().stream().map(securityPermissionService::serviceReadByIdForBack).collect(Collectors.toSet()));
        }
        securityUserRepository.save(securityUser);

    }

    /**
     * چک میکند آیا با تغییر نقش های یک کاربر ، توکن های کاربر موردنظر را غیرفعال کند یا نه
     *
     * @param username             نام کاربری
     * @param securityRoleIdDBSet  آیدی نقش های کاربر در دیتابیس
     * @param securityRoleIdDtoSet آیدی نقش های کاربر در مدل
     */
    private void serviceCheckInvalidTokenByRole(String username, Set<Long> securityRoleIdDBSet, Set<Long> securityRoleIdDtoSet) {

        //اگر تعداد آیدی نقش ها با هم متفاوت باشد یعنی توکن کاربر باید غیر فعال شود
        if (securityRoleIdDtoSet.size() != securityRoleIdDBSet.size()) {
            //غیرفعال کردن توکن
            securityUserTokenService.serviceInvalid(Set.of(username), SecurityTokenInvalidTypeEnum.SECURITY_USER_UPDATE, SecurityUserInvalidTokenEnum.JUST_BACK);
        } else {
            //اگر تعداد آیدی نقش ها با هم یکی باشد ولی آیدی هایشان باهم متفاوت باشند یعنی توکن کاربر باید غیر فعال شود
            securityRoleIdDBSet.removeAll(securityRoleIdDtoSet);
            if (securityRoleIdDBSet.size() != 0) {
                //غیرفعال کردن توکن
                securityUserTokenService.serviceInvalid(Set.of(username), SecurityTokenInvalidTypeEnum.SECURITY_USER_UPDATE, SecurityUserInvalidTokenEnum.JUST_BACK);
            }
        }
    }

    /**
     * چک میکند آیا با تغییر دسترسی های یک کاربر ، توکن های کاربر موردنظر را غیرفعال کند یا نه
     *
     * @param username                   نام کاربری
     * @param securityPermissionIdDBSet  آیدی دسترسی های کاربر در دیتابیس
     * @param securityPermissionIdDtoSet آیدی دسترسی های کاربر در مدل
     */
    private void serviceCheckInvalidTokenByPermission(String username, Set<Long> securityPermissionIdDBSet, Set<Long> securityPermissionIdDtoSet) {
        //اگر تعداد آیدی دسترسی ها با هم متفاوت باشد یعنی توکن کاربر باید غیر فعال شود
        if (securityPermissionIdDtoSet.size() != securityPermissionIdDBSet.size()) {
            //غیرفعال کردن توکن
            securityUserTokenService.serviceInvalid(Set.of(username), SecurityTokenInvalidTypeEnum.SECURITY_USER_UPDATE, SecurityUserInvalidTokenEnum.JUST_BACK);
        } else {
            //اگر تعداد آیدی دسترسی ها با هم یکی باشد ولی آیدی هایشان باهم متفاوت باشند یعنی توکن کاربر باید غیر فعال شود
            securityPermissionIdDBSet.removeAll(securityPermissionIdDtoSet);
            if (securityPermissionIdDBSet.size() != 0) {
                //غیرفعال کردن توکن
                securityUserTokenService.serviceInvalid(Set.of(username), SecurityTokenInvalidTypeEnum.SECURITY_USER_UPDATE, SecurityUserInvalidTokenEnum.JUST_BACK);
            }
        }
    }

}
