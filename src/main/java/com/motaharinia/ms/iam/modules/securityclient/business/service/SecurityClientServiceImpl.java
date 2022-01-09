package com.motaharinia.ms.iam.modules.securityclient.business.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.motaharinia.ms.iam.config.security.oauth2.dto.BearerTokenDto;
import com.motaharinia.ms.iam.config.security.oauth2.dto.LoggedInClientDto;
import com.motaharinia.ms.iam.modules.securityclient.business.enumeration.AuthorityEnum;
import com.motaharinia.ms.iam.modules.securityclient.business.enumeration.GrantTypeEnum;
import com.motaharinia.ms.iam.modules.securityclient.business.exception.SecurityClientException;
import com.motaharinia.ms.iam.modules.securityclient.business.mapper.SecurityClientMapper;
import com.motaharinia.ms.iam.modules.securityclient.persistence.orm.SecurityClient;
import com.motaharinia.ms.iam.modules.securityclient.persistence.orm.SecurityClientRepository;
import com.motaharinia.ms.iam.modules.securityclient.presentation.SecurityClientRequestDto;
import com.motaharinia.ms.iam.modules.securityclient.presentation.SecurityClientResponseDto;
import com.motaharinia.ms.iam.modules.securityclient.presentation.UpdateSecurityClientRequestDto;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Primary;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.ClientRegistrationException;
import org.springframework.security.oauth2.provider.client.BaseClientDetails;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author eng.motahari@gmail.com<br>
 * کلاس پیاده سازی سرویس کاربر کلاینت
 */

//https://blog.couchbase.com/oauth-2-dynamic-client-registration/

@Slf4j
@Primary
@Service
public class SecurityClientServiceImpl implements SecurityClientService, ClientDetailsService {

    private PasswordEncoder passwordEncoder;
    private SecurityClientMapper securityClientMapper;
    private final SecurityClientRepository securityClientRepository;
    private final ObjectMapper objectMapper;
    private final SecurityClientTokenService securityClientTokenService;

    private static final String BUSINESS_EXCEPTION_SECURITY_CLIENT_ID_NOT_FOUND = "BUSINESS_EXCEPTION.SECURITY_CLIENT_ID_NOT_FOUND";
    private static final String BUSINESS_EXCEPTION_SECURITY_CLIENT_PASSWORD_IS_INVALID = "BUSINESS_EXCEPTION.SECURITY_CLIENT_PASSWORD_IS_INVALID";

    public SecurityClientServiceImpl(PasswordEncoder passwordEncoder, SecurityClientMapper securityClientMapper, SecurityClientRepository securityClientRepository, ObjectMapper objectMapper, SecurityClientTokenService securityClientTokenService) {
        this.passwordEncoder = passwordEncoder;
        this.securityClientMapper = securityClientMapper;
        this.securityClientRepository = securityClientRepository;
        this.objectMapper = objectMapper;
        this.securityClientTokenService = securityClientTokenService;
    }

    /**
     * متد جستجوی کاربر کلاینت مطابق شناسه آن
     *
     * @param clientId شناسه کلاینت
     * @return خروجی: مدل کاربر کلاینت
     * @throws ClientRegistrationException خطا
     */
    @Override
    public ClientDetails loadClientByClientId(String clientId) throws ClientRegistrationException {
        //جستجوی کاربر کلاینت
        Optional<SecurityClient> securityClientOptional = securityClientRepository.findByClientId(clientId);
        //بررسی وجود
        if (securityClientOptional.isEmpty()) {
            throw new SecurityClientException(clientId, BUSINESS_EXCEPTION_SECURITY_CLIENT_ID_NOT_FOUND, "");
        }
        //تبدیل اطلاعات انتیتی به صورت csv برای ثبت در مدل
        SecurityClient securityClient = securityClientOptional.get();
        String resourceIds = String.join(",", securityClient.getResourceIdSet());
        String scopes = String.join(",", securityClient.getScopeSet());
        String grantTypes = String.join(",", securityClient.getAuthorizedGrantTypeSet());
        String authorities = String.join(",", securityClient.getAuthoritySet());
        //ایجاد و خروجی مدل کاربر کلاینت طبق اطلاعات انتیتی
        BaseClientDetails baseClientDetails = new BaseClientDetails(securityClient.getClientId(), resourceIds, scopes, grantTypes, authorities);
        baseClientDetails.setClientSecret(securityClient.getClientSecret());
        baseClientDetails.setAccessTokenValiditySeconds(securityClient.getAccessTokenValiditySeconds());
        baseClientDetails.setRefreshTokenValiditySeconds(securityClient.getRefreshTokenValiditySeconds());
        baseClientDetails.setAdditionalInformation(new HashMap<>());
        baseClientDetails.setAutoApproveScopes(new HashSet<>());

        return baseClientDetails;
    }

