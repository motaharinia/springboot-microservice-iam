package com.motaharinia.ms.iam.modules.backuser.presentation;

import com.codahale.metrics.annotation.Timed;
import com.motaharinia.ms.iam.config.security.oauth2.dto.BearerTokenDto;
import com.motaharinia.ms.iam.config.security.oauth2.enumeration.AuthorityConstant;
import com.motaharinia.ms.iam.external.StateProcessEnum;
import com.motaharinia.ms.iam.external.captchaotp.presentation.AspectUsernameDto;
import com.motaharinia.ms.iam.external.common.state.StateTools;
import com.motaharinia.ms.iam.modules.backuser.business.BackUserService;
import com.motaharinia.ms.iam.modules.backuser.business.enumeration.BackUserGridSearchTypeEnum;
import com.motaharinia.ms.iam.modules.backuser.presentation.dto.*;
import com.motaharinia.ms.iam.modules.backuser.presentation.signin.SigninCheckCredentialRequestDto;
import com.motaharinia.ms.iam.modules.backuser.presentation.signin.SigninCheckCredentialResponseDto;
import com.motaharinia.ms.iam.modules.backuser.presentation.signin.SigninCheckOtpRequestDto;
import com.motaharinia.msjpautility.page.CustomPageResponseDto;
import com.motaharinia.msutility.custom.customdto.ClientResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Maryam
 * کلاس کنترلر کاربر برنامه بک
 */
@RestController
@RequestMapping("/api/v1.0/back-user")
public class BackUserController {

    private final BackUserService backUserService;
    private final StateTools stateTools;


    /**
     * پیام موفقیت فرم
     */
    private static final String FORM_SUBMIT_SUCCESS = "USER_MESSAGE.FORM_SUBMIT_SUCCESS";

    @Autowired
    public BackUserController(BackUserService backUserService, StateTools stateTools) {
        this.backUserService = backUserService;
        this.stateTools = stateTools;
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
        SigninCheckCredentialResponseDto signinCheckCredentialResponseDto = backUserService.signinCheckCredential(dto.getUsername(), dto.getPassword(), new AspectUsernameDto(dto.getUsername()));
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
        BearerTokenDto bearerTokenDto = backUserService.signinCheckOtp(dto.getUsername(), dto.getPassword(), dto.getOtp(), dto.getRememberMe());
        //به روزرسانی کلید مراحل
        stateTools.stepForward(StateProcessEnum.SIGNIN, dto.getUsername(), 2);
        return new ClientResponseDto<>(bearerTokenDto, FORM_SUBMIT_SUCCESS);
    }


    //-------------------------------------------------------
    //password (change,forget)
    //-------------------------------------------------------

//    /**
//     * متد تغییر رمز عبور
//     *
//     * @param dto مدل درخواست تغییر رمز عبور
//     * @return خروجی: مدل پاسخ تغییر رمز عبور
//     */
//    //@CaptchaCheck
//    @Timed
//    @Operation(security = @SecurityRequirement(name = "bearerAuth"))
//    @PutMapping("/change-password")
//    //@PreAuthorize("backUser_changePassword")
//    public ClientResponseDto<ChangePasswordResponseDto> changePassword(@RequestBody @Validated ChangePasswordRequestDto dto) {
//        //جستجوی کاربر و تغییر رمز عبور در صورت صحت رمز عبور قبلی
//        ChangePasswordResponseDto changePasswordResponseDto = backUserService.changePassword(dto.getCurrentPassword(), dto.getNewPassword(), dto.getNewPassword());
//        return new ClientResponseDto<>(changePasswordResponseDto, FORM_SUBMIT_SUCCESS);
//    }
//
//    /**
//     * متد گام اول فراموشی رمز عبور (ارسال کد تایید)
//     *
//     * @param username کلمه کاربری
//     * @return خروجی: رشته otp در حالت تست
//     */
//    //@CaptchaCheck
//    @Timed
//    @PutMapping("/forget-password-check-username/{username}")
//    public ClientResponseDto<ForgetPasswordCheckUsernameResponseDto> forgetPasswordCheckUsername(@PathVariable String username) {
//        //جستجوی کاربر و ارسال کد تایید به موبایل کاربر
//        ForgetPasswordCheckUsernameResponseDto forgetPasswordCheckUsernameResponseDto = backUserService.forgetPasswordCheckUsername(username);
//        //به روزرسانی کلید مراحل
//        stateTools.stepForward(StateProcessEnum.FORGET_PASSWORD, username, 1);
//        return new ClientResponseDto<>(forgetPasswordCheckUsernameResponseDto, FORM_SUBMIT_SUCCESS);
//    }
//
//    /**
//     * متد گام دوم فراموشی رمز عبور (ریست کردن رمز عبور یا کد تایید)
//     *
//     * @param dto مدل درخواست تغییر رمز عبور
//     * @return خروجی: مدل توکن
//     */
//    //@CaptchaCheck
//    @Timed
//    @PutMapping("/forget-password-check-otp")
//    public ClientResponseDto<BearerTokenDto> forgetPasswordCheckOtp(@RequestBody @Validated ForgetPasswordCheckOtpRequestDto dto) {
//        //بررسی کلید مراحل
//        stateTools.stepForwardCheck(StateProcessEnum.FORGET_PASSWORD, dto.getUsername(), 2);
//        //جستجوی کاربر و ریست رمز عبور در صورت صحت رمز عبور قبلی
//        BearerTokenDto bearerTokenDto = backUserService.forgetPasswordCheckOtp(dto, false);
//        //به روزرسانی کلید مراحل
//        stateTools.stepForward(StateProcessEnum.FORGET_PASSWORD, dto.getUsername(), 2);
//        return new ClientResponseDto<>(bearerTokenDto, FORM_SUBMIT_SUCCESS);
//    }

