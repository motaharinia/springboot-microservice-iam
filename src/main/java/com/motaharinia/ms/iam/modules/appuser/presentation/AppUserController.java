package com.motaharinia.ms.iam.modules.appuser.presentation;

import com.codahale.metrics.annotation.Timed;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.MetadataException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.motaharinia.ms.iam.config.batch.BatchKeyConstant;
import com.motaharinia.ms.iam.config.security.oauth2.dto.BearerTokenDto;
import com.motaharinia.ms.iam.config.security.oauth2.dto.LoggedInUserDto;
import com.motaharinia.ms.iam.config.security.oauth2.enumeration.AuthorityConstant;
import com.motaharinia.ms.iam.config.security.oauth2.resource.ResourceUserTokenProvider;
import com.motaharinia.ms.iam.external.StateProcessEnum;
import com.motaharinia.ms.iam.external.captchaotp.presentation.AspectUsernameDto;
import com.motaharinia.ms.iam.external.common.ratelimit.presentation.RateRequestDto;
import com.motaharinia.ms.iam.external.common.state.StateTools;
import com.motaharinia.ms.iam.modules.appuser.business.enumeration.AppUserGridSearchTypeEnum;
import com.motaharinia.ms.iam.modules.appuser.business.exception.AppUserException;
import com.motaharinia.ms.iam.modules.appuser.business.service.AppUserService;
import com.motaharinia.ms.iam.modules.appuser.business.service.AppUserServiceImpl;
import com.motaharinia.ms.iam.modules.appuser.presentation.changepassword.ChangePasswordRequestDto;
import com.motaharinia.ms.iam.modules.appuser.presentation.changepassword.ChangePasswordResponseDto;
import com.motaharinia.ms.iam.modules.appuser.presentation.dto.*;
import com.motaharinia.ms.iam.modules.appuser.presentation.dto.create.AppUserCreateRequestDto;
import com.motaharinia.ms.iam.modules.appuser.presentation.forgetpassword.ForgetPasswordCheckOtpRequestDto;
import com.motaharinia.ms.iam.modules.appuser.presentation.forgetpassword.ForgetPasswordCheckUsernameResponseDto;
import com.motaharinia.ms.iam.modules.appuser.presentation.signin.SigninCheckCredentialRequestDto;
import com.motaharinia.ms.iam.modules.appuser.presentation.signin.SigninCheckCredentialResponseDto;
import com.motaharinia.ms.iam.modules.appuser.presentation.signin.SigninCheckOtpRequestDto;
import com.motaharinia.ms.iam.modules.appuser.presentation.signup.SignupCheckCredentialRequestDto;
import com.motaharinia.ms.iam.modules.appuser.presentation.signup.SignupCheckCredentialResponseDto;
import com.motaharinia.ms.iam.modules.appuser.presentation.signup.SignupCheckOtpRequestOtpDto;
import com.motaharinia.ms.iam.modules.fso.FsoSetting;
import com.motaharinia.ms.iam.modules.fso.business.enumeration.CrudFileHandleActionEnum;
import com.motaharinia.ms.iam.modules.fso.business.service.FsoService;
import com.motaharinia.ms.iam.modules.fso.business.service.FsoUploadedFileService;
import com.motaharinia.ms.iam.modules.fso.presentation.FsoUploadedFileDto;
import com.motaharinia.ms.iam.modules.fso.presentation.crudfilehandle.CrudFileHandleDto;
import com.motaharinia.msjpautility.page.CustomPageResponseDto;
import com.motaharinia.msutility.custom.customdto.ClientResponseDto;
import com.motaharinia.msutility.custom.customjson.CustomObjectMapper;
import com.motaharinia.msutility.custom.customvalidation.nationalcode.NationalCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * @author eng.motahari@gmail.com<br>
 * کلاس کنترلر کاربر برنامه فرانت
 *
 *     برای AppUser نبازی به کنترل دسترسی (authorization) نمیباشد
 *     برای BackUser نباز به کنترل دسترسی (authorization) میباشد
 */
@RestController
@RequestMapping("/api/v1.0/app-user")
@Slf4j
public class AppUserController {

    private final AppUserService appUserService;
    private final FsoUploadedFileService fsoUploadedFileService;
    private final FsoService fsoService;
    private final StateTools stateTools;
    private final ResourceUserTokenProvider resourceUserTokenProvider;

    private final JobLauncher jobLauncher;
    private final Job job;

    private final ObjectMapper objectMapper;

    /**
     * پیام موفقیت فرم
     */
    private static final String FORM_SUBMIT_SUCCESS = "USER_MESSAGE.FORM_SUBMIT_SUCCESS";


