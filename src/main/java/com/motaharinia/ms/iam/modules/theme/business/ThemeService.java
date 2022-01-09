package com.motaharinia.ms.iam.modules.theme.business;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.motaharinia.ms.iam.modules.theme.presentation.dto.*;
import com.motaharinia.msjpautility.page.CustomPageResponseDto;
import org.springframework.data.domain.Pageable;

import javax.validation.constraints.NotNull;
import java.util.Set;


/**
 * کلاس اینترفیس سرویس تم
 */
public interface ThemeService {

    /**
     * متد ثبت تم
     * @param dto  کلاس مدل درخواست ثبت تم
     * @return خروجی: کلاس ریسپانس تم
     */
    ThemeResponseDto create(@NotNull ThemeCreateRequestDto dto);

    /**
     * متد ویرایش تم
     * @param dto  کلاس مدل درخواست ویرایش تم
     * @return خروجی: کلاس ریسپانس تم
     */
    ThemeResponseDto update(@NotNull ThemeUpdateRequestDto dto);

    /**
     * متد حذف تم
     * @param idSet     لیستی از شناسه های تم
     */
    void delete(@NotNull Set<Long> idSet);

    /**
     * متد مشاهده اطلاعات تم
     * @param id شناسه تم
     * @return خروجی: کلاس ریسپانس مشاهده اطلاعات تم
     */
    ThemeReadResponseDto read(@NotNull Long id) throws JsonProcessingException;

    /**
     * مشاهده تمام تم ها
     * @param pageable اطلاعات صفحه بندی
     * @return خروجی: کلاس ریسپانس مشاهده اطلاعات تم
     */
    CustomPageResponseDto<ThemeReadMinimalResponseDto> readAll(Pageable pageable);

    /**
     * متد ست کردن تم پیشفرض
     * @param dto مدل ست کردن تم پیشفرض
     * @return خروجی: مدل ست کردن تم پیشفرض
     */
    ThemeSetThemeDto setDefaultTheme(ThemeSetThemeDto dto);

    /**
     * مشاهده اطلاعات تم پیشفرض
     * @return خروجی: کلاس ریسپانس مشاهده اطلاعات تم
     */
    ThemeReadResponseDto readDefaultTheme();


}