    //-------------------------------------------------------
    //CRUD
    //-------------------------------------------------------

    /**
     * متد جستجوی با شناسه
     *
     * @param id شناسه
     * @return خروجی: مدل جستجو شده
     */
    @Operation(security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('" + AuthorityConstant.IAM_BACK_USER_READ+ "')")
    public ClientResponseDto<BackUserReadResponseDto> readById(@PathVariable Long id) {
        return new ClientResponseDto<>((backUserService.readById(id)), FORM_SUBMIT_SUCCESS);
    }

    /**
     * جستجو تمامی کاربران برنامه بک
     *
     * @return List<BackUserReadResponseDto>
     */
    @Operation(security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/read-all")
    @PreAuthorize("hasAnyAuthority('" + AuthorityConstant.IAM_BACK_USER_READ+ "')")
    public ClientResponseDto<CustomPageResponseDto<BackUserReadResponseDto>> readAll(@RequestParam(value = "searchType", required = false) BackUserGridSearchTypeEnum searchType, @RequestParam(value = "searchValue", required = false) String searchValue,
                                                                                     @PageableDefault(page = 0, size = 20) @SortDefault.SortDefaults({@SortDefault(sort = "createAt", direction = Sort.Direction.DESC)}) Pageable pageable) {
        //جستجو
        CustomPageResponseDto<BackUserReadResponseDto> readResponseDtoList = backUserService.readAll(searchType, searchValue, pageable);
        return new ClientResponseDto<>(readResponseDtoList, FORM_SUBMIT_SUCCESS);
    }

    /**
     * متد جستجو با csv شناسه
     *
     * @param ids لیست شناسه
     * @return خروجی: لیست مدل جستجو شده
     */
    @Operation(security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/read-by-ids/{ids}")
    @PreAuthorize("hasAnyAuthority('" + AuthorityConstant.IAM_BACK_USER_READ+ "')")
    public ClientResponseDto<List<BackUserMinimalReadResponseDto>> readByIds(@PathVariable String ids, @PageableDefault(page = 0, size = 20) @SortDefault.SortDefaults({@SortDefault(sort = "createAt", direction = Sort.Direction.DESC)}) Pageable pageable) {
        return new ClientResponseDto<>((backUserService.readByIds(ids, pageable)), FORM_SUBMIT_SUCCESS);
    }

    /**
     * متد گام دوم ثبت نام(بررسی کد تایید داخلی)
     *
     * @param dto مدل درخواست گام دوم ثبت نام(بررسی کد تایید سجام)
     * @return خروجی: مدل پاسخ گام دوم ثبت نام(بررسی کد تایید سجام)
     */
    @Operation(security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping()
    @PreAuthorize("hasAnyAuthority('" + AuthorityConstant.IAM_BACK_USER_CREATE + "')")
    public ClientResponseDto<BackUserResponseDto> create(@RequestBody @Validated BackUserCreateRequestDto dto) {
        //ثبت کاربر
        BackUserResponseDto backUserResponseDto = backUserService.create(dto);
        return new ClientResponseDto<>(backUserResponseDto, FORM_SUBMIT_SUCCESS);
    }

    /**
     * ویرایش اطلاعات
     *
     * @param dto مدل ویرایش اطلاعات
     * @return BackUserResponseDto
     */
    @Operation(security = @SecurityRequirement(name = "bearerAuth"))
    @PutMapping()
    @PreAuthorize("hasAnyAuthority('" + AuthorityConstant.IAM_BACK_USER_UPDATE+ "')")
    public ClientResponseDto<BackUserResponseDto> update(@RequestBody @Validated BackUserUpdateRequestDto dto) {
        //ویرایش
        BackUserResponseDto backUserResponseDto = backUserService.update(dto);
        return new ClientResponseDto<>(backUserResponseDto, FORM_SUBMIT_SUCCESS);
    }


    /**
     * فعال/عیرفعال کردن
     *
     * @param invalid فعال غیرفعال
     * @param ids     شناسه کاربران برنامه بک بصورت csv
     * @return Boolean
     */
    @Operation(security = @SecurityRequirement(name = "bearerAuth"))
    @PutMapping("/invalid/{invalid}/{ids}")
    @PreAuthorize("hasAnyAuthority('" + AuthorityConstant.IAM_BACK_USER_UPDATE+ "')")
    public ClientResponseDto<Boolean> invalid(@PathVariable Boolean invalid, @PathVariable String ids) {
        //فعال/غیرفعال کردن کاربر برنامه بک
        backUserService.invalid(invalid, ids);
        return new ClientResponseDto<>(true, FORM_SUBMIT_SUCCESS);
    }

    /**
     * حذف کاربر
     *
     * @param ids     شناسه کاربران برنامه بک بصورت csv
     * @return Boolean
     */
    @Operation(security = @SecurityRequirement(name = "bearerAuth"))
    @DeleteMapping("/{ids}")
    @PreAuthorize("hasAnyAuthority('" + AuthorityConstant.IAM_BACK_USER_DELETE+ "')")
    public ClientResponseDto<Boolean> delete(@PathVariable String ids) {
        //حذف کاربر برنامه بک
        backUserService.delete(ids);
        return new ClientResponseDto<>(true, FORM_SUBMIT_SUCCESS);
    }

}
