package com.motaharinia.ms.iam.modules.securityclient.presentation;

import com.motaharinia.ms.iam.config.security.oauth2.authorization.AuthorizationClientTokenProvider;
import com.motaharinia.ms.iam.config.security.oauth2.dto.BearerTokenDto;
import com.motaharinia.ms.iam.modules.securityclient.business.service.SecurityClientService;
import com.motaharinia.msutility.custom.customdto.ClientResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;


/**
 * کلاس کنترلر تست کاربر کلاینت
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/api/v1.0/security-client")
public class SecurityClientController {

    private AuthorizationClientTokenProvider authorizationClientTokenProvider;
    private SecurityClientService securityClientService;

    private static final String FORM_SUBMIT_SUCCESS = "USER_MESSAGE.FORM_SUBMIT_SUCCESS";
    private static final String USER_MESSAGE_OPERATION_SUCCESS = "USER_MESSAGE.OPERATION_SUCCESS";

    public SecurityClientController(AuthorizationClientTokenProvider authorizationClientTokenProvider, SecurityClientService securityClientService) {
        this.authorizationClientTokenProvider = authorizationClientTokenProvider;
        this.securityClientService = securityClientService;
    }

    /**
     * متد تست خواندن پروفایل
     *
     * @return خروجی: متن تستی
     */
    @GetMapping("/read-profile")
    @PreAuthorize("hasAuthority('READ_PROFILE')")
    public String readProfile() {
        return "readProfile";
    }

    /**
     * متد تست خواندن تصویر پروفایل
     *
     * @return خروجی: متن تستی
     */
    @GetMapping("/read-profile-picture")
    @PreAuthorize("hasAuthority('READ_PROFILE_PICTURE')")
    public String readProfilePicture() {
        return "readProfilePicture";
    }

    /**
     * client ثبت کاربر
     *
     * @param requestDto client مدل ثبت کاربر
     * @return خروجی: client مدل پاسخ
     */
    @PostMapping("/")
    public ClientResponseDto<SecurityClientResponseDto> create(@RequestBody SecurityClientRequestDto requestDto) {
        SecurityClientResponseDto responseDto = securityClientService.create(requestDto);
        return new ClientResponseDto<>(responseDto, FORM_SUBMIT_SUCCESS);
    }

    /**
     * با شناسه و رمز عبور client جستجوی کاربر
     *
     * @param clientId     شناسه کاربر
     * @param clientSecret رمز عبور کاربر
     * @return خروجی: client مدل پاسخ
     */
    @GetMapping("/")
    public ClientResponseDto<SecurityClientResponseDto> read(@RequestParam String clientId, @RequestParam String clientSecret) {
        SecurityClientResponseDto responseDto = securityClientService.readByIdAndSecret(clientId, clientSecret);
        return new ClientResponseDto<>(responseDto, USER_MESSAGE_OPERATION_SUCCESS);
    }

    /**
     * client متد بروزرسانی کاربر
     *
     * @param requestDto client مدل درخواست بروزرسانی کاربر
     * @return خروجی: client مدل پاسخ
     */
    @PutMapping("/")
    public ClientResponseDto<SecurityClientResponseDto> update(@RequestBody UpdateSecurityClientRequestDto requestDto) {
        SecurityClientResponseDto responseDto = securityClientService.update(requestDto);
        return new ClientResponseDto<>(responseDto, USER_MESSAGE_OPERATION_SUCCESS);
    }

    /**
     * با شناسه و رمز عبور client حذف کاربر
     *
     * @param clientId     شناسه کاربر
     * @param clientSecret رمز عبور کاربر
     * @return خروجی: client مدل پاسخ
     */
    @DeleteMapping("/")
    public ClientResponseDto<SecurityClientResponseDto> delete(@RequestParam String clientId, @RequestParam String clientSecret) {
        SecurityClientResponseDto responseDto = securityClientService.deleteByIdAndSecret(clientId, clientSecret);
        return new ClientResponseDto<>(responseDto, USER_MESSAGE_OPERATION_SUCCESS);
    }

    /**
     * متد ایجاد توکن برای کلاینت
     *
     * @param request   سرولت ریکوئست
     * @param grantType نوع اعطا
     * @param scope     دامنه
     * @return خروجی: client مدل پاسخ
     */
    @PostMapping("/create-token")
    public ClientResponseDto<BearerTokenDto> createBearerToken(HttpServletRequest request, @RequestParam("grant_type") String grantType, @RequestParam("scope") String scope) {
        String[] basic = authorizationClientTokenProvider.resolveBasic(request).split(":");
        BearerTokenDto responseDto = securityClientService.createBearerToken(basic[0], basic[1], grantType, scope);
        return new ClientResponseDto<>(responseDto, USER_MESSAGE_OPERATION_SUCCESS);
    }
}