    /**
     * client ثبت کاربر
     *
     * @param requestDto client مدل ثبت کاربر
     * @return خروجی: client مدل پاسخ
     */
    @Override
    public SecurityClientResponseDto create(SecurityClientRequestDto requestDto) {
        SecurityClient securityClient = securityClientMapper.toEntity(requestDto);
        if (!ObjectUtils.isEmpty(requestDto.getClientSecret())) {
            securityClient.setSecretRequired(true);
            securityClient.setClientSecret(passwordEncoder.encode(requestDto.getClientSecret()));
        }
        //بررسی معتبر بودن مقدار دسترسی
        securityClient.setAuthoritySet(requestDto.getAuthoritySet().stream().filter(s -> {
            if (ObjectUtils.isEmpty(AuthorityEnum.valueOf(s)))
                return false;
            return true;
        }).collect(Collectors.toSet()));
        HashSet<String> grantTypSet = new HashSet<>();
        grantTypSet.add(GrantTypeEnum.CLIENT_CREDENTIAL.getValue());
        securityClient.setAuthorizedGrantTypeSet(grantTypSet);
        if (ObjectUtils.isEmpty(requestDto.getRefreshTokenValiditySeconds()))
            securityClient.setRefreshTokenValiditySeconds(21600);
        if (ObjectUtils.isEmpty(requestDto.getAccessTokenValiditySeconds()))
            securityClient.setAccessTokenValiditySeconds(3600);
        HashSet<String> scopeSet = new HashSet<>();
        scopeSet.add("all");
        securityClient.setScopeSet(scopeSet);

        securityClient = securityClientRepository.save(securityClient);

        return securityClientMapper.toDto(securityClient);
    }

    /**
     * با شناسه و رمز عبور client جستجوی کاربر
     *
     * @param clientId     شناسه کاربر
     * @param clientSecret رمز عبور کاربر
     * @return خروجی: client مدل پاسخ
     */
    @Override
    public SecurityClientResponseDto readByIdAndSecret(@NotNull String clientId, @NotNull String clientSecret) {
        //جستجوی کاربر کلاینت
        Optional<SecurityClient> securityClientOptional = securityClientRepository.findByClientId(clientId);
        //بررسی وجود
        if (securityClientOptional.isEmpty()) {
            throw new SecurityClientException(clientId, BUSINESS_EXCEPTION_SECURITY_CLIENT_ID_NOT_FOUND, "");
        }
        //بررسی رمز عبور
        SecurityClient securityClient = securityClientOptional.get();
        if (securityClient.getSecretRequired() && !passwordEncoder.matches(clientSecret, securityClient.getClientSecret()))
            throw new SecurityClientException(clientId, BUSINESS_EXCEPTION_SECURITY_CLIENT_PASSWORD_IS_INVALID, "");

        return securityClientMapper.toDto(securityClient);
    }

    /**
     * با شناسه و رمز عبور client جستجوی کاربر
     *
     * @param clientId شناسه کاربر
     * @return خروجی: client اینتیتی پاسخ
     */
    @Override
    public SecurityClient serviceReadClientById(@NotNull String clientId) {
        //جستجوی کاربر کلاینت
        Optional<SecurityClient> securityClientOptional = securityClientRepository.findByClientId(clientId);
        //بررسی وجود
        if (securityClientOptional.isEmpty()) {
            throw new SecurityClientException(clientId, BUSINESS_EXCEPTION_SECURITY_CLIENT_ID_NOT_FOUND, "");
        }
        //بررسی رمز عبور
        SecurityClient securityClient = securityClientOptional.get();

        return securityClient;
    }