    @Autowired
    public AppUserController(AppUserService appUserService, FsoUploadedFileService fsoUploadedFileService, FsoService fsoService, StateTools stateTools, ResourceUserTokenProvider resourceUserTokenProvider, JobLauncher jobLauncher, @Qualifier("appUserCreateJob") Job job, CustomObjectMapper objectMapper) {
        this.appUserService = appUserService;
        this.fsoUploadedFileService = fsoUploadedFileService;
        this.fsoService = fsoService;
        this.stateTools = stateTools;
        this.resourceUserTokenProvider = resourceUserTokenProvider;
        this.jobLauncher = jobLauncher;
        this.job = job;
        this.objectMapper = objectMapper;
    }

    //--------------------------------------------------------------------------------------------------------------
    //methods for AppUser (front user)
    //--------------------------------------------------------------------------------------------------------------
    //-------------------------------------------------------
    //signup
    //-------------------------------------------------------

    /**
     * متد گام اول ثبت نام(بررسی کلمه کاربری و  رمزعبور)
     *
     * @param dto کلاس مدل درخواست گام اول ثبت نام(بررسی کلمه کاربری و  رمز عبور)
     * @return خروجی:  کلاس مدل پاسخ گام اول ثبت نام(بررسی کلمه کاربری و  رمز عبور)
     */
    @Timed
    @PostMapping("/signup-check-credential")
    public ClientResponseDto<SignupCheckCredentialResponseDto> signupCheckCredential(@RequestBody @Validated SignupCheckCredentialRequestDto dto) {
        //جستجوی کاربر ، بررسی کلمه کاربری و رمز عبور وارد شده توسط کاربر
        SignupCheckCredentialResponseDto signupCheckCredentialResponseDto = appUserService.signupCheckCredential(dto, new AspectUsernameDto(dto.getUsername()));
        //به روزرسانی کلید مراحل
        stateTools.stepForward(StateProcessEnum.SIGNUP, dto.getUsername(), 1);
        return new ClientResponseDto<>(signupCheckCredentialResponseDto, FORM_SUBMIT_SUCCESS);
    }

