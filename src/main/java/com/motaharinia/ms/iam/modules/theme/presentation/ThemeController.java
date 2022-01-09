package com.motaharinia.ms.iam.modules.theme.presentation;

import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.MetadataException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.motaharinia.ms.iam.config.security.oauth2.enumeration.AuthorityConstant;
import com.motaharinia.ms.iam.modules.fso.FsoSetting;
import com.motaharinia.ms.iam.modules.fso.business.enumeration.CrudFileHandleActionEnum;
import com.motaharinia.ms.iam.modules.fso.business.service.FsoService;
import com.motaharinia.ms.iam.modules.fso.presentation.crudfilehandle.CrudFileHandleDto;
import com.motaharinia.ms.iam.modules.theme.business.ThemeService;
import com.motaharinia.ms.iam.modules.theme.presentation.dto.*;
import com.motaharinia.msjpautility.page.CustomPageResponseDto;
import com.motaharinia.msutility.custom.customdto.ClientResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * کلاس کنترلر تم
 */
@RestController
@RequestMapping("/api/v1.0/theme")
public class ThemeController {

    private final ThemeService themeService;
    private final FsoService fsoService;

    /**
     * پیام موفقیت فرم
     */
    private static final String FORM_SUBMIT_SUCCESS = "USER_MESSAGE.FORM_SUBMIT_SUCCESS";

    public ThemeController(ThemeService themeService, FsoService fsoService) {
        this.themeService = themeService;
        this.fsoService = fsoService;
    }

    //--------------------------------------------------------------------------------------------------------------
    //methods for BackUser (Admin Panel)
    //--------------------------------------------------------------------------------------------------------------

