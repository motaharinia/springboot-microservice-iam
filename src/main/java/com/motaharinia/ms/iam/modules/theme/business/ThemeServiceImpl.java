package com.motaharinia.ms.iam.modules.theme.business;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.motaharinia.ms.iam.config.security.oauth2.resource.ResourceUserTokenProvider;
import com.motaharinia.ms.iam.modules.appuser.business.service.AppUserService;
import com.motaharinia.ms.iam.modules.backuser.business.BackUserService;
import com.motaharinia.ms.iam.modules.fso.FsoSetting;
import com.motaharinia.ms.iam.modules.fso.business.enumeration.SubSystemEnum;
import com.motaharinia.ms.iam.modules.fso.business.service.FsoService;
import com.motaharinia.ms.iam.modules.theme.business.exception.ThemeException;
import com.motaharinia.ms.iam.modules.theme.business.mapper.ThemeMapper;
import com.motaharinia.ms.iam.modules.theme.persistence.odm.ThemeDocument;
import com.motaharinia.ms.iam.modules.theme.persistence.odm.ThemeDocumentRepository;
import com.motaharinia.ms.iam.modules.theme.presentation.dto.*;
import com.motaharinia.msjpautility.document.customcounter.CustomCounterService;
import com.motaharinia.msjpautility.page.CustomPageResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * کلاس پیاده سازی سرویس تم
 */
@Slf4j
@Service
//@Transactional(rollbackFor = Exception.class)
@Transactional(rollbackFor = Exception.class, value = "mongoTransactionManager")
public class ThemeServiceImpl implements ThemeService {

    private final ThemeDocumentRepository themeDocumentRepository;
    private final ThemeMapper themeMapper;
    private final FsoService fsoService;
    private final CustomCounterService customCounterService;
    private final ResourceUserTokenProvider resourceUserTokenProvider;
    private final AppUserService appUserService;
    private final BackUserService backUserService;

    public static final String BUSINESS_EXCEPTION_THEME_ID_NOT_FOUND = "BUSINESS_EXCEPTION.THEME_ID_NOT_FOUND";
    public static final String BUSINESS_EXCEPTION_THEME_CAN_NOT_DELETE_DEFAULT_THEME = "BUSINESS_EXCEPTION.THEME_CAN_NOT_DELETE_DEFAULT_THEME";
    public static final String BUSINESS_EXCEPTION_THEME_TITLE_IS_DUPLICATE = "BUSINESS_EXCEPTION.THEME_TITLE_IS_DUPLICATE";
    public static final String BUSINESS_EXCEPTION_THEME_DEFAULT_THEME_NOT_FOUND = "BUSINESS_EXCEPTION.THEME_DEFAULT_THEME_NOT_FOUND";

    public ThemeServiceImpl(ThemeDocumentRepository themeDocumentRepository, ThemeMapper themeMapper, FsoService fsoService, CustomCounterService customCounterService, ResourceUserTokenProvider resourceUserTokenProvider, AppUserService appUserService, BackUserService backUserService) {
        this.themeDocumentRepository = themeDocumentRepository;
        this.themeMapper = themeMapper;
        this.fsoService = fsoService;
        this.customCounterService = customCounterService;
        this.resourceUserTokenProvider = resourceUserTokenProvider;
        this.appUserService = appUserService;
        this.backUserService = backUserService;
    }

    /**
     * متد ثبت تم
     *
     * @param dto کلاس مدل درخواست ثبت تم
     * @return خروجی: کلاس ریسپانس تم
     */
    @Override
    public ThemeResponseDto create(@NotNull ThemeCreateRequestDto dto) {
        //نام تم نباید تکراری باشد
        if (themeDocumentRepository.findByTitle(dto.getTitle()).isPresent())
            throw new ThemeException(dto.getTitle(), BUSINESS_EXCEPTION_THEME_TITLE_IS_DUPLICATE, "");

        ThemeDocument themeDocument = themeMapper.toEntity(dto);
        //جنریت کردن primaryKey برای دیتابیس مانگو
        themeDocument.setId(customCounterService.generatePrimaryKey(ThemeDocument.class.getName()));
        return new ThemeResponseDto(themeDocumentRepository.save(themeDocument).getId());
    }

    /**
     * متد ویرایش تم
     *
     * @param dto کلاس مدل درخواست ویرایش تم
     * @return خروجی: کلاس ریسپانس تم
     */
    @Override
    public ThemeResponseDto update(@NotNull ThemeUpdateRequestDto dto) {
        //جستجو با شناسه
        ThemeDocument themeDocument = themeDocumentRepository.findById(dto.getId()).orElseThrow(() -> new ThemeException(dto.getId().toString(), BUSINESS_EXCEPTION_THEME_ID_NOT_FOUND, ""));

        //نام تم نباید تکراری باشد
        Optional<ThemeDocument> themeDocumentByTitle = themeDocumentRepository.findByTitle(dto.getTitle());
        if ((themeDocumentByTitle.isPresent()) && !(themeDocument.getId().equals(themeDocumentByTitle.get().getId())))
            throw new ThemeException(dto.getTitle(), BUSINESS_EXCEPTION_THEME_TITLE_IS_DUPLICATE, "");

        //کپی کردن فیلدهای مدل در انتیتی
        themeMapper.toEntity(dto, themeDocument);
        return new ThemeResponseDto(themeDocumentRepository.save(themeDocument).getId());

    }

