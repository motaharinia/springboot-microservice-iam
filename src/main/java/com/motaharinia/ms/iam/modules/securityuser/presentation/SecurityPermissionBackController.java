package com.motaharinia.ms.iam.modules.securityuser.presentation;
import com.motaharinia.ms.iam.config.security.oauth2.enumeration.AuthorityConstant;
import com.motaharinia.ms.iam.modules.securityuser.business.service.SecurityPermissionService;
import com.motaharinia.ms.iam.modules.securityuser.presentation.securitypermission.SecurityPermissionReadResponseDto;
import com.motaharinia.msutility.custom.customdto.ClientResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * کلاس کنترلر دسترسی کاربر برای کاربر برنامه بک
 */
@Slf4j
@RestController
@RequestMapping("/api/v1.0/security-permission-back")
public class SecurityPermissionBackController {

    private final SecurityPermissionService securityPermissionService;


    /**
     * پیام موفقیت فرم
     */
    private static final String FORM_SUBMIT_SUCCESS = "USER_MESSAGE.FORM_SUBMIT_SUCCESS";

    public SecurityPermissionBackController(SecurityPermissionService securityPermissionService) {
        this.securityPermissionService = securityPermissionService;
    }

    /**
     * جستجوی دسترسی با شناسه
     *
     * @param id شناسه دسترسی
     * @return SecurityPermissionDto مدل مشاهده اطلاعات دسترسی ها
     */
    @Operation(security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('" + AuthorityConstant.IAM_BACK_USER_PERMISSION_READ + "')")
    public ClientResponseDto<SecurityPermissionReadResponseDto> readByIdForBack(@PathVariable Long id) {
        //جستجوی دسترسی با شناسه
        SecurityPermissionReadResponseDto securityPermissionReadResponseDto = securityPermissionService.readByIdForBack(id);
        return new ClientResponseDto<>(securityPermissionReadResponseDto, FORM_SUBMIT_SUCCESS);
    }


    /**
     * جنریت کردن کل درخت دسترسی
     *
     * @return List<SecurityPermissionDto> لیست مدل مشاهده اطلاعات دسترسی ها
     */
    @Operation(security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/read-all")
    @PreAuthorize("hasAnyAuthority('" + AuthorityConstant.IAM_BACK_USER_PERMISSION_READ + "')")
    public ClientResponseDto<List<SecurityPermissionReadResponseDto>> readAllForBack(@RequestParam(required = false) Long parentId) {
        //جستجوی دسترسی ، جنریت کردن درخت دسترسی
        List<SecurityPermissionReadResponseDto> securityPermissionReadResponseDtoList = securityPermissionService.readAllForBack(parentId, null);

        return new ClientResponseDto<>(securityPermissionReadResponseDtoList, FORM_SUBMIT_SUCCESS);
    }



}