    /**
     * متد ثبت تم
     *
     * @param dto کلاس مدل درخواست ثبت تم
     * @return خروجی: کلاس ریسپانس تم
     */
    @Operation(security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping()
    @PreAuthorize("hasAnyAuthority('" + AuthorityConstant.IAM_THEME_CREATE + "')")
    public ClientResponseDto<ThemeResponseDto> create(@RequestBody @Validated ThemeCreateRequestDto dto) throws ImageProcessingException, MetadataException, IOException {
        //ثبت تم
        ThemeResponseDto themeResponseDto = themeService.create(dto);

        //ثبت فایلها بعد از اطمینان از ثبت انتیتی در دیتابیس
        if (CollectionUtils.isNotEmpty(dto.getImageList())) {
            CrudFileHandleDto crudFileHandleDto = new CrudFileHandleDto(themeResponseDto.getId(), CrudFileHandleActionEnum.ENTITY_CREATE, dto.getImageList(), FsoSetting.MS_IAM_THEME_IMAGES);
            fsoService.crudHandle(crudFileHandleDto);
        }

        return new ClientResponseDto<>(themeResponseDto, FORM_SUBMIT_SUCCESS);
    }

    /**
     * متد ویرایش تم
     *
     * @param dto کلاس مدل درخواست ویرایش تم
     * @return خروجی: کلاس ریسپانس تم
     */
    @Operation(security = @SecurityRequirement(name = "bearerAuth"))
    @PutMapping()
    @PreAuthorize("hasAnyAuthority('" + AuthorityConstant.IAM_THEME_UPDATE + "')")
    public ClientResponseDto<ThemeResponseDto> update(@RequestBody @Validated ThemeUpdateRequestDto dto) throws ImageProcessingException, MetadataException, IOException {
        //ویرایش تم
        ThemeResponseDto themeResponseDto = themeService.update(dto);

        //ثبت فایلها بعد از اطمینان از ثبت انتیتی در دیتابیس
        if (CollectionUtils.isNotEmpty(dto.getImageList())) {
            CrudFileHandleDto crudFileHandleDto = new CrudFileHandleDto(themeResponseDto.getId(), CrudFileHandleActionEnum.ENTITY_UPDATE, dto.getImageList(), FsoSetting.MS_IAM_THEME_IMAGES);
            fsoService.crudHandle(crudFileHandleDto);
        }

        return new ClientResponseDto<>(themeResponseDto, FORM_SUBMIT_SUCCESS);
    }

    /**
     * متد حذف تم
     *
     * @param ids شناسه تم ها بصورت csv
     * @return Boolean
     */
    @Operation(security = @SecurityRequirement(name = "bearerAuth"))
    @DeleteMapping("{ids}")
    @PreAuthorize("hasAnyAuthority('" + AuthorityConstant.IAM_THEME_DELETE + "')")
    public ClientResponseDto<Boolean> delete(@PathVariable String ids) throws ImageProcessingException, MetadataException, IOException {

        //تبدیل csv آیدیها به لیست
        Set<Long> idSet = Stream.of(ids.split(",")).map(Long::parseLong).collect(Collectors.toSet());

        //حذف تم
        themeService.delete(idSet);

        //حذف فایلها بعد از اطمینان از حذف انتیتی در دیتابیس
        for (Long id : idSet) {
            CrudFileHandleDto crudFileHandleDto = new CrudFileHandleDto(id, CrudFileHandleActionEnum.ENTITY_DELETE, new ArrayList<>(), FsoSetting.MS_IAM_THEME_IMAGES);
            fsoService.crudHandle(crudFileHandleDto);
        }

        return new ClientResponseDto<>(Boolean.TRUE, FORM_SUBMIT_SUCCESS);
    }

    /**
     * متد مشاهده اطلاعات تم
     *
     * @param id شناسه تم
     * @return خروجی: کلاس ریسپانس مشاهده اطلاعات تم
     */
    @Operation(security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('" + AuthorityConstant.IAM_THEME_READ + "')")
    public ClientResponseDto<ThemeReadResponseDto> readById(@PathVariable Long id) throws JsonProcessingException {
        //مشاهده تم
        return new ClientResponseDto<>(themeService.read(id), FORM_SUBMIT_SUCCESS);
    }

    /**
     * متد مشاهده اطلاعات تم ها
     *
     * @return خروجی: کلاس ریسپانس مشاهده اطلاعات تم
     */
    @Operation(security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/read-all")
    @PreAuthorize("hasAnyAuthority('" + AuthorityConstant.IAM_THEME_READ + "')")
    public ClientResponseDto<CustomPageResponseDto<ThemeReadMinimalResponseDto>> readAll(@PageableDefault(page = 0, size = 20) @SortDefault.SortDefaults({@SortDefault(sort = "createAt", direction = Sort.Direction.DESC)}) Pageable pageable) throws ImageProcessingException, MetadataException, IOException {
        //مشاهده تم
        return new ClientResponseDto<>(themeService.readAll(pageable), FORM_SUBMIT_SUCCESS);
    }

    /**
     * متد ست کردن تم پیشفرض
     *
     * @param dto مدل ست کردن تم برای کاربر
     * @return خروجی: مدل ست کردن تم برای کاربر
     */
    @Operation(security = @SecurityRequirement(name = "bearerAuth"))
    @PutMapping("/set-default-theme")
    @PreAuthorize("hasAnyAuthority('" + AuthorityConstant.IAM_THEME_UPDATE + "')")
    public ClientResponseDto<ThemeSetThemeDto> setDefaultTheme(@RequestBody @Validated ThemeSetThemeDto dto) {
        //ست کردن تم پیشفرض
        return new ClientResponseDto<>(themeService.setDefaultTheme(dto), FORM_SUBMIT_SUCCESS);
    }

    /**
     * متد مشاهده اطلاعات تم پیشفرض
     * برای appUserوbackUser کاربرد دارد
     *
     * @return خروجی: کلاس ریسپانس مشاهده اطلاعات تم
     */
    @Operation(security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/read-default-theme")
    public ClientResponseDto<ThemeReadResponseDto> readDefaultTheme() {
        //مشاهده تم
        return new ClientResponseDto<>(themeService.readDefaultTheme(), FORM_SUBMIT_SUCCESS);
    }
}
