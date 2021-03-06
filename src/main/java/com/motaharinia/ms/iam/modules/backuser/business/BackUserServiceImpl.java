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
 * @author eng.motahari@gmail.com<br>
 * ???????? ?????????? ???????? ?????????? ?????????? ????
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
     * ?????? ?????????? true ???????? ???? ???????????????? ???? ???????? ?????? ?????????? ?????????????? ???????? ???????? ???? ?????????? ?????????? ???? ???????? ?????? ?????????? ???????? ??????????
     * ?? ???????????????????? DevController ???????? ??????????
     */
    @Value("${app.security.test-activated:false}")
    private boolean securityTestActivated;

    //-------------------------------------------------------
    //Read Methods
    //------------------------------------------------------

    /**
     * * ?????? ???????????? ?????????? ???? ???? ???????? ?????????? ??????????
     *
     * @param id ???????? ?????????? ??????????
     * @return BackUserDto ??????????:?????? ?????????? ???? ????????????
     */
    @Override
    public BackUserDto serviceReadById(@NotNull Long id) {
        //?????????? ?????????? ????
        return backUserMapper.toDto(backUserRepository.findById(id).orElseThrow(() -> new BackUserException(id.toString(), BUSINESS_EXCEPTION_BACK_USER_ID_NOT_FOUND, "id:" + id)));
    }


    //-------------------------------------------------------
    //signin
    //------------------------------------------------------

    /**
     * ?????? ?????? ?????? ?????????? ????????(?????????? ???????? ???????????? ?? ?????? ????????)
     *
     * @param username          ???????? ???????????? (???? ?????? ?????? ?????????? / ?????????? ?????? ????????????)
     * @param password          ?????? ????????
     * @param aspectUsernameDto ?????? ?????????? ???? ???? ???????? ?????? ???????????? ???????? @CaptchaCheck
     * @return ??????????: ?????? ???????? ?????? ?????? ?????????? ????????
     */
    @Override
    //???????? ???? ?????? ???????? ???????? ???????? ?????? ???? ?????????? ?????????? ???????? ???????? ???????? ???????????? ???????? ?????????? ???????? ???? ?????????? ???????? ????????
    @CaptchaCheck(tryCount = 15, tryTtlInMinutes = 10, banTtlInMinutes = 10)
    public @NotNull SigninCheckCredentialResponseDto signinCheckCredential(@NotNull String username, @NotNull String password, @NotNull AspectUsernameDto aspectUsernameDto) {

        //?????????? ?????????? ??????????
        SecurityUserReadDto securityUserReadDto = securityUserService.serviceSigninCheckCredential(username, password, false);

        //?????????? ?????????????? ???????? ???? ?????? ?????????? ???????? ?????????? ???????????? ????
        List<Object[]> backUserInvalidAndHidden = backUserRepository.readInvalidById(securityUserReadDto.getBackUserId());
        if (backUserInvalidAndHidden.isEmpty()) {
            throw new BackUserException(username, BUSINESS_EXCEPTION_BACK_USER_USERNAME_NOT_FOUND, "username:" + username);
        }
        if (((Boolean) backUserInvalidAndHidden.get(0)[0]) || ((Boolean) backUserInvalidAndHidden.get(0)[1])) {
            throw new BackUserException(username, BUSINESS_EXCEPTION_BACK_USER_IS_INVALID, "username:" + username);
        }

        //???????????? ???? ?????????? ?????????? ?????? ???? ?? ?????????? ???????? ???????? ?????????????? ?????? ?????????? ???????? ?? ???? ?????? ???????? ?????????????? ?????????? ?????????? ???????? ?????????? ?????????? ?????? ??????
        //?????????? ?? ?????????? ???? ?????????? ???????????? ???? ??????????
        String otp = captchaOtpExternalService.otpCreate(SourceProjectEnum.MS_IAM, OTP_MOBILE_SIGNIN_BACK_USER + username, otpLength, otpTtlSeconds).getValue();
        notificationExternalService.send(SourceProjectEnum.MS_IAM, securityUserReadDto.getMobileNo(), NOTIFICATION_BACK_USER_SIGNIN_OTP + "::" + otp);

        //?????? ???? ???????? ???????? ???????????? ???????? ?????????? ???? ?????????? ???????? ?????????? ?????????? ?????????? ???? ???????? ???? ?????????? ???????? ???????? ???? ?????? ?????? ???????? ???????? ????????
        if (!securityTestActivated) {
            otp = "";
        }

        return new SigninCheckCredentialResponseDto(otp);
    }

    /**
     * ?????? ?????? ?????? ?????????? ????????(?????????? ???? ??????????)
     *
     * @param username   ???????? ???????????? (???? ?????? ?????? ?????????? / ?????????? ?????? ????????????)
     * @param password   ?????? ????????
     * @param otp        ???? ??????????
     * @param rememberMe ?????? ???? ???????? ??????????
     * @return ??????????: ?????? ????????
     */
    @Override
    public @NotNull BearerTokenDto signinCheckOtp(@NotNull String username, @NotNull String password, @NotNull String otp, @NotNull Boolean rememberMe) {

        //?????????? ???? ??????????
        captchaOtpExternalService.otpCheck(SourceProjectEnum.MS_IAM, OTP_MOBILE_SIGNIN_BACK_USER + username, otp, "signinCheckOtp", username, 200, 1, 1);

        //?????????? ?????????? ??????????
        SecurityUserReadDto securityUserReadDto = securityUserService.serviceSigninCheckCredential(username, password, false);

        //???????????? backUser
        BackUser backUser = backUserRepository.findById(securityUserReadDto.getBackUserId()).orElseThrow(() -> new BackUserException(username, BUSINESS_EXCEPTION_BACK_USER_USERNAME_NOT_FOUND, "username:" + username));

        //?????????? ?????????????? ???????? ???? ?????? ?????????? ???????? ?????????? ???????????? ????
        if ((backUser.getInvalid()) || (backUser.getHidden())) {
            throw new BackUserException(username, BUSINESS_EXCEPTION_BACK_USER_IS_INVALID, "username:" + username);
        }

        //?????????? ???????????? ???? ?????? ?????????? ???????????? ????
        BackUserDto backUserDto = backUserMapper.toDto(backUser);

        //?????????? ?????????? ?????????? ?? ?????????? ????????
        return securityUserService.serviceSigninGenerateToken(username, null, backUserDto, rememberMe);
    }

    //-------------------------------------------------------
    //password (change,forget)
    //-------------------------------------------------------

    /**
     * ?????? ?????????? ?????? ????????
     *
     * @param currentPassword   ?????? ???????? ????????
     * @param newPassword       ?????? ???????? ????????
     * @param newPasswordRepeat ?????????? ?????? ???????? ????????
     * @return ??????????: ?????? ???????? ?????????? ?????? ????????
     */
    @Override
    public @NotNull ChangePasswordResponseDto changePassword(@NotNull String currentPassword, @NotNull String newPassword, @NotNull String newPasswordRepeat) {

        //?????????? ?????? ?????????? ?????? ????????
        if (!(newPassword.equals(newPasswordRepeat))) {
            throw new SecurityUserException("", BUSINESS_EXCEPTION_BACK_USER_REPEAT_PASSWORD_NOT_EQUAL, "");
        }

        return new ChangePasswordResponseDto(securityUserService.serviceChangePassword(currentPassword, newPassword));
    }

    /**
     * ?????? ?????? ?????? ?????????????? ?????? ???????? (?????????? ???????? ????????????)
     *
     * @param username ???????? ????????????
     * @return ??????????: ?????? ???????? ?????????????? ?????? ???????? (?????????? ???????? ????????????)
     */
    @Override
    public @NotNull ForgetPasswordCheckUsernameResponseDto forgetPasswordCheckUsername(@NotNull String username) {
        //?????????? ???? ???????? ???????????? ?? ?????????? ????????????
        String mobileNo = securityUserService.readByUsernameForGetMobileNo(username, false);

        //?????????? ?? ?????????? ???? ?????????? ???????????? ???? ??????????
        String otp = captchaOtpExternalService.otpCreate(SourceProjectEnum.MS_IAM, OTP_MOBILE_FORGET_PASSWORD_BACK_USER + username, otpLength, otpTtlSeconds).getValue();
        notificationExternalService.send(SourceProjectEnum.MS_IAM, mobileNo, NOTIFICATION_BACK_USER_FORGET_PASSWORD_OTP + "::" + otp);

        //?????? ???? ???????? ???????? ???????????? ???????? ?????????? ???? ?????????? ???????? ?????????? ?????????? ?????????? ???? ???????? ???? ?????????? ???????? ???????? ???? ?????? ?????? ???????? ???????? ????????
        if (!securityTestActivated) {
            otp = "";
        }

        return new ForgetPasswordCheckUsernameResponseDto(otp);
    }

    /**
     * ?????? ?????? ?????? ?????????????? ?????? ???????? (???????? ???????? ?????? ???????? ???? ???? ??????????)
     *
     * @param dto     ?????? ?????????????? ?????????????? ?????? ????????
     * @param isFront ?????????? ?????????? ???? ???????? ???????? ?????????? ???????????? ?????????? ?????? ???? ???????? ?????????? ????
     * @return ??????????: ?????? ????????
     */
    @Override
    public @NotNull BearerTokenDto forgetPasswordCheckOtp(ForgetPasswordCheckOtpRequestDto dto, @NotNull Boolean isFront) {

        //?????????? ?????? ?????????? ?????? ????????
        if (!dto.getNewPassword().equals(dto.getNewPasswordRepeat())) {
            throw new BackUserException(dto.getUsername(), BUSINESS_EXCEPTION_BACK_USER_REPEAT_PASSWORD_NOT_EQUAL, "username:" + dto.getUsername());
        }

        //?????????? ???? ??????????
        captchaOtpExternalService.otpCheck(SourceProjectEnum.MS_IAM, OTP_MOBILE_FORGET_PASSWORD_BACK_USER + dto.getUsername(), dto.getOtp(), "forgetPasswordCheckOtp", dto.getUsername(), 200, 1, 1);

        //?????????? ???????? ?????????? ???????????? ???? ???? ???????? ????????????
        SecurityUserReadDto securityUserReadDto = securityUserService.serviceReadByUsername(dto.getUsername(), false);

        //?????????? ?????? ???????? ?????????? ????????
        BackUserDto backUserDto = backUserMapper.toDto(backUserRepository.findById(securityUserReadDto.getBackUserId()).orElseThrow(() -> new BackUserException("", BUSINESS_EXCEPTION_BACK_USER_ID_NOT_FOUND, "")));

        //?????????? ?????? ???????? ?????????? ??????????
        return securityUserService.serviceForgetPassword(dto.getUsername(), dto.getNewPassword(), null, backUserDto, dto.getRememberMe());
    }

    //-------------------------------------------------------------
    //CRUD
    //-------------------------------------------------------------


    /**
     * @param id ?????????? ?????????? ???????????? ????
     * @return BackUserReadResponseDto ??????????:???????? ???? ???????? ?????????????? ???????? ???? ?????????? ???????????? ???? ????????????
     */
    @Override
    public BackUserReadResponseDto readById(Long id) {
        //?????????? ?????????? ????????????
        BackUser backUser = backUserRepository.findById(id).orElseThrow(() -> new BackUserException(id.toString(), BUSINESS_EXCEPTION_BACK_USER_ID_NOT_FOUND, "id:" + id));
        //???? ???????? ?????????????? ?????????? ????????????
        BackUserReadResponseDto dto = backUserMapper.toBackUserReadResponseDto(backUser);
        //???? ???????? ?????? ?? ???????????? ??????????
        dto.setRoleAndPermissionDto(securityUserService.serviceReadRoleAndPermissionForBack(backUser.getId()));
        return dto;
    }

    /**
     * ?????? ?????????? ???? csv ???????? ????
     *
     * @param ids      ?????????? csv
     * @param pageable ???????? ????????
     * @return List<BackUserMinimalReadResponseDto> ???????? ??????
     */
    @Override
    public List<BackUserMinimalReadResponseDto> readByIds(String ids, Pageable pageable) {
        Set<Long> idSet = Stream.of(ids.split(",")).map(Long::parseLong).collect(Collectors.toSet());
        List<BackUser> backUserList = backUserRepository.findByIdIn(idSet, pageable);
        return backUserList.stream().map(backUserMapper::toBackUserMinimalReadResponseDto).collect(Collectors.toList());
    }

    /**
     * ?????? ?????????? ?????????? ?????????????? ???????????? ????
     *
     * @param searchType  ?????? ??????
     * @param searchValue ?????????? ??????
     * @param pageable    ???????? ???????? ????????
     * @return CustomPageResponseDto<BackUserReadResponseDto> ???????? ?????? ?????????? ???????????? ????
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
                //?????????? ???????????? ???? ??????
                BackUserReadResponseDto dto = backUserMapper.toBackUserReadResponseDto(backUser);
                //???? ???????? ???????????? ???? ?? ?????? ?????? ????????????
                dto.setRoleAndPermissionDto(securityUserService.serviceReadRoleAndPermissionForBack(backUser.getId()));
                return dto;
            });
            return new CustomPageResponseDto<>(finalPage);
        }
        return null;

    }


    /**
     * ?????? ?????? ?????????? ???????????? ????
     *
     * @param dto ???????? ?????? ?????? ?????????? ???????????? ????
     * @return ??????????: ?????? ???????? ?????????? ???????????? ????
     */
    @Override
    public @NotNull BackUserResponseDto create(@NotNull BackUserCreateRequestDto dto) {

        //?????????? ???????????? ?????????? ?????????? ?????? (???????? ????????????)
        if (org.apache.commons.lang3.ObjectUtils.isNotEmpty(backUserRepository.readIdByNationalCode(dto.getUsername()))) {
            throw new BackUserException(dto.getUsername(), BUSINESS_EXCEPTION_BACK_USER_IS_EXISTED, "");
        }

        //?????????? ?????????? ???? ????????????
        //?????? ???????? ?????????????? ?????? ???? ????????????
        BackUser backUser = backUserMapper.toEntity(dto);
        backUserRepository.save(backUser);

        //?????????? ?????????? ?????????? - ???????? ???????? ?????????? ????????????
        securityUserService.serviceSignup(new SignupDto(
                new SecurityUserCreateRequestDto(dto.getUsername(), dto.getPassword(), dto.getBackUserDto().getMobileNo(), dto.getBackUserDto().getEmailAddress(), null, backUser.getId())
                , dto.getSecurityRoleIdSet(), dto.getSecurityPermissionIncludeIdSet(), null, null, false));

        return new BackUserResponseDto(backUser.getId());
    }

    /**
     * ?????? ???????????? ?????????? ???????????? ????
     *
     * @param dto ?????? ???????????? ??????????????  ?????????? ???????????? ????
     * @return ??????????: ?????? ?????????? ???????????? ???? BackUserUpdateRequestDto
     */
    @Override
    public BackUserResponseDto update(@NotNull BackUserUpdateRequestDto dto) {
        //?????????? ?????????? ????????????
        BackUser backUser = backUserRepository.findById(dto.getId()).orElseThrow(() -> new BackUserException("", BUSINESS_EXCEPTION_BACK_USER_ID_NOT_FOUND, ""));

        //?????????? ???????????? ?????????? ?????????? ?????? (???????? ????????????)
        Long id = backUserRepository.readIdByNationalCode(dto.getUsername());
        if (id != null && !id.equals(dto.getId())) {
            throw new AppUserException(dto.getUsername(), BUSINESS_EXCEPTION_BACK_USER_IS_EXISTED + "::" + dto.getUsername(), "");
        }


        //?????? ???????? ?????????????? ?????? ???? ????????????
        backUserMapper.toEntity(dto, backUser);
        backUserRepository.save(backUser);

        securityUserService.serviceUpdate(new SecurityUserUpdateDto(dto.getBackUserDto().getMobileNo(), dto.getUsername(), dto.getPassword(), dto.getBackUserDto().getEmailAddress(), null, backUser.getId(), dto.getSecurityRoleIdSet(), dto.getSecurityPermissionIncludeIdSet()));

        return new BackUserResponseDto(backUser.getId());
    }


    /**
     * ?????? ???????? ???? ?????????????? ???????? ?????????????? ???????????? ????
     *
     * @param invalid ????????/ ??????????????
     * @param ids     ???????? ?????????? ?????????????? ???? ?????????? csv
     */
    @Override
    public void invalid(@NotNull Boolean invalid, @NotNull String ids) {
        //?????????? csv ???????????? ???? ????????
        Set<Long> idSet = Stream.of(ids.split(",")).map(Long::parseLong).collect(Collectors.toSet());
        //?????????? ?????????? ?????????? ???? ?????????? ??????
        LoggedInUserDto loggedInUserDto = resourceUserTokenProvider.getLoggedInDto().orElseThrow(() -> new BackUserException("", BUSINESS_EXCEPTION_BACK_USER_NOT_LOGGED_IN, ""));
        //?????????? ?????? ?????????? ?????????? ?????????? ?????? ?????????? ???????? ???? ?????????????? ???????? ???????? ???? ??????????
        if (idSet.contains(loggedInUserDto.getBackUserId()))
            throw new BackUserException("", BUSINESS_EXCEPTION_BACK_USER_NOT_ACCESS_HIMSELF, "");


        //???????????? ?????????????? ???? ???? ?????????? ????????????
        List<BackUser> backUserList = backUserRepository.findByIdIn(idSet, null);

        //?????? ?????????? ???????????? ???????? ?????? ?????? ???????? ??????
        if (backUserList.isEmpty())
            throw new BackUserException("", BUSINESS_EXCEPTION_BACK_USER_ID_NOT_FOUND, "");

        // ???????? ???? ?????????????? ??????
        backUserList.forEach(backUser -> {
            backUser.setInvalid(invalid);
            backUserRepository.save(backUser);
            // ???????? ???? ?????????????? ???????? ?????????? ??????????
            securityUserService.serviceInvalidForBack(backUser.getId(), invalid);
        });


        //?????? ?????????????? ?????????? ???????? ???????? ?????????????? ???? ?????????????? ???? ?????????????? ????????
        if (invalid) {
            //???????????? ???????? ???????????? ???? ?????????? ?????????? ???? ?????? ?????????????? ???????? ???????? ???????????????? ???? ?????????????? ????????????
            securityUserTokenService.serviceInvalid(securityUserService.serviceReadUsernamesByBackUserIdSet(idSet), SecurityTokenInvalidTypeEnum.SECURITY_USER_INVALID, SecurityUserInvalidTokenEnum.JUST_BACK);
        }

    }

    /**
     * ?????? ?????? ?????????? ???????????? ????
     *
     * @param ids ???????? ?????????? ?????????????? ???? ?????????? csv
     */
    @Override
    public void delete(@NotNull String ids) {
//        //?????????? ?????????? ???????????? ???? ???? ???? ??????
//        BackUser backUser = backUserRepository.findById(id).orElseThrow(() -> new BackUserException(id.toString(), BUSINESS_EXCEPTION_BACK_USER_ID_NOT_FOUND, "id:" + id));
//        backUserRepository.delete(backUser);
//        //?????? ?????????? ??????????
//        securityUserService.serviceDeleteForBack(backUser.getId());

        //?????????? csv ???????????? ???? ????????
        Set<Long> idSet = Stream.of(ids.split(",")).map(Long::parseLong).collect(Collectors.toSet());
        //?????????? ?????????? ?????????? ???? ?????????? ??????
        LoggedInUserDto loggedInUserDto = resourceUserTokenProvider.getLoggedInDto().orElseThrow(() -> new BackUserException("", BUSINESS_EXCEPTION_BACK_USER_NOT_LOGGED_IN, ""));
        //?????????? ?????? ?????????? ?????????? ?????????? ?????? ?????????? ???????? ???? ?????????????? ???????? ???????? ???? ??????????
        if (idSet.contains(loggedInUserDto.getBackUserId()))
            throw new BackUserException("", BUSINESS_EXCEPTION_BACK_USER_NOT_ACCESS_HIMSELF, "");


        //???????????? ?????????????? ???? ???? ?????????? ????????????
        List<BackUser> backUserList = backUserRepository.findByIdIn(idSet, null);

        //?????? ?????????? ???????????? ???????? ?????? ?????? ???????? ??????
        if (backUserList.isEmpty())
            throw new BackUserException("", BUSINESS_EXCEPTION_BACK_USER_ID_NOT_FOUND, "");

        //?????? ?????????? ??????????
        backUserList.forEach(backUser -> {
            //?????? ???????? ???????? ?????? ?????????? ?? ?????????????? ???????? ?????????? ??????
            if (!backUser.getInvalid())
                throw new BackUserException("", BUSINESS_EXCEPTION_BACK_USER_MUST_BE_INVALID, "");

            backUserRepository.delete(backUser);
            //?????? ?????????? ??????????
            securityUserService.serviceDeleteForBack(backUser.getId());
        });

    }

}