    /**
     * متد گام دوم ثبت نام(بررسی کد تایید داخلی)
     *
     * @param dto مدل درخواست گام دوم ثبت نام(بررسی کد تایید سجام)
     * @return خروجی: مدل توکن
     */
    @PostMapping("/signup-check-otp")
    public ClientResponseDto<BearerTokenDto> signupCheckOtp(@RequestBody @Validated SignupCheckOtpRequestOtpDto dto) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        //بررسی کلید مراحل
        stateTools.stepForwardCheck(StateProcessEnum.SIGNUP, dto.getUsername(), 2);
        //جستجوی کاربر ، بررسی کد تایید وارد شده توسط کاربر
        BearerTokenDto bearerTokenDto = appUserService.signupCheckOtp(dto);
        //به روزرسانی کلید مراحل
        stateTools.stepForward(StateProcessEnum.SIGNUP, dto.getUsername(), 2);
        return new ClientResponseDto<>(bearerTokenDto, FORM_SUBMIT_SUCCESS);
    }

    //-------------------------------------------------------
    //signin
    //-------------------------------------------------------

    /**
     * متد گام اول احراز هویت(بررسی کلمه کاربری و رمز عبور)
     *
     * @param dto مدل درخواست گام اول احراز هویت(بررسی کلمه کاربری)
     * @return خروجی: مدل پاسخ گام اول احراز هویت
     */
    @Timed
    @PostMapping("/signin-check-credential")
    public ClientResponseDto<SigninCheckCredentialResponseDto> signinCheckCredential(@RequestBody @Validated SigninCheckCredentialRequestDto dto) {
        //جستجوی کاربر ، بررسی کلمه کاربری و رمز عبور کاربر و در صورت صحت ارسال کد تایید داخلی
        SigninCheckCredentialResponseDto signinCheckCredentialResponseDto = appUserService.signinCheckCredential(new RateRequestDto(dto.getUsername()), dto.getUsername(), dto.getPassword());
        //به روزرسانی کلید مراحل
        stateTools.stepForward(StateProcessEnum.SIGNIN, dto.getUsername(), 1);
        return new ClientResponseDto<>(signinCheckCredentialResponseDto, FORM_SUBMIT_SUCCESS);
    }


    /**
     * متد گام دوم احراز هویت(بررسی کد تایید داخلی)
     *
     * @param dto مدل درخواست گام دوم احراز هویت(بررسی کد تایید داخلی)
     * @return خروجی: مدل توکن
     */
    @Timed
    @PostMapping("/signin-check-otp")
    public ClientResponseDto<BearerTokenDto> signinCheckOtp(@RequestBody @Validated SigninCheckOtpRequestDto dto) {
        //بررسی کلید مراحل
        stateTools.stepForwardCheck(StateProcessEnum.SIGNIN, dto.getUsername(), 2);
        //جستجوی کاربر ، بررسی کد تایید وارد شده توسط کاربر
        BearerTokenDto bearerTokenDto = appUserService.signinCheckOtp(new AspectUsernameDto(dto.getUsername()), dto.getUsername(), dto.getPassword(), dto.getOtp(), dto.getRememberMe());
        //به روزرسانی کلید مراحل
        stateTools.stepForward(StateProcessEnum.SIGNIN, dto.getUsername(), 2);
        return new ClientResponseDto<>(bearerTokenDto, FORM_SUBMIT_SUCCESS);
    }


    //-------------------------------------------------------
    //password (change,forget)
    //-------------------------------------------------------

    /**
     * متد تغییر رمز عبور
     *
     * @param dto مدل درخواست تغییر رمز عبور
     * @return خروجی: مدل پاسخ تغییر رمز عبور
     */
    @Timed
    @Operation(security = @SecurityRequirement(name = "bearerAuth"))
    @PutMapping("/change-password")
    public ClientResponseDto<ChangePasswordResponseDto> changePassword(@RequestBody @Validated ChangePasswordRequestDto dto) {
        //جستجوی کاربر و تغییر رمز عبور در صورت صحت رمز عبور قبلی
        ChangePasswordResponseDto changePasswordResponseDto = appUserService.changePassword(dto.getCurrentPassword(), dto.getNewPassword(), dto.getNewPassword());
        return new ClientResponseDto<>(changePasswordResponseDto, FORM_SUBMIT_SUCCESS);
    }

    /**
     * متد گام اول فراموشی رمز عبور (ارسال کد تایید)
     *
     * @param username کلمه کاربری
     * @return خروجی: رشته otp در حالت تست
     */
    @Timed
    @PutMapping("/forget-password-check-username/{username}/")
    public ClientResponseDto<ForgetPasswordCheckUsernameResponseDto> forgetPasswordCheckUsername(@PathVariable @Validated @NationalCode String username) {
        //جستجوی کاربر و ارسال کد تایید به موبایل کاربر
        ForgetPasswordCheckUsernameResponseDto forgetPasswordCheckUsernameResponseDto = appUserService.forgetPasswordCheckUsername(username);
        //به روزرسانی کلید مراحل
        stateTools.stepForward(StateProcessEnum.FORGET_PASSWORD, username, 1);
        return new ClientResponseDto<>(forgetPasswordCheckUsernameResponseDto, FORM_SUBMIT_SUCCESS);
    }

    /**
     * متد گام دوم فراموشی رمز عبور (ریست کردن رمز عبور یا کد تایید)
     *
     * @param dto مدل درخواست تغییر رمز عبور
     * @return خروجی: مدل توکن
     */
    @Timed
    @PutMapping("/forget-password-check-otp")
    public ClientResponseDto<BearerTokenDto> forgetPasswordCheckOtp(@RequestBody @Validated ForgetPasswordCheckOtpRequestDto dto) {
        //بررسی کلید مراحل
        stateTools.stepForwardCheck(StateProcessEnum.FORGET_PASSWORD, dto.getUsername(), 2);
        //جستجوی کاربر و ریست رمز عبور در صورت صحت رمز عبور قبلی
        BearerTokenDto bearerTokenDto = appUserService.forgetPasswordCheckOtp(new AspectUsernameDto(dto.getUsername()), dto, true);
        //به روزرسانی کلید مراحل
        stateTools.stepForward(StateProcessEnum.FORGET_PASSWORD, dto.getUsername(), 2);
        return new ClientResponseDto<>(bearerTokenDto, FORM_SUBMIT_SUCCESS);
    }

    //-------------------------------------------------------
    //invite
    //-------------------------------------------------------

    /**
     * متد دعوت دوستان به ثبت نام در سامانه با استفاده از کد معرف
     *
     * @param dto کلاس مدل دعوت از دوستان
     * @return Boolean
     */
    @PostMapping("/invite-friend-by-invitation-code")
    public ClientResponseDto<Boolean> inviteFriendByInvitationCode(@RequestBody @Validated InviteFriendRequestDto dto) {
        return new ClientResponseDto<>(appUserService.inviteFriendByInvitationCode(dto), FORM_SUBMIT_SUCCESS);
    }


    //-------------------------------------------------------
    //updateProfile
    //-------------------------------------------------------

    /**
     * متد ویرایش اطلاعات پروفایل کاربر برنامه فرانت توسط خودش
     *
     * @param dto مدل ویرایش اطلاعات  کاربر برنامه فرانت
     * @return AppUserResponseDto
     * @throws ImageProcessingException
     * @throws MetadataException
     * @throws IOException
     */
    @Operation(security = @SecurityRequirement(name = "bearerAuth"))
    @PutMapping("/update-profile")
    public ClientResponseDto<AppUserResponseDto> updateProfile(@RequestBody @Validated AppUserUpdateProfileRequestDto dto) throws ImageProcessingException, MetadataException, IOException {
        //ویرایش
        AppUserResponseDto appUserResponseDto = appUserService.updateProfile(dto);

        //ویرایش فایلها بعد از اطمینان از ویرایش انتیتی در دیتابیس
        if (CollectionUtils.isNotEmpty(dto.getProfileImageFileList())) {
            CrudFileHandleDto crudFileHandleDto = new CrudFileHandleDto(appUserResponseDto.getId(), CrudFileHandleActionEnum.ENTITY_UPDATE, dto.getProfileImageFileList(), FsoSetting.MS_IAM_APP_USER_PROFILE_IMAGE);
            fsoService.crudHandle(crudFileHandleDto);
        }

        return new ClientResponseDto<>(appUserResponseDto, FORM_SUBMIT_SUCCESS);
    }

    @GetMapping("/read-profile")
    public ClientResponseDto<AppUserReadResponseDto> readProfile() {
        //گرفتن شناسه کاربری شخص لاگین شده
        LoggedInUserDto loggedInUserDto = resourceUserTokenProvider.getLoggedInDto().orElseThrow(() -> new AppUserException("", AppUserServiceImpl.BUSINESS_EXCEPTION_APP_USER_USER_NOT_LOGGED_IN, ""));
        return new ClientResponseDto<>((appUserService.readById(loggedInUserDto.getAppUserId())), FORM_SUBMIT_SUCCESS);
    }

    //--------------------------------------------------------------------------------------------------------------
    //methods for BackUser (Admin Panel)
    //--------------------------------------------------------------------------------------------------------------
    //-------------------------------------------------------
    //CRUD
    //-------------------------------------------------------

    /**
     * متد جستجو با شناسه کاربری
     *
     * @param id شناسه
     * @return خروجی: مدل جستجو شدهAppUserReadResponseDto
     */
    @Operation(security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('" + AuthorityConstant.IAM_APP_USER_READ+ "')")
    public ClientResponseDto<AppUserReadResponseDto> readById(@PathVariable Long id) {
        return new ClientResponseDto<>((appUserService.readById(id)), FORM_SUBMIT_SUCCESS);
    }

    /**
     * جستجو تمامی کاربران برنامه فرانت
     *
     * @return List<AppUserReadResponseDto>
     */
    @Operation(security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/read-all")
    @PreAuthorize("hasAnyAuthority('" + AuthorityConstant.IAM_APP_USER_READ+ "')")
    public ClientResponseDto<CustomPageResponseDto<AppUserReadResponseDto>> readAll(@RequestParam(value = "searchType", required = false) AppUserGridSearchTypeEnum searchType, @RequestParam(value = "searchValue", required = false) String searchValue,
                                                                                    @PageableDefault(page = 0, size = 20) @SortDefault.SortDefaults({@SortDefault(sort = "createAt", direction = Sort.Direction.DESC)}) Pageable pageable) {
        CustomPageResponseDto<AppUserReadResponseDto> appUserDtoSet = appUserService.readAll(searchType, searchValue, pageable);
        return new ClientResponseDto<>(appUserDtoSet, FORM_SUBMIT_SUCCESS);
    }

    /**
     * ثبت اطلاعات کاربر برنامه فرانت
     *
     * @param dto مدل ثبت اطلاعات  کاربر برنامه فرانت
     * @return AppUserResponseDto
     * @throws ImageProcessingException
     * @throws MetadataException
     * @throws IOException
     */
    @Operation(security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping()
    @PreAuthorize("hasAnyAuthority('" + AuthorityConstant.IAM_APP_USER_CREATE+ "')")
    public ClientResponseDto<AppUserResponseDto> create(@RequestBody @Validated AppUserCreateRequestDto dto) throws ImageProcessingException, MetadataException, IOException, NoSuchAlgorithmException {
        //ثبت
        AppUserResponseDto appUserResponseDto = appUserService.create(dto, true);

        //ثبت فایلها بعد از اطمینان از ثبت انتیتی در دیتابیس
        if (CollectionUtils.isNotEmpty(dto.getProfileImageFileList())) {
            CrudFileHandleDto crudFileHandleDto = new CrudFileHandleDto(appUserResponseDto.getId(), CrudFileHandleActionEnum.ENTITY_CREATE, dto.getProfileImageFileList(), FsoSetting.MS_IAM_APP_USER_PROFILE_IMAGE);
            fsoService.crudHandle(crudFileHandleDto);
        }

        return new ClientResponseDto<>(appUserResponseDto, FORM_SUBMIT_SUCCESS);
    }

    /**
     * ویرایش اطلاعات کاربر برنامه فرانت توسط ادمین پنل
     *
     * @param dto مدل ویرایش اطلاعات  کاربر برنامه فرانت
     * @return AppUserResponseDto
     * @throws ImageProcessingException
     * @throws MetadataException
     * @throws IOException
     */
    @Operation(security = @SecurityRequirement(name = "bearerAuth"))
    @PutMapping()
    @PreAuthorize("hasAnyAuthority('" + AuthorityConstant.IAM_APP_USER_UPDATE+ "')")
    public ClientResponseDto<AppUserResponseDto> update(@RequestBody @Validated AppUserUpdateRequestDto dto) throws ImageProcessingException, MetadataException, IOException {
        //ویرایش
        AppUserResponseDto appUserResponseDto = appUserService.update(dto);

        //ویرایش فایلها بعد از اطمینان از ویرایش انتیتی در دیتابیس
        if (CollectionUtils.isNotEmpty(dto.getProfileImageFileList())) {
            CrudFileHandleDto crudFileHandleDto = new CrudFileHandleDto(appUserResponseDto.getId(), CrudFileHandleActionEnum.ENTITY_UPDATE, dto.getProfileImageFileList(), FsoSetting.MS_IAM_APP_USER_PROFILE_IMAGE);
            fsoService.crudHandle(crudFileHandleDto);
        }

        return new ClientResponseDto<>(appUserResponseDto, FORM_SUBMIT_SUCCESS);
    }


    /**
     * فعال/عیرفعال کردن کاربر برنامه فرانت
     *
     * @param invalid شناسه کاربر برنامه فرانت
     * @param ids     شناسه کاربران برنامه فرانت بصورت csv
     * @return Boolean
     */
    @Operation(security = @SecurityRequirement(name = "bearerAuth"))
    @PutMapping("/invalid/{invalid}/{ids}")
    @PreAuthorize("hasAnyAuthority('" + AuthorityConstant.IAM_APP_USER_UPDATE+ "')")
    public ClientResponseDto<Boolean> invalid(@PathVariable Boolean invalid, @PathVariable String ids) {
        //فعال/غیرفعال کردن
        appUserService.invalid(invalid, ids);
        return new ClientResponseDto<>(true, FORM_SUBMIT_SUCCESS);
    }

    //-------------------------------------------------------
    //upload excel and batch create
    //-------------------------------------------------------
    @PostMapping("/create-batch/{fileKey}")
    @PreAuthorize("hasAnyAuthority('" + AuthorityConstant.IAM_APP_USER_CREATE+ "')")
    public ClientResponseDto<Boolean> createBatch(@PathVariable String fileKey) throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException, IOException {

        FsoUploadedFileDto fsoUploadedFileDto = fsoUploadedFileService.readByFileKey(fileKey);

        //اجرای جاب
        JobExecution jobExecution = jobLauncher.run(job, new JobParametersBuilder().addString("excelFilePath", fsoUploadedFileDto.getFileUploadedPath()).addDate("date", new Date()).toJobParameters());

        //خواندن هش مپ خطاهایی که در حین write اتفاق رخ داده است
        ExecutionContext executionContext = jobExecution.getExecutionContext();
        HashMap<String, List<String>> exceptionHashMap = (HashMap<String, List<String>>) executionContext.get(BatchKeyConstant.EXCEPTION_LOG.getValue());
        if (!ObjectUtils.isEmpty(exceptionHashMap)) {
            String exception = objectMapper.writeValueAsString(exceptionHashMap);
            log.error(" all exceptions : {}  " + exception);
            throw new AppUserException("", exception, "");
        }

        return new ClientResponseDto<>(true, FORM_SUBMIT_SUCCESS);
    }

}

