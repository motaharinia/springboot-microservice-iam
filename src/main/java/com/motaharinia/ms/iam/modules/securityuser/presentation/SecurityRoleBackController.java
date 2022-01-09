package com.motaharinia.ms.iam.modules.securityuser.presentation;

import com.motaharinia.ms.iam.config.security.oauth2.enumeration.AuthorityConstant;
import com.motaharinia.ms.iam.modules.securityuser.business.enumeration.SecurityRoleGridSearchTypeEnum;
import com.motaharinia.ms.iam.modules.securityuser.business.service.SecurityRoleService;
import com.motaharinia.ms.iam.modules.securityuser.presentation.securityrole.SecurityRoleCreateRequestDto;
import com.motaharinia.ms.iam.modules.securityuser.presentation.securityrole.SecurityRoleReadResponseDto;
import com.motaharinia.ms.iam.modules.securityuser.presentation.securityrole.SecurityRoleResponseDto;
import com.motaharinia.ms.iam.modules.securityuser.presentation.securityrole.SecurityRoleUpdateRequestDto;
import com.motaharinia.msjpautility.page.CustomPageResponseDto;
import com.motaharinia.msutility.custom.customdto.ClientResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * کلاس کنترلر نقش کاربری برای کاربر برنامه بک
 */
@Slf4j
@RestController
@RequestMapping("/api/v1.0/security-role-back")
public class SecurityRoleBackController {

    private final SecurityRoleService securityRoleService;


    /**
     * پیام موفقیت فرم
     */
    private static final String FORM_SUBMIT_SUCCESS = "USER_MESSAGE.FORM_SUBMIT_SUCCESS";

    public SecurityRoleBackController(SecurityRoleService securityRoleService) {
        this.securityRoleService = securityRoleService;
    }


    //-------------------------------------------------------
    //CRUD
    //-------------------------------------------------------
    /**
     * جستجوی نقش کاربری با شناسه
     *
     * @param id شناسه نقش کاربری
     * @return SecurityRoleDto  مدل مشاهده نقش کاربری
     */
    @Operation(security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/{id}")
    //@PreAuthorize("hasAnyAuthority('" + AuthorityConstant.IAM_BACK_USER_ROLE_READ + "')")
    public ClientResponseDto<SecurityRoleReadResponseDto> readById(@PathVariable Long id) {
        //جستجوی نقش کاربری با شناسه
        SecurityRoleReadResponseDto securityRoleReadResponseDto = securityRoleService.readById(id);
        return new ClientResponseDto<>(securityRoleReadResponseDto, FORM_SUBMIT_SUCCESS);
    }

    /**
     * جستجو تمامی نقش های کاربری
     *
     * @return Page<SecurityRoleDto>  لیست مدل مشاهده نقش کاربری
     */
    @Operation(security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/read-all")
    @PreAuthorize("hasAnyAuthority('" + AuthorityConstant.IAM_BACK_USER_ROLE_READ + "')")
    public ClientResponseDto<CustomPageResponseDto<SecurityRoleReadResponseDto>> readAll(@RequestParam(value = "searchType", required = false) SecurityRoleGridSearchTypeEnum searchType, @RequestParam(value = "searchValue", required = false) String searchValue,
                                                                                         @PageableDefault(page = 0, size = 20) @SortDefault.SortDefaults({@SortDefault(sort = "createAt", direction = Sort.Direction.DESC)}) Pageable pageable) {
        //جستجوی نقش کاربری
        CustomPageResponseDto<SecurityRoleReadResponseDto> securityRoleReadResponseDtoSet = securityRoleService.readAll(searchType, searchValue, pageable);
        return new ClientResponseDto<>(securityRoleReadResponseDtoSet, FORM_SUBMIT_SUCCESS);
    }

    /**
     * ثبت نقش کاربری
     *
     * @param dto مدل ثبت اطلاعات نقش کاربری
     * @return SecurityRoleResponseDto مدل پاسخ نقش کاربری
     */
    @Operation(security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping()
    @PreAuthorize("hasAnyAuthority('" + AuthorityConstant.IAM_BACK_USER_ROLE_CREATE + "')")
    public ClientResponseDto<SecurityRoleResponseDto> create(@RequestBody @Validated SecurityRoleCreateRequestDto dto) {
        //ثبت نقش کاربری
        SecurityRoleResponseDto securityRoleResponseDto = securityRoleService.create(dto, false);
        return new ClientResponseDto<>(securityRoleResponseDto, FORM_SUBMIT_SUCCESS);
    }


    /**
     * ویرایش اطلاعات نقش کاربری
     *
     * @param dto مدل ویرایش اطلاعات نقش کاربری
     * @return SecurityRoleResponseDto مدل پاسخ نقش کاربری
     */
    @Operation(security = @SecurityRequirement(name = "bearerAuth"))
    @PutMapping()
    @PreAuthorize("hasAnyAuthority('" + AuthorityConstant.IAM_BACK_USER_ROLE_UPDATE + "')")
    public ClientResponseDto<SecurityRoleResponseDto> update(@RequestBody @Validated SecurityRoleUpdateRequestDto dto) {
        //ویرایش نقش کاربری
        SecurityRoleResponseDto securityRoleResponseDto = securityRoleService.update(dto, false);
        return new ClientResponseDto<>(securityRoleResponseDto, FORM_SUBMIT_SUCCESS);
    }

    /**
     * فعال/غیرفعال کردن اطلاعات نقش کاربری
     *
     * @param invalid شناسه کاربر برنامه بک
     * @param ids     شناسه کاربران برنامه بک بصورت csv
     * @return SecurityRoleResponseDto مدل پاسخ نقش کاربری
     */
    @Operation(security = @SecurityRequirement(name = "bearerAuth"))
    @PutMapping("/invalid/{invalid}/{ids}")
    @PreAuthorize("hasAnyAuthority('" + AuthorityConstant.IAM_BACK_USER_ROLE_UPDATE + "')")
    public ClientResponseDto<Boolean> invalid(@PathVariable Boolean invalid, @PathVariable String ids) {
        //فعال/غیرفعال کردن نقش کاربری
        securityRoleService.invalid(invalid, ids,false);
        return new ClientResponseDto<>(true, FORM_SUBMIT_SUCCESS);
    }

    /**
     * حذف نقش کاربری
     *
     * @param ids     رشته شناسه کاربران بک بصورت csv
     * @return Boolean
     */
    @Operation(security = @SecurityRequirement(name = "bearerAuth"))
    @DeleteMapping("/{ids}")
    @PreAuthorize("hasAnyAuthority('" + AuthorityConstant.IAM_BACK_USER_ROLE_DELETE + "')")
    public ClientResponseDto<Boolean> delete(@PathVariable String ids) {
        //حذف نقش کاربری
        securityRoleService.delete(ids);
        return new ClientResponseDto<>(true, FORM_SUBMIT_SUCCESS);
    }

}
