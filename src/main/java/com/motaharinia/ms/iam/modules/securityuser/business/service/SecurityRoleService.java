package com.motaharinia.ms.iam.modules.securityuser.business.service;

import com.motaharinia.ms.iam.modules.securityuser.business.enumeration.SecurityRoleGridSearchTypeEnum;
import com.motaharinia.ms.iam.modules.securityuser.persistence.orm.SecurityRole;
import com.motaharinia.ms.iam.modules.securityuser.presentation.securityrole.SecurityRoleCreateRequestDto;
import com.motaharinia.ms.iam.modules.securityuser.presentation.securityrole.SecurityRoleReadResponseDto;
import com.motaharinia.ms.iam.modules.securityuser.presentation.securityrole.SecurityRoleResponseDto;
import com.motaharinia.ms.iam.modules.securityuser.presentation.securityrole.SecurityRoleUpdateRequestDto;
import com.motaharinia.msjpautility.page.CustomPageResponseDto;
import com.motaharinia.msutility.custom.customdto.search.data.SearchDataDto;
import com.motaharinia.msutility.custom.customdto.search.filter.SearchFilterDto;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * @author eng.motahari@gmail.com<br>
 * کلاس اینترفیس سرویس نقش کاربری
 */

public interface SecurityRoleService {

    //-------------------------------------------------------
    //Read Methods
    //------------------------------------------------------
    /**
     * متد خواندن نقش کاربری با عنوان
     *
     * @param title عنوان
     * @param isFront آیا نقش کاریری برای فرانت است؟
     * @return خروجی: انتیتی نقش کاربری
     */
    @NotNull
    SecurityRole serviceReadByTitle(@NotNull String title, Boolean isFront);

    /**
     *
     * @param id  آیدی نقش کاربری
     * @return SecurityRole خروجی:انتیتی نقش کاربری
     */
    @NotNull
    SecurityRole serviceReadById(@NotNull Long id);

    /**
     * جستجو نقش  کاربری کاربر امنیت با شناسه که فقط برای کاربر برنامه فرانت میباشد
     * @param id آیدی نقش کاربری
     * @return SecurityRole خروجی:انتیتی نقش کاربری
     */
    SecurityRole serviceReadByIdForFront(@NotNull Long id);

    /**
     * جستجو نقش  کاربری کاربر امنیت با شناسه که فقط برای کاربر برنامه فرانت میباشد که وضعیت غیرفعال هم چک میکند
     * @param id آیدی نقش کاربری
     * @return SecurityRole خروجی:انتیتی نقش کاربری
     */
    SecurityRole serviceReadByIdAndCheckInvalidForFront(@NotNull Long id);

    /**
     * جستجو نقش  کاربری کاربر امنیت با شناسه که فقط برای کاربر برنامه بک میباشد
     * @param id آیدی نقش کاربری
     * @return SecurityRole خروجی:انتیتی نقش کاربری
     */
    SecurityRole serviceReadByIdForBack(@NotNull Long id);

    /**
     * جستجو نقش  کاربری کاربر امنیت با شناسه  که فقط برای کاربر برنامه بک میباشد که وضعیت غیرفعال هم چک میکند
     * @param id آیدی نقش کاربری
     * @return SecurityRole خروجی:انتیتی نقش کاربری
     */
    SecurityRole serviceReadByIdAndCheckInvalidForBack(@NotNull Long id);


    //-------------------------------------------------------------
    //CRUD
    //-------------------------------------------------------------

    /**
     * متد جستجو نقش کاربری با آیدی
     *
     * @param id آیدی نقش کاربری
     * @return خروجی: مدل مشاهده نقش کاربری
     */
    SecurityRoleReadResponseDto readById(@NotNull Long id);

    /**
     * متد جستجو تمامی نقش های کاربری
     *
     * @param searchType نوع سرچ
     * @param searchValue مقدار سرچ
     * @param pageable     برای صفحه بندی
     * @return CustomPageResponseDto<SecurityRoleDto> لیست از مدل نقش کاربری
     */
    CustomPageResponseDto<SecurityRoleReadResponseDto> readAll(SecurityRoleGridSearchTypeEnum searchType , String searchValue, Pageable pageable);


    /**
     * متد ثبت نقش کاربری
     *
     * @param dto مدل ثبت نقش کاربری
     * @param isFront آیا نقش کاریری برای فرانت است؟
     * @return خروجی: مدل نقش کاربری
     */
    SecurityRoleResponseDto create(SecurityRoleCreateRequestDto dto , Boolean isFront);

    /**
     * متد ویرایش نقش کاربری
     *
     * @param dto مدل ویرایش نقش کاربری
     * @param isFront آیا نقش کاریری برای فرانت است؟
     * @return خروجی: مدل نقش کاربری
     */
    SecurityRoleResponseDto update(SecurityRoleUpdateRequestDto dto, Boolean isFront);

    /**
     * متد فعال یا غیرقعال کردن نقش کاربری
     *
     * @param invalid فعال/ غیرفعال
     * @param ids     رشته شناسه نقش کاربری بصورت csv
     * @param isFront آیا نقش کاریری برای فرانت است؟

     */
    void invalid(@NotNull Boolean invalid, @NotNull String ids, @NotNull Boolean isFront);

    /**
     * متد حذف نقش کاربری
     *
     * @param ids     رشته شناسه کاربران بک بصورت csv
     */
    void delete(@NotNull String ids);


    //-------------------------------------------------------------
    //این یک متد تستی است و برای جستجو پیشرفته میباشد. در حال حاضر در پروژه استفاده نمیشود
    //-------------------------------------------------------------
    @NotNull
    SearchDataDto readGrid(@NotNull SearchFilterDto searchFilterDto, @NotNull Class searchViewTypeInterface, @NotNull List<Object> searchValueList);
}
