package com.motaharinia.ms.iam.modules.securityuser.presentation;

import com.motaharinia.ms.iam.config.security.oauth2.dto.BearerTokenDto;
import com.motaharinia.ms.iam.modules.securityuser.business.service.SecurityUserTokenService;
import com.motaharinia.ms.iam.modules.securityuser.presentation.securityusertoken.SecurityUserTokenDto;
import com.motaharinia.msjpautility.page.CustomPageResponseDto;
import com.motaharinia.msutility.custom.customdto.ClientResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * کلاس کنترلر توکن امنیت
 */
@Slf4j
@RestController
@RequestMapping("/api/v1.0/security-user-token")
public class SecurityUserTokenController {

    private final SecurityUserTokenService securityUserTokenService;


    /**
     * پیام موفقیت فرم
     */
    private static final String FORM_SUBMIT_SUCCESS = "USER_MESSAGE.FORM_SUBMIT_SUCCESS";

    public SecurityUserTokenController(SecurityUserTokenService securityUserTokenService) {
        this.securityUserTokenService = securityUserTokenService;
    }


    //-------------------------------------------------------
    //CRUD
    //-------------------------------------------------------

    /**
     * جستجو تمامی توکن های امنیت کاربر لاگین شده
     *
     * @return
     */
    @Operation(security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/read-all")
    public ClientResponseDto<CustomPageResponseDto<SecurityUserTokenDto>> readAll(
            @RequestParam(value = "fromDate", required = false) Long fromDate,
            @RequestParam(value = "toDate", required = false) Long toDate,
            @PageableDefault(page = 0, size = 20) @SortDefault.SortDefaults({@SortDefault(sort = "createAt", direction = Sort.Direction.DESC)}) Pageable pageable) {

        LocalDateTime localDateTimeFromDate = ((fromDate == null) ? LocalDateTime.now().minusDays(7L) : Instant.ofEpochMilli(fromDate).atZone(ZoneId.systemDefault()).toLocalDate().atStartOfDay());
        LocalDateTime localDateTimeToDate = ((toDate == null) ? LocalDateTime.now() : Instant.ofEpochMilli(toDate).atZone(ZoneId.systemDefault()).toLocalDate().atStartOfDay().plusDays(1));

        //جستجوی نقش کاربری
        return new ClientResponseDto<>(securityUserTokenService.readAll(localDateTimeFromDate, localDateTimeToDate,pageable), FORM_SUBMIT_SUCCESS);
    }

    /**
     * ایجاد توکن جدید برای  کاربر لاگین شده
     * @param refreshToken رفرش توکن
     * @param httpServletRequest سرولت ریکوئست
     * @return خروجی:مدل تولید توکن احراز هویت
     */
    @PutMapping("/renew-token/{refreshToken}")
    public ClientResponseDto<BearerTokenDto> renewToken(@PathVariable String refreshToken, HttpServletRequest httpServletRequest) {
        //ایجاد توکن جدید
        BearerTokenDto bearerTokenDto = securityUserTokenService.renewToken(refreshToken, httpServletRequest);
        return new ClientResponseDto<>(bearerTokenDto, FORM_SUBMIT_SUCCESS);
    }

    /**
     * مشاهده سشن های فعال کاربر لاگین شده
     * @return خروجی: لیست مدل توکن امنیت
     */
    @Operation(security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/read-all-active-session")
    public ClientResponseDto<CustomPageResponseDto<SecurityUserTokenDto>> readAllActiveSessionByCurrentUser(@PageableDefault(page = 0, size = 20) @SortDefault.SortDefaults({@SortDefault(sort = "createAt", direction = Sort.Direction.DESC)}) Pageable pageable) {
        return new ClientResponseDto<>(securityUserTokenService.readAllActiveSessionByCurrentUser(pageable), FORM_SUBMIT_SUCCESS);
    }

    /**
     * متد kill کردن رفرش توکن برای  کاربر لاگین شده
     * @param refreshToken رفرش توکن
     */
    @Operation(security = @SecurityRequirement(name = "bearerAuth"))
    @PutMapping("/terminate/{refreshToken}")
    public ClientResponseDto<Boolean> terminate(@PathVariable String refreshToken) {
        securityUserTokenService.terminate(refreshToken);
        return new ClientResponseDto<>(true, FORM_SUBMIT_SUCCESS);
    }

    /**
     * خارج شدن از حساب کاربری  کاربر لاگین شده
     */
    @Operation(security = @SecurityRequirement(name = "bearerAuth"))
    @PutMapping("/logout")
    public ClientResponseDto<Boolean> logout(HttpServletRequest httpServletRequest) {
        securityUserTokenService.logout(httpServletRequest);
        return new ClientResponseDto<>(true, FORM_SUBMIT_SUCCESS);
    }

}
