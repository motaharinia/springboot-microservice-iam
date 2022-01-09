package com.motaharinia.ms.iam.modules.securityuser.business.service;

import com.motaharinia.ms.iam.modules.securityuser.persistence.orm.SecurityPermission;
import com.motaharinia.ms.iam.modules.securityuser.presentation.securitypermission.SecurityPermissionCreateRequestDto;
import com.motaharinia.ms.iam.modules.securityuser.presentation.securitypermission.SecurityPermissionReadResponseDto;
import com.motaharinia.ms.iam.modules.securityuser.presentation.securitypermission.SecurityPermissionResponseDto;
import com.motaharinia.ms.iam.modules.securityuser.presentation.securitypermission.SecurityPermissionUpdateRequestDto;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;

/**
 * @author eng.motahari@gmail.com<br>
 * کلاس اینترفیس سرویس دسترسی کاربر
 */
public interface SecurityPermissionService {

    //-------------------------------------------------------------
    //Read method
    //-------------------------------------------------------------

    /**
     * جستجو با آیدی
     *
     * @param id آیدی دسترسی
     * @return خروجی: انتیتی دسترسی
     */
    SecurityPermission serviceReadById(Long id);

    /**
     * جستجو دسترسی های فرانت
     * @param id آیدی نقش کاربری
     * @return SecurityPermission خروجی:انتیتی نقش کاربری
     */
    SecurityPermission serviceReadByIdForFront(@NotNull Long id);

    /**
     * جستجو دسترسی های بک
     * @param id آیدی نقش کاربری
     * @return SecurityPermission خروجی:انتیتی نقش کاربری
     */
    SecurityPermission serviceReadByIdForBack(@NotNull Long id);


    /**
     * جستجوی دسترسی های یک نقش کاربری
     * @param roleId آیدی نقش کاربری
     * @return List<SecurityPermissionDto> لیست مدل خروجی
     */
    List<SecurityPermissionReadResponseDto> serviceReadAllByRoleId(@NotNull Long roleId);
    //-------------------------------------------------------------
    //CRUD
    //-------------------------------------------------------------
    /**
     * متد جستجو دسترسی با آیدی برای کاربران برنامه فرانت
     * @param id  آیدی دسترسی
     * @return خروجی: مدل مشاهده دسترسی
     */
    SecurityPermissionReadResponseDto readByIdForFront(@NotNull Long id);

    /**
     *متد جستجو دسترسی برای کاربران برنامه فرانت
     * @param parentId  آیدی دسترسی
     * @param authoritySet  لیست دسترسی های شخصی که لاگین کرده است
     * @param authoritySet
     * @return
     */
    List<SecurityPermissionReadResponseDto> readAllForFront(Long parentId, Set<String> authoritySet);
    /**
     * متد جستجو دسترسی با آیدی برای کاربران برنامه بک
     * @param id  آیدی دسترسی
     * @return خروجی: مدل مشاهده دسترسی
     */
    SecurityPermissionReadResponseDto readByIdForBack(@NotNull Long id);

    /**
     *متد جستجو دسترسی برای کاربران برنامه بک
     * @param parentId  آیدی دسترسی
     * @param authoritySet  لیست دسترسی های شخصی که لاگین کرده است
     * @param authoritySet
     * @return
     */
    List<SecurityPermissionReadResponseDto> readAllForBack(Long parentId, Set<String> authoritySet);


    /**
     * متد ثبت دسترسی
     * @param dto  مدل ثبت دسترسی
     * @return خروجی: مدل دسترسی
     */
    SecurityPermissionResponseDto create(@NotNull SecurityPermissionCreateRequestDto dto);

    /**
     * متد ویرایش دسترسی
     * @param dto  مدل ویرایش دسترسی
     * @return خروجی: مدل دسترسی
     */
    SecurityPermissionResponseDto update(@NotNull SecurityPermissionUpdateRequestDto dto);

    /**
     * متد حذف دسترسی
     * @param id  آیدی دسترسی
     */
    void delete(@NotNull Long id);

}