    /**
     * client متد بروزرسانی کاربر
     *
     * @param requestDto client مدل درخواست بروزرسانی کاربر
     * @return خروجی: client مدل پاسخ
     */
    @Override
    public SecurityClientResponseDto update(UpdateSecurityClientRequestDto requestDto) {

        //جستجوی کاربر کلاینت
        Optional<SecurityClient> securityClientOptional = securityClientRepository.findByClientId(requestDto.getClientId());
        //بررسی وجود
        if (securityClientOptional.isEmpty()) {
            throw new SecurityClientException(requestDto.getClientId(), BUSINESS_EXCEPTION_SECURITY_CLIENT_ID_NOT_FOUND, "");
        }
        SecurityClient securityClient = securityClientOptional.get();
        //بررسی رمز عبور
        if (securityClient.getSecretRequired() && !passwordEncoder.matches(requestDto.getClientSecret(), securityClient.getClientSecret()))
            throw new SecurityClientException(requestDto.getClientId(), BUSINESS_EXCEPTION_SECURITY_CLIENT_PASSWORD_IS_INVALID, "");

        securityClient.setClientId(requestDto.getNewClientId());
        securityClient.setClientTitle(requestDto.getClientTitle());
        securityClient.setSecretRequired(requestDto.getSecretRequired());
        if (requestDto.getSecretRequired())
            securityClient.setClientSecret(passwordEncoder.encode(requestDto.getNewClientSecret()));

        securityClient = securityClientRepository.save(securityClient);

        return securityClientMapper.toDto(securityClient);
    }

    /**
     * با شناسه و رمز عبور client حذف کاربر
     *
     * @param clientId     شناسه کاربر
     * @param clientSecret رمز عبور کاربر
     * @return خروجی: client مدل پاسخ
     */
    @Override
    public SecurityClientResponseDto deleteByIdAndSecret(@NotNull String clientId, @NotNull String clientSecret) {
        //جستجوی کاربر کلاینت
        Optional<SecurityClient> securityClientOptional = securityClientRepository.findByClientId(clientId);
        //بررسی وجود
        if (securityClientOptional.isEmpty()) {
            throw new SecurityClientException(clientId, BUSINESS_EXCEPTION_SECURITY_CLIENT_ID_NOT_FOUND, "");
        }
        //بررسی رمز عبور
        SecurityClient securityClient = securityClientOptional.get();
        if (securityClient.getSecretRequired() && !passwordEncoder.matches(clientSecret, securityClient.getClientSecret()))
            throw new SecurityClientException(clientId, BUSINESS_EXCEPTION_SECURITY_CLIENT_PASSWORD_IS_INVALID, "");

        securityClientRepository.delete(securityClient);

        return securityClientMapper.toDto(securityClient);
    }

    /**
     * متد تولید اکسس توکن Bearer از شناسه کاربری و دسترسی های او
     *
     * @param grantType    نوع اعطا
     * @param scope        دامنه کاربر
     * @param clientId     شناسه کاربر
     * @param clientSecret رمز عبور کاربر
     * @return خروجی: مدل توکن
     */
    @Override
    public BearerTokenDto createBearerToken(@NotNull String clientId, @NotNull String clientSecret, @NotNull String grantType, @NotNull String scope) {
        SecurityClient securityClient = serviceReadClientById(clientId);
        if (Boolean.TRUE.equals(securityClient.getSecretRequired()) && !passwordEncoder.matches(clientSecret, securityClient.getClientSecret()))
            throw new SecurityClientException(clientId, BUSINESS_EXCEPTION_SECURITY_CLIENT_PASSWORD_IS_INVALID, "");

        LoggedInClientDto loggedInClientDto = new LoggedInClientDto();
        loggedInClientDto.setId(securityClient.getId());
        loggedInClientDto.setClientId(securityClient.getClientId());
        loggedInClientDto.setClientTitle(securityClient.getClientTitle());
        loggedInClientDto.setAuthoritySet(securityClient.getAuthoritySet());

        return securityClientTokenService.createClientBearerToken(loggedInClientDto, new HashMap<>(), securityClient.getAccessTokenValiditySeconds(), securityClient.getRefreshTokenValiditySeconds());
    }

}