    /**
     * متد حذف تم
     *
     * @param idSet لیستی از شناسه های تم
     */
    @Override
    public void delete(@NotNull Set<Long> idSet) {
        idSet.forEach(id -> {
            //جستجو با شناسه
            ThemeDocument themeDocument = themeDocumentRepository.findById(id).orElseThrow(() -> new ThemeException(id.toString(), BUSINESS_EXCEPTION_THEME_ID_NOT_FOUND, ""));
            if (themeDocument.getIsDefault()) {
                throw new ThemeException(id.toString(), BUSINESS_EXCEPTION_THEME_CAN_NOT_DELETE_DEFAULT_THEME, "");
            }
            //حذف تم
            themeDocumentRepository.delete(themeDocument);
        });
    }

    /**
     * متد مشاهده اطلاعات تم
     *
     * @param id شناسه تم
     * @return خروجی: کلاس ریسپانس مشاهده اطلاعات تم
     */
    @Override
    public ThemeReadResponseDto read(@NotNull Long id) throws JsonProcessingException {
        //جستجو با شناسه
        ThemeDocument themeDocument = themeDocumentRepository.findById(id).orElseThrow(() -> new ThemeException(id.toString(), BUSINESS_EXCEPTION_THEME_ID_NOT_FOUND, ""));
        return this.FillThemeReadResponseDto(themeDocument);
    }

    /**
     * مشاهده تمام تم ها
     *
     * @param pageable اطلاعات صفحه بندی
     * @return خروجی: کلاس ریسپانس مشاهده اطلاعات تم
     */
    public CustomPageResponseDto<ThemeReadMinimalResponseDto> readAll(Pageable pageable) {
        return new CustomPageResponseDto<>(themeDocumentRepository.findAll(pageable).map(themeMapper::toThemeReadMinimalResponseDto));
    }

    /**
     * متد ست کردن تم پیشفرض
     *
     * @param dto مدل ست کردن تم پیشفرض
     * @return خروجی: مدل ست کردن تم پیشفرض
     */
    @Override
    public ThemeSetThemeDto setDefaultTheme(ThemeSetThemeDto dto) {
        //جستجو تم با شناسه جهت ست شدن بعنوان تم پیشفرض
        ThemeDocument themeDocument = themeDocumentRepository.findById(dto.getId()).orElseThrow(() -> new ThemeException(dto.getId().toString(), BUSINESS_EXCEPTION_THEME_ID_NOT_FOUND, ""));
        //اگر شناسه تم ارسال شده تم پیشفرض نباشد ، آن تم پیشفرض میشود
        if (Boolean.FALSE.equals(themeDocument.getIsDefault())) {
            themeDocument.setIsDefault(true);
            themeDocumentRepository.save(themeDocument);
        }

        //بقیه تم ها از حالت پیشفرض خارج میشوند بجز تمی که آیدی آن در مدل ارسال شده است
        List<ThemeDocument> themeDocumentList = themeDocumentRepository.findByIdNot(dto.getId());
        themeDocumentList.forEach(themeDocument1 -> {
            themeDocument1.setIsDefault(false);
            themeDocumentRepository.save(themeDocument1);
        });

        return dto;
    }

    /**
     * مشاهده اطلاعات تم پیشفرض
     *
     * @return خروجی: کلاس ریسپانس مشاهده اطلاعات تم
     */
    @Override
    public ThemeReadResponseDto readDefaultTheme() {
        ThemeDocument themeDocument = themeDocumentRepository.findByIsDefaultIsTrue().orElseThrow(() -> new ThemeException("", BUSINESS_EXCEPTION_THEME_DEFAULT_THEME_NOT_FOUND, ""));
        return this.FillThemeReadResponseDto(themeDocument);
    }

    /**
     * متد پر کردن مدل ریسپانس ThemeReadResponseDto جهت مشاهده اطلاعات
     * @param themeDocument انتیتی تم
     * @return خروجی: کلاس ریسپانس مشاهده اطلاعات تم
     */
    private ThemeReadResponseDto FillThemeReadResponseDto(ThemeDocument themeDocument) {
        //ست کردن انتیتی و تصاویر تم در مدل
        ThemeReadResponseDto dto = readFso(themeMapper.toDto(themeDocument));

        //ست کردن هش مپ فایل با مسیر هش شده در مدل
        dto.getImageList().forEach(fileViewDto -> dto.getImageHashMap().put(fileViewDto.getFullName(), "/api/v1.0/fso/download/" + SubSystemEnum.MS_IAM + "/theme/" + fileViewDto.getHashedPath() + "/"));

        return dto;
    }

    private ThemeReadResponseDto readFso(ThemeReadResponseDto dto) {
        dto.setImageList(fsoService.readFileViewDtoList(FsoSetting.MS_IAM_THEME_IMAGES, dto.getId()));
        return dto;
    }


}
