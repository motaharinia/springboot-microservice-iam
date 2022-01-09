package com.motaharinia.ms.iam.modules.securityuser.business.service;

import com.motaharinia.ms.iam.config.caching.CachingConfiguration;
import com.motaharinia.ms.iam.config.security.oauth2.authorization.AuthorizationClientTokenProvider;
import com.motaharinia.ms.iam.config.security.oauth2.authorization.AuthorizationUserTokenProvider;
import com.motaharinia.ms.iam.config.security.oauth2.dto.BearerTokenDto;
import com.motaharinia.ms.iam.config.security.oauth2.dto.LoggedInUserDto;
import com.motaharinia.ms.iam.config.security.oauth2.resource.ResourceUserTokenProvider;
import com.motaharinia.ms.iam.external.userpanel.business.service.UserPanelExternalCallService;
import com.motaharinia.ms.iam.external.userpanel.presentation.dto.DashboardDto;
import com.motaharinia.ms.iam.modules.securityuser.business.enumeration.SecurityTokenInvalidTypeEnum;
import com.motaharinia.ms.iam.modules.securityuser.business.enumeration.SecurityUserInvalidTokenEnum;
import com.motaharinia.ms.iam.modules.securityuser.business.exception.SecurityUserTokenException;
import com.motaharinia.ms.iam.modules.securityuser.business.mapper.SecurityUserTokenMapper;
import com.motaharinia.ms.iam.modules.securityuser.persistence.orm.SecurityUserToken;
import com.motaharinia.ms.iam.modules.securityuser.persistence.orm.SecurityUserTokenRepository;
import com.motaharinia.ms.iam.modules.securityuser.presentation.securityusertoken.SecurityUserTokenCreateDto;
import com.motaharinia.ms.iam.modules.securityuser.presentation.securityusertoken.SecurityUserTokenDto;
import com.motaharinia.msjpautility.page.CustomPageResponseDto;
import com.motaharinia.msutility.tools.network.NetworkTools;
import com.motaharinia.msutility.tools.network.requestinfo.RequestInfoDto;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * کلاس پیاده سازی سرویس توکن امنیت
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class SecurityUserTokenServiceImpl implements SecurityUserTokenService {

    private final SecurityUserTokenRepository securityUserTokenRepository;
    private final SecurityUserTokenMapper securityUserTokenMapper;
    private final ResourceUserTokenProvider resourceUserTokenProvider;
    private final AuthorizationUserTokenProvider authorizationUserTokenProvider;
    private final AuthorizationClientTokenProvider authorizationClientTokenProvider;
    private final RedissonClient redissonClient;
    private final UserPanelExternalCallService userPanelExternalCallService;

    private static final String BUSINESS_EXCEPTION_SECURITY_TOKEN_NOT_FOUND = "BUSINESS_EXCEPTION.SECURITY_TOKEN_NOT_FOUND";
    private static final String BUSINESS_EXCEPTION_SECURITY_TOKEN_REFRESH_TOKEN_IS_INVALID = "BUSINESS_EXCEPTION.SECURITY_TOKEN_REFRESH_TOKEN_IS_INVALID";
    private static final String BUSINESS_EXCEPTION_SECURITY_TOKEN_INVALID_CLAIM = "BUSINESS_EXCEPTION.SECURITY_TOKEN_INVALID_CLAIM";
    private static final String BUSINESS_EXCEPTION_SECURITY_TOKEN_USER_NOT_LOGGED_IN = "BUSINESS_EXCEPTION.USER_NOT_LOGGED_IN";

    /**
     * تعداد سطرهای مجازی که برای هر یوزر در تیبل سکیوریتی توکن میتواند بماند
     * و اگر بیشتر از این تعداد شود باید سطرهای قبلی حذف شوند
     */
    @Value("${app.security.security-token-max-history}")
    private Long MAX_HISTORY;

    /**
     * مدت زمان عمر برای توکن
     */
    @Value("${app.security.token-validity-seconds}")
    private long TOKEN_VALIDITY_SECONDS;

    /**
     * مدت زمان عمر برای rememberMe توکن
     */
    @Value("${app.security.token-validity-seconds-remember-me}")
    private long TOKEN_VALIDITY_SECONDS_REMEMBER_ME;

    /**
     * مدت زمان عمر برای رفرش توکن
     */
    @Value("${app.security.refresh.token-validity-seconds}")
    private long REFRESH_TOKEN_VALIDITY_SECONDS;

    public SecurityUserTokenServiceImpl(SecurityUserTokenRepository securityUserTokenRepository, SecurityUserTokenMapper securityUserTokenMapper, ResourceUserTokenProvider resourceUserTokenProvider, AuthorizationUserTokenProvider authorizationUserTokenProvider, AuthorizationClientTokenProvider authorizationClientTokenProvider, RedissonClient redissonClient, UserPanelExternalCallService userPanelExternalCallService) {
        this.securityUserTokenRepository = securityUserTokenRepository;
        this.securityUserTokenMapper = securityUserTokenMapper;
        this.resourceUserTokenProvider = resourceUserTokenProvider;
        this.authorizationUserTokenProvider = authorizationUserTokenProvider;
        this.authorizationClientTokenProvider = authorizationClientTokenProvider;
        this.redissonClient = redissonClient;
        this.userPanelExternalCallService = userPanelExternalCallService;
    }


    /**
     * جستجو توکن امنیت از تاریخ تا تاریخ موردنظر
     *
     * @param fromDate از تاریخ
     * @param toDate   تا تاریخ
     * @param pageable صفحه بندی
     * @return CustomPageResponseDto<SecurityTokenDto>
     */
    @Override
    public CustomPageResponseDto<SecurityUserTokenDto> readAll(@NotNull LocalDateTime fromDate, @NotNull LocalDateTime toDate, @NotNull Pageable pageable) {
        Optional<LoggedInUserDto> loggedInUserDtoOptional = resourceUserTokenProvider.getLoggedInDto();
        if (loggedInUserDtoOptional.isEmpty()) {
            throw new SecurityUserTokenException("", BUSINESS_EXCEPTION_SECURITY_TOKEN_USER_NOT_LOGGED_IN, "");
        }
        Page<SecurityUserTokenDto> securityTokenPage = securityUserTokenRepository.findAllByUsernameAndCreateAtBetween(loggedInUserDtoOptional.get().getUsername(), fromDate, toDate, pageable).map(securityUserTokenMapper::toDto);
        return new CustomPageResponseDto<>(securityTokenPage);
    }


    /**
     * متد تولید توکن برای فرانت
     *
     * @param loggedInUserDto        مدل کاربر لاگین شده
     * @param rememberMe             آیا به خاطر بماند؟
     * @param additionalClaimHashMap هش مپ اطلاعات دیگر مورد نیاز در توکن
     * @param referenceId            زمانی که توکن جدید تولید میشود اگر از توکن دیگری استفاده کرده باشد آیدی توکن استفاده شده در این فیلد قرار میگیرد
     * @param isFront                آیا توکن برای فرانت است؟
     * @return خروجی:مدل تولید توکن احراز هویت
     */
    @Override
    @NotNull
    public BearerTokenDto createBearerToken(LoggedInUserDto loggedInUserDto, boolean rememberMe, HashMap<String, Object> additionalClaimHashMap, Long referenceId, Boolean isFront) {
        String refreshToken = Instant.now().toEpochMilli() + "_" + UUID.randomUUID().toString();

        long expiresIn = TOKEN_VALIDITY_SECONDS;
        if (rememberMe) {
            expiresIn = TOKEN_VALIDITY_SECONDS_REMEMBER_ME;
        }

        String accessToken = authorizationUserTokenProvider.createAccessToken(loggedInUserDto, additionalClaimHashMap, expiresIn);

        //زمانیکه سکیوریتی توکن تولید میشود باید در دیتابیس لاگ شود
        SecurityUserTokenCreateDto securityTokenDto = new SecurityUserTokenCreateDto(
                loggedInUserDto.getUsername(),
                accessToken,
                LocalDateTime.now(),
                LocalDateTime.now().plusSeconds(expiresIn),
                refreshToken,
                LocalDateTime.now().plusSeconds(REFRESH_TOKEN_VALIDITY_SECONDS),
                rememberMe,
                referenceId,
                isFront
        );

        this.serviceCreate(securityTokenDto);

        return new BearerTokenDto(refreshToken, accessToken, "Bearer", expiresIn);
    }


    /**
     * ثبت توکن امنیت - زمانی که توکن جدیدی تولید میشود این متد فراخوانی میشود
     *
     * @param dto مدل ثبت توکن امنیت
     */
    private void serviceCreate(SecurityUserTokenCreateDto dto) {
        SecurityUserToken securityUserToken = new SecurityUserToken();

        //ست کردن اطلاعات مربوط به user-agent
        RequestInfoDto requestInfoDto = NetworkTools.readCurrentRequestInfo(true);
        securityUserToken.setBrowser(requestInfoDto.getBrowser());
        securityUserToken.setBrowserVersion(requestInfoDto.getBrowserVersion());
        securityUserToken.setOperatingSystem(requestInfoDto.getOperatingSystem());
        securityUserToken.setDeviceType(requestInfoDto.getOperatingSystem());
        securityUserToken.setIpAddress(requestInfoDto.getIpAddress());

        securityUserToken.setUsername(dto.getUsername());
        securityUserToken.setAccessToken(dto.getAccessToken());
        securityUserToken.setIssuedAt(dto.getIssuedAt());
        securityUserToken.setExpiredAt(dto.getExpiredAt());
        securityUserToken.setRememberMe(dto.getRememberMe());
        securityUserToken.setRefreshToken(dto.getRefreshToken());
        securityUserToken.setRefreshTokenExpiredAt(dto.getRefreshTokenExpiredAt());
        securityUserToken.setReferenceId(dto.getReferenceId());
        securityUserToken.setIsFront(dto.getIsFront());

        securityUserTokenRepository.save(securityUserToken);

        //ثبت لاگ در ردیس برای appUser هایی که توکن جدید برایشان صادر شده است
        if (!ObjectUtils.isEmpty(dto.getIsFront()) && Boolean.TRUE.equals(dto.getIsFront())) {
            this.updateCountOfOnlineUsers(dto.getUsername(), securityUserToken.getId(), securityUserToken.getExpiredAt(), true);
        }

    }

    /**
     * ایجاد توکن اکسس توکن جدید با یک ریفرش توکن فعال
     *
     * @param refreshToken       رفرش توکن
     * @param httpServletRequest سرولت ریکوئست
     * @return خروجی:مدل تولید توکن احراز هویت
     */
    @Override
    public BearerTokenDto renewToken(String refreshToken, HttpServletRequest httpServletRequest) {

        LocalDateTime localDateTime = LocalDateTime.now();

        //به دست آوردن ریفرش توکن معتبر
        SecurityUserToken securityUserToken = securityUserTokenRepository.findByRefreshTokenAndInvalidIsFalseAndRefreshTokenExpiredAtIsGreaterThanEqual(refreshToken, localDateTime).orElseThrow(() -> new SecurityUserTokenException("", BUSINESS_EXCEPTION_SECURITY_TOKEN_REFRESH_TOKEN_IS_INVALID, ""));

        String accessToken;
        long expiresIn;

        Claims claims = resourceUserTokenProvider.getClaims(securityUserToken.getAccessToken(), false);
        Optional<LoggedInUserDto> loggedInUserDtoOptional = Optional.ofNullable(claims.get(ResourceUserTokenProvider.CUSTOM_CLAIM_LOGGED_IN_USER, LoggedInUserDto.class));
        if (loggedInUserDtoOptional.isEmpty()) {
            throw new SecurityUserTokenException("", BUSINESS_EXCEPTION_SECURITY_TOKEN_INVALID_CLAIM, "");
        }
        LoggedInUserDto loggedInUserDto = loggedInUserDtoOptional.get();
        //تبدیل کلیم ها به هشمپ
        HashMap<String, Object> additionalClaimHashMap = new HashMap();
            /*for (Map.Entry<String, Object> entry : claims.entrySet()) {
                if (!entry.getKey().equals(AuthorizationTokenProvider.CUSTOM_CLAIM_LOGGED_IN_USER))
                    additionalClaimMap.put(entry.getKey(), entry.getValue());
            }*/

        //به دست آوردن مدت اتقضای توکن
        expiresIn = TOKEN_VALIDITY_SECONDS;
        if (Boolean.TRUE.equals(securityUserToken.getRememberMe())) {
            expiresIn = TOKEN_VALIDITY_SECONDS_REMEMBER_ME;
        }

        //تولید توکن جدید و به روز رسانی سطر فعلی دیتابیس
        accessToken = authorizationUserTokenProvider.createAccessToken(loggedInUserDto, additionalClaimHashMap, expiresIn);


        securityUserToken.setAccessToken(accessToken);
        securityUserToken.setIssuedAt(localDateTime);
        securityUserToken.setExpiredAt(LocalDateTime.now().plusSeconds(expiresIn));
        securityUserToken.setRefreshTokenExpiredAt(localDateTime.plusSeconds(REFRESH_TOKEN_VALIDITY_SECONDS));

        //ست کردن اطلاعات مربوط به user-agent
        RequestInfoDto requestInfoDto = NetworkTools.readCurrentRequestInfo(true);
        securityUserToken.setBrowser(requestInfoDto.getBrowser());
        securityUserToken.setBrowserVersion(requestInfoDto.getBrowserVersion());
        securityUserToken.setOperatingSystem(requestInfoDto.getOperatingSystem());
        securityUserToken.setDeviceType(requestInfoDto.getOperatingSystem());
        securityUserToken.setIpAddress(requestInfoDto.getIpAddress());

        securityUserTokenRepository.save(securityUserToken);

        //به روز رسانی لاگ در ردیس برای appUser هایی که توکنشان آپدیت شده است
        if (!ObjectUtils.isEmpty(securityUserToken.getIsFront()) && Boolean.TRUE.equals(securityUserToken.getIsFront())) {
            this.updateCountOfOnlineUsers(securityUserToken.getUsername(), securityUserToken.getId(), securityUserToken.getExpiredAt(), true);
        }

        //ایجاد توکن جدید
        return new BearerTokenDto(refreshToken, accessToken, "Bearer", expiresIn);

    }


    /**
     * مشاهده سشن های فعال کاربر لاگین شده
     *
     * @param pageable اطلاعات صفحه بندی
     * @return CustomPageResponseDto<SecurityTokenDto> خروجی: لیست مدل توکن امنیت
     */
    @Override
    public CustomPageResponseDto<SecurityUserTokenDto> readAllActiveSessionByCurrentUser(Pageable pageable) {
        //بررسی و دریافت اطلاعات کاربر امنیت لاگین شده
        Optional<LoggedInUserDto> loggedInUserDtoOptional = resourceUserTokenProvider.getLoggedInDto();
        if (loggedInUserDtoOptional.isEmpty()) {
            throw new SecurityUserTokenException("", BUSINESS_EXCEPTION_SECURITY_TOKEN_USER_NOT_LOGGED_IN, "");
        }
        LoggedInUserDto loggedInUserDto = loggedInUserDtoOptional.get();

        Page<SecurityUserTokenDto> securityTokenPage = securityUserTokenRepository.findByUsernameAndInvalidIsFalse(loggedInUserDto.getUsername(), pageable).map(securityUserTokenMapper::toDto);

        return new CustomPageResponseDto<>(securityTokenPage);
    }

    /**
     * متد kill کردن رفرش توکن
     *
     * @param refreshToken رفرش توکن
     */
    @Override
    public void terminate(String refreshToken) {
        //بررسی و دریافت اطلاعات کاربر امنیت لاگین شده
        Optional<LoggedInUserDto> loggedInUserDtoOptional = resourceUserTokenProvider.getLoggedInDto();
        if (loggedInUserDtoOptional.isEmpty()) {
            throw new SecurityUserTokenException("", BUSINESS_EXCEPTION_SECURITY_TOKEN_USER_NOT_LOGGED_IN, "");
        }
        LoggedInUserDto loggedInUserDto = loggedInUserDtoOptional.get();

        //جستجوی توکن امنیت
        List<SecurityUserToken> securityUserTokenList = securityUserTokenRepository.findByRefreshTokenAndUsernameAndInvalidIsFalse(refreshToken, loggedInUserDto.getUsername())
                .orElseThrow(() -> new SecurityUserTokenException("", BUSINESS_EXCEPTION_SECURITY_TOKEN_NOT_FOUND, ""));

        for (SecurityUserToken securityUserToken : securityUserTokenList) {
            // غیرفعال کردن توکن
            this.serviceInvalid(securityUserToken, SecurityTokenInvalidTypeEnum.REFRESH_TOKEN_TERMINATE, LocalDateTime.now());

            //حذف لاگ از ردیس برای افرادی که توکن شان منقضی شده است
            this.updateCountOfOnlineUsers(securityUserToken.getUsername(), securityUserToken.getId(), securityUserToken.getExpiredAt(), false);
        }
    }

    /**
     * خارج شدن از حساب کاربری
     *
     * @param httpServletRequest سرولت ریکوئست
     */
    @Override
    public void logout(HttpServletRequest httpServletRequest) {
        String accessToken = resourceUserTokenProvider.resolveAccessToken(httpServletRequest);
        if (StringUtils.isEmpty(accessToken)) {
            throw new SecurityUserTokenException("", BUSINESS_EXCEPTION_SECURITY_TOKEN_USER_NOT_LOGGED_IN, "");
        }

        //جستجوی توکن امنیت
        List<SecurityUserToken> securityUserTokenList = securityUserTokenRepository.findByAccessToken(accessToken)
                .orElseThrow(() -> new SecurityUserTokenException("", BUSINESS_EXCEPTION_SECURITY_TOKEN_NOT_FOUND, ""));

        for (SecurityUserToken securityUserToken : securityUserTokenList) {
            // غیرفعال کردن توکن
            this.serviceInvalid(securityUserToken, SecurityTokenInvalidTypeEnum.LOGOUT, LocalDateTime.now());

            //حذف لاگ از ردیس برای افرادی که توکن شان منقضی شده است
            this.updateCountOfOnlineUsers(securityUserToken.getUsername(), securityUserToken.getId(), securityUserToken.getExpiredAt(), false);
        }
    }

    /**
     * غیرفعال کردن توکن هایی که  تاریخ انقضای توکنشان به اتمام رسیده است
     */
    @Async
    @Override
    public void scheduleInvalidRefreshTokenByExpiration() {
        LocalDateTime now = LocalDateTime.now();
        Optional<List<SecurityUserToken>> securityTokenOptionalList = securityUserTokenRepository.findByInvalidIsFalseAndRefreshTokenExpiredAtIsLessThan(now);

        if (securityTokenOptionalList.isPresent()) {
            List<SecurityUserToken> securityUserTokenList = securityTokenOptionalList.get();
            for (SecurityUserToken securityUserToken : securityUserTokenList) {
                // غیرفعال کردن توکن
                this.serviceInvalid(securityUserToken, SecurityTokenInvalidTypeEnum.REFRESH_TOKEN_EXPIRATION, LocalDateTime.now());

                //حذف لاگ از ردیس برای افرادی که توکن شان منقضی شده است
                this.updateCountOfOnlineUsers(securityUserToken.getUsername(), securityUserToken.getId(), securityUserToken.getExpiredAt(), false);
            }
        }
    }


    /**
     * متد غیر فعال کردن توکن با کلمه کاربری
     * این متد بصورت درون سرویسی و از ماژول هایی مانند AppUser , BackUse,SecurityRole,SecurityPermission در زمان غیرفعال شدن یا حذف شدن فراخوانی میشوند
     *
     * @param usernameSet                  لیست کلمه کاربری
     * @param securityTokenInvalidTypeEnum علت غیرفعال شدن توکن
     * @param securityUserInvalidTokenEnum کدام توکن کاربر باید غیرفعال شوند؟ توکن های بک کاربر یا توکن های فرانت کاربر یا هردو
     */
    public void serviceInvalid(Set<String> usernameSet, SecurityTokenInvalidTypeEnum securityTokenInvalidTypeEnum, SecurityUserInvalidTokenEnum securityUserInvalidTokenEnum) {
        List<SecurityUserToken> securityUserTokenOptionalList = null;
        switch (securityUserInvalidTokenEnum) {
            case BOTH:
                securityUserTokenOptionalList = securityUserTokenRepository.findByUsernameInAndInvalidIsFalse(usernameSet);
                break;
            case JUST_BACK:
                securityUserTokenOptionalList = securityUserTokenRepository.findByUsernameInAndInvalidIsFalseAndIsFrontIsFalse(usernameSet);
                break;
            case JUST_FRONT:
                securityUserTokenOptionalList = securityUserTokenRepository.findByUsernameInAndInvalidIsFalseAndIsFrontIsTrue(usernameSet);
                break;
        }
        if (!CollectionUtils.isEmpty(securityUserTokenOptionalList)) {
            securityUserTokenOptionalList.forEach(securityUserToken -> {
                // غیرفعال کردن توکن
                this.serviceInvalid(securityUserToken, securityTokenInvalidTypeEnum, LocalDateTime.now());
            });
        }
    }

    /**
     * غیرفعال کردن توکن
     *
     * @param securityUserToken            انتیتی سکیوریتی توکن
     * @param securityTokenInvalidTypeEnum علت غیرفعال شدن توکن
     * @param localDateTime                تاریخ غیرفعال شدن
     */
    private void serviceInvalid(SecurityUserToken securityUserToken, SecurityTokenInvalidTypeEnum securityTokenInvalidTypeEnum, LocalDateTime localDateTime) {
        securityUserToken.setInvalid(true);
        securityUserToken.setInvalidDate(localDateTime);
        securityUserToken.setInvalidEnum(securityTokenInvalidTypeEnum);
        securityUserTokenRepository.save(securityUserToken);
    }

    /**
     * متد گرفتن هش مپ کاربران آنلاین از ردیس
     *
     * @return RBucket<HashMap < String, HashMap < String, Long>>>
     * HashMap<String, HashMap<String, Long>> ====> HashMap<username, HashMap<securityTokenId, expiredAt>>
     */
    private RBucket<ConcurrentHashMap<String, ConcurrentHashMap<String, Long>>> getTryBucket() {
        String bucketKey = CachingConfiguration.REDIS_IAM_LOGIN_INFO;
        RBucket<ConcurrentHashMap<String, ConcurrentHashMap<String, Long>>> readRBucket = redissonClient.getBucket(bucketKey);
        // اگر برای اولین بار متد را فراخوانی کرده است
        if (redissonClient.getKeys().countExists(bucketKey) == 0) {
            readRBucket.set(new ConcurrentHashMap<>());
        }
        return readRBucket;
    }

    /**
     * به روز رسانی تعداد کاربران آنلاین در ردیس
     * زمانی که توکن جدید ایجاد میشود در ردیس ثبت میشود و زمانی که توکنی غیرفعال میشود از ردیس حذف میشود
     *
     * @param username  نام کاربری
     * @param id        شناسه SecurityToken
     * @param expiredAt تاریخ انقضا
     * @param isCreate  آیا عملیات ثبت در ردیس انجام شود یا حذف از ردیس؟
     */
    public void updateCountOfOnlineUsers(@NotNull String username, @NotNull Long id, @NotNull LocalDateTime expiredAt, @NotNull Boolean isCreate) {

        RBucket<ConcurrentHashMap<String, ConcurrentHashMap<String, Long>>> readRBucket = this.getTryBucket();
        ConcurrentHashMap<String, ConcurrentHashMap<String, Long>> onLineAppUsersMap = readRBucket.get();
        String idString = id.toString();
        //عملیات ثبت- زمانی که توکن جدید ایجاد میشود در ردیس ثبت میشود
        if (Boolean.TRUE.equals(isCreate)) {
            Long expiredAtLong = expiredAt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
            if (onLineAppUsersMap.containsKey(username)) {
                onLineAppUsersMap.get(username).put(idString, expiredAtLong);
            } else {
                ConcurrentHashMap<String, Long> newMap = new ConcurrentHashMap<>();
                newMap.put(idString, expiredAtLong);
                onLineAppUsersMap.put(username, newMap);
            }
        } else {//عملیات حذف- زمانی که توکنی غیرفعال میشود از ردیس حذف میشود
            //پاک کردن از مپ با شناسه
            if (onLineAppUsersMap.containsKey(username) || onLineAppUsersMap.get(username).containsKey(idString)) {
                onLineAppUsersMap.get(username).remove(idString);
            }
            //در انتها اگر مقداری در هش مپ اولی نبود خود هش مپ هم پاک میشود
            if (onLineAppUsersMap.get(username).size() == 0) {
                onLineAppUsersMap.remove(username);
            }
        }
        readRBucket.set(onLineAppUsersMap);
    }

    /**
     * متد اسکجل جهت پاک کردن توکن هایی که تاریخ انقضایشان (expiredAt) به اتمام رسیده است از ردیس
     */
    public void scheduleReportOnlineUsers() {

        RBucket<ConcurrentHashMap<String, ConcurrentHashMap<String, Long>>> readRBucket = this.getTryBucket();

        ConcurrentHashMap<String, ConcurrentHashMap<String, Long>> onLineAppUsersMap = readRBucket.get();

        //لوپ روی هش مپ کاربران آنلاین که در ردیس ذخیره شده است
        for (Map.Entry<String, ConcurrentHashMap<String, Long>> entry : onLineAppUsersMap.entrySet()) {
            String keyUsername = entry.getKey();
            ConcurrentHashMap<String, Long> valueMap = entry.getValue();

            //پاک کردن هشمپ هایی که تاریخ انقضایشان از تاریخ جاری گذشته است
            for (Map.Entry<String, Long> innerEntry : valueMap.entrySet()) {
                String keySecurityTokenId = innerEntry.getKey();
                Long expiredAt = innerEntry.getValue();

                if (LocalDateTime.ofInstant(Instant.ofEpochMilli(expiredAt), ZoneId.systemDefault()).isBefore(LocalDateTime.now()))
                    onLineAppUsersMap.get(keyUsername).remove(keySecurityTokenId);
            }

            //در انتها اگر مقداری در هش مپ اولی نبود خود هش مپ هم پاک میشود
            if (onLineAppUsersMap.get(keyUsername).size() == 0) {
                onLineAppUsersMap.remove(keyUsername);
            }

        }
        readRBucket.set(onLineAppUsersMap);

        Integer onLineUsers = onLineAppUsersMap.size();

        //فراخوانی متد بک تو بک یوزرپنل
        userPanelExternalCallService.dashboard(new DashboardDto(null, "USER", String.valueOf(onLineUsers)));

    }

}
