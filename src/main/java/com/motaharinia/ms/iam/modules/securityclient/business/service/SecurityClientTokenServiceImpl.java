package com.motaharinia.ms.iam.modules.securityclient.business.service;

import com.motaharinia.ms.iam.config.security.oauth2.authorization.AuthorizationClientTokenProvider;
import com.motaharinia.ms.iam.config.security.oauth2.dto.BearerTokenDto;
import com.motaharinia.ms.iam.config.security.oauth2.dto.LoggedInClientDto;
import com.motaharinia.ms.iam.config.security.oauth2.resource.ResourceClientTokenProvider;
import com.motaharinia.ms.iam.modules.securityclient.business.exception.SecurityClientTokenException;
import com.motaharinia.ms.iam.modules.securityclient.business.mapper.SecurityClientTokenMapper;
import com.motaharinia.ms.iam.modules.securityclient.persistence.orm.SecurityClient;
import com.motaharinia.ms.iam.modules.securityclient.persistence.orm.SecurityClientRepository;
import com.motaharinia.ms.iam.modules.securityclient.persistence.orm.SecurityClientToken;
import com.motaharinia.ms.iam.modules.securityclient.persistence.orm.SecurityClientTokenRepository;
import com.motaharinia.ms.iam.modules.securityclient.presentation.securityclienttoken.SecurityClientTokenCreateDto;
import com.motaharinia.ms.iam.modules.securityclient.presentation.securityclienttoken.SecurityClientTokenDto;
import com.motaharinia.ms.iam.modules.securityuser.business.enumeration.SecurityTokenInvalidTypeEnum;
import com.motaharinia.msjpautility.page.CustomPageResponseDto;
import com.motaharinia.msutility.tools.network.NetworkTools;
import com.motaharinia.msutility.tools.network.requestinfo.RequestInfoDto;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * کلاس پیاده سازی سرویس توکن امنیت
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class SecurityClientTokenServiceImpl implements SecurityClientTokenService {

    private final SecurityClientTokenRepository securityClientTokenRepository;
    private final SecurityClientRepository securityClientRepository;
    private final SecurityClientTokenMapper securityClientTokenMapper;
    private final ResourceClientTokenProvider resourceClientTokenProvider;
    private final AuthorizationClientTokenProvider authorizationClientTokenProvider;

    private static final String BUSINESS_EXCEPTION_SECURITY_TOKEN_REFRESH_TOKEN_IS_INVALID = "BUSINESS_EXCEPTION.SECURITY_TOKEN_REFRESH_TOKEN_IS_INVALID";
    private static final String BUSINESS_EXCEPTION_SECURITY_TOKEN_INVALID_CLAIM = "BUSINESS_EXCEPTION.SECURITY_TOKEN_INVALID_CLAIM";
    private static final String BUSINESS_EXCEPTION_SECURITY_TOKEN_USER_NOT_LOGGED_IN = "BUSINESS_EXCEPTION.USER_NOT_LOGGED_IN";


    public SecurityClientTokenServiceImpl(SecurityClientTokenRepository securityClientTokenRepository, SecurityClientRepository securityClientRepository, SecurityClientTokenMapper securityClientTokenMapper, ResourceClientTokenProvider resourceClientTokenProvider, AuthorizationClientTokenProvider authorizationClientTokenProvider) {
        this.securityClientTokenRepository = securityClientTokenRepository;
        this.securityClientRepository = securityClientRepository;
        this.securityClientTokenMapper = securityClientTokenMapper;
        this.resourceClientTokenProvider = resourceClientTokenProvider;
        this.authorizationClientTokenProvider = authorizationClientTokenProvider;
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
    public CustomPageResponseDto<SecurityClientTokenDto> readAll(@NotNull LocalDateTime fromDate, @NotNull LocalDateTime toDate, @NotNull Pageable pageable) {
        Optional<LoggedInClientDto> loggedInClientDtoOptional = resourceClientTokenProvider.getLoggedInDto();
        if (loggedInClientDtoOptional.isEmpty()) {
            throw new SecurityClientTokenException("", BUSINESS_EXCEPTION_SECURITY_TOKEN_USER_NOT_LOGGED_IN, "");
        }
        Page<SecurityClientTokenDto> securityTokenPage = securityClientTokenRepository.findAllByUsernameAndCreateAtBetween(loggedInClientDtoOptional.get().getClientId(), fromDate, toDate, pageable).map(securityClientTokenMapper::toDto);
        return new CustomPageResponseDto<>(securityTokenPage);
    }

    /**
     * متد تولید توکن برای فرانت
     *
     * @param loggedInClientDto      مدل کلاینت لاگین شده
     * @param additionalClaimHashMap هش مپ اطلاعات دیگر مورد نیاز در توکن
     * @param expiresIn              تاریخ انقضای توکن
     * @param refreshTokenExpiredAt  تاریخ انقضای ریفرش توکن     * @return خروجی:مدل تولید توکن احراز هویت
     */
    @Override
    public @NotNull BearerTokenDto createClientBearerToken(LoggedInClientDto loggedInClientDto, HashMap<String, Object> additionalClaimHashMap, long expiresIn, long refreshTokenExpiredAt) {
        String refreshToken = Instant.now().toEpochMilli() + "_" + UUID.randomUUID().toString();

        String accessToken = authorizationClientTokenProvider.createAccessToken(loggedInClientDto, additionalClaimHashMap, expiresIn);

        //زمانیکه سکیوریتی توکن تولید میشود باید در دیتابیس لاگ شود
        SecurityClientTokenCreateDto securityTokenDto = new SecurityClientTokenCreateDto(
                loggedInClientDto.getClientId(),
                accessToken,
                LocalDateTime.now(),
                LocalDateTime.now().plusSeconds(expiresIn),
                refreshToken,
                LocalDateTime.now().plusSeconds(refreshTokenExpiredAt)
        );

        this.serviceCreate(securityTokenDto);

        return new BearerTokenDto(refreshToken, accessToken, "Bearer", expiresIn);
    }

    /**
     * ثبت توکن امنیت - زمانی که توکن جدیدی تولید میشود این متد فراخوانی میشود
     *
     * @param dto مدل ثبت توکن امنیت
     */
    private void serviceCreate(SecurityClientTokenCreateDto dto) {
        SecurityClientToken securityClientToken = new SecurityClientToken();

        //ست کردن اطلاعات مربوط به user-agent
        RequestInfoDto requestInfoDto = NetworkTools.readCurrentRequestInfo(true);
        securityClientToken.setIpAddress(requestInfoDto.getIpAddress());

        securityClientToken.setUsername(dto.getUsername());
        securityClientToken.setAccessToken(dto.getAccessToken());
        securityClientToken.setIssuedAt(dto.getIssuedAt());
        securityClientToken.setExpiredAt(dto.getExpiredAt());
        securityClientToken.setRefreshToken(dto.getRefreshToken());
        securityClientToken.setRefreshTokenExpiredAt(dto.getRefreshTokenExpiredAt());

        securityClientTokenRepository.save(securityClientToken);

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
        SecurityClientToken securityClientToken = securityClientTokenRepository.findByRefreshTokenAndInvalidIsFalseAndRefreshTokenExpiredAtIsGreaterThanEqual(refreshToken, localDateTime).orElseThrow(() -> new SecurityClientTokenException("", BUSINESS_EXCEPTION_SECURITY_TOKEN_REFRESH_TOKEN_IS_INVALID, ""));

        //جستجوی securityClient با کلمه کاربری
        SecurityClient securityClient = securityClientRepository.findByClientId(securityClientToken.getUsername()).orElseThrow(() -> new SecurityClientTokenException("", BUSINESS_EXCEPTION_SECURITY_TOKEN_REFRESH_TOKEN_IS_INVALID, ""));


        String accessToken;
        long expiresIn;

        //ایجاد مدل SecurityClientTokenException موردنظر جهت ثبت توکن جدید
        Claims claims = resourceClientTokenProvider.getClaims(securityClientToken.getAccessToken(), false);
        Optional<LoggedInClientDto> loggedInUserDtoOptional = Optional.ofNullable(claims.get(ResourceClientTokenProvider.CUSTOM_CLAIM_LOGGED_IN_CLIENT, LoggedInClientDto.class));
        if (loggedInUserDtoOptional.isEmpty()) {
            throw new SecurityClientTokenException("", BUSINESS_EXCEPTION_SECURITY_TOKEN_INVALID_CLAIM, "");
        }
        LoggedInClientDto loggedInClientDto = loggedInUserDtoOptional.get();


        //تبدیل کلیم ها به هشمپ
        HashMap<String, Object> additionalClaimHashMap = new HashMap();
        /*for (Map.Entry<String, Object> entry : claims.entrySet()) {
            if (!entry.getKey().equals(AuthorizationTokenProvider.CUSTOM_CLAIM_LOGGED_IN_USER))
                additionalClaimMap.put(entry.getKey(), entry.getValue());
        }*/

        expiresIn = securityClient.getAccessTokenValiditySeconds();

        //تولید توکن جدید و به روز رسانی سطر فعلی دیتابیس
        accessToken = authorizationClientTokenProvider.createAccessToken(loggedInClientDto, additionalClaimHashMap, expiresIn);

        securityClientToken.setAccessToken(accessToken);
        securityClientToken.setIssuedAt(localDateTime);
        securityClientToken.setExpiredAt(LocalDateTime.now().plusSeconds(expiresIn));
        securityClientToken.setRefreshTokenExpiredAt(localDateTime.plusSeconds(securityClient.getRefreshTokenValiditySeconds()));

        //ست کردن اطلاعات مربوط به user-agent
        RequestInfoDto requestInfoDto = NetworkTools.readCurrentRequestInfo(true);
        securityClientToken.setIpAddress(requestInfoDto.getIpAddress());

        securityClientTokenRepository.save(securityClientToken);

        //ایجاد توکن جدید
        return new BearerTokenDto(refreshToken, accessToken, "Bearer", expiresIn);

    }

    /**
     * غیرفعال کردن توکن هایی که  تاریخ انقضای توکنشان به اتمام رسیده است
     */
    @Async
    @Override
    public void scheduleInvalidRefreshTokenByExpiration() {
        LocalDateTime now = LocalDateTime.now();
        Optional<List<SecurityClientToken>> securityTokenOptionalList = securityClientTokenRepository.findByInvalidIsFalseAndRefreshTokenExpiredAtIsLessThan(now);

        if (securityTokenOptionalList.isPresent()) {
            List<SecurityClientToken> securityClientTokenList = securityTokenOptionalList.get();
            for (SecurityClientToken securityClientToken : securityClientTokenList) {
                // غیرفعال کردن توکن
                this.serviceInvalid(securityClientToken, SecurityTokenInvalidTypeEnum.REFRESH_TOKEN_EXPIRATION, LocalDateTime.now());

            }
        }
    }

    /**
     * غیرفعال کردن توکن
     *
     * @param securityClientToken            انتیتی سکیوریتی توکن
     * @param securityTokenInvalidTypeEnum علت غیرفعال شدن توکن
     * @param localDateTime                تاریخ غیرفعال شدن
     */
    private void serviceInvalid(SecurityClientToken securityClientToken, SecurityTokenInvalidTypeEnum securityTokenInvalidTypeEnum, LocalDateTime localDateTime) {
        securityClientToken.setInvalid(true);
        securityClientToken.setInvalidDate(localDateTime);
        securityClientToken.setInvalidEnum(securityTokenInvalidTypeEnum);
        securityClientTokenRepository.save(securityClientToken);
    }
}
