package com.motaharinia.ms.iam.modules.securityuser.business.service;

import com.motaharinia.ms.iam.modules.securityuser.business.exception.SecurityPermissionException;
import com.motaharinia.ms.iam.modules.securityuser.business.exception.SecurityRoleException;
import com.motaharinia.ms.iam.modules.securityuser.business.mapper.SecurityPermissionMapper;
import com.motaharinia.ms.iam.modules.securityuser.persistence.orm.SecurityPermission;
import com.motaharinia.ms.iam.modules.securityuser.persistence.orm.SecurityPermissionRepository;
import com.motaharinia.ms.iam.modules.securityuser.presentation.securitypermission.SecurityPermissionCreateRequestDto;
import com.motaharinia.ms.iam.modules.securityuser.presentation.securitypermission.SecurityPermissionReadResponseDto;
import com.motaharinia.ms.iam.modules.securityuser.presentation.securitypermission.SecurityPermissionResponseDto;
import com.motaharinia.ms.iam.modules.securityuser.presentation.securitypermission.SecurityPermissionUpdateRequestDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author eng.motahari@gmail.com<br>
 * کلاس پیاده سازی سرویس دسترسی کاربر
 */
@Service
@Slf4j
@Transactional(rollbackFor = Exception.class)
public class SecurityPermissionServiceImpl implements SecurityPermissionService {

    private final SecurityPermissionRepository securityPermissionRepository;
    private final SecurityPermissionMapper securityPermissionMapper;

    public SecurityPermissionServiceImpl(SecurityPermissionRepository securityPermissionRepository, SecurityPermissionMapper securityPermissionMapper) {
        this.securityPermissionRepository = securityPermissionRepository;
        this.securityPermissionMapper = securityPermissionMapper;
    }

    private static final String BUSINESS_EXCEPTION_SECURITY_PERMISSION_ID_NOT_FOUND = "BUSINESS_EXCEPTION.SECURITY_PERMISSION_ID_NOT_FOUND";
    private static final String BUSINESS_EXCEPTION_SECURITY_PERMISSION_AUTHORITY_IS_DUPLICATE = "BUSINESS_EXCEPTION.SECURITY_PERMISSION_AUTHORITY_IS_DUPLICATE";
    private static final String BUSINESS_EXCEPTION_SECURITY_PERMISSION_HAS_DEPENDENCY = "BUSINESS_EXCEPTION.SECURITY_PERMISSION_HAS_DEPENDENCY";


    //-------------------------------------------------------------
    //Find method or Read method
    //-------------------------------------------------------------

    /**
     * جستجو با آیدی
     *
     * @param id آیدی دسترسی
     * @return خروجی: انتیتی دسترسی
     */
    public SecurityPermission serviceReadById(Long id) {
        return securityPermissionRepository.findById(id).orElseThrow(() -> new SecurityPermissionException(id.toString(), BUSINESS_EXCEPTION_SECURITY_PERMISSION_ID_NOT_FOUND, "id:" + id));
    }

    /**
     * جستجو دسترسی های فرانت
     *
     * @param id آیدی نقش کاربری
     * @return SecurityPermission خروجی:انتیتی نقش کاربری
     */
    @Override
    public SecurityPermission serviceReadByIdForFront(@NotNull Long id) {
        return securityPermissionRepository.findByIdAndIsFrontIsTrue(id).orElseThrow(() -> new SecurityRoleException(id.toString(), BUSINESS_EXCEPTION_SECURITY_PERMISSION_ID_NOT_FOUND, "id:" + id));
    }


    /**
     * جستجو دسترسی های بک
     *
     * @param id آیدی نقش کاربری
     * @return SecurityPermission خروجی:انتیتی نقش کاربری
     */
    @Override
    public SecurityPermission serviceReadByIdForBack(@NotNull Long id) {
        return securityPermissionRepository.findByIdAndIsFrontIsFalse(id).orElseThrow(() -> new SecurityRoleException(id.toString(), BUSINESS_EXCEPTION_SECURITY_PERMISSION_ID_NOT_FOUND, "id:" + id));
    }


    //-------------------------------------------------------------
    //Read method
    //-------------------------------------------------------------

    /**
     * چک کردن تکراری نبودن نام دسترسی (authority)
     *
     * @param authority نام دسترسی
     * @param isFront   آیا دسترسی برای فرانت است؟
     * @param id        شناسه دسترسی جهت چک کردن تکراری نبودن عنوان در هنگام ویرایش
     */
    private void serviceCheckDuplicateAuthorityWithException(String authority, Boolean isFront, Long id) {
        //بررسی وجود نام و جستجو-در صورتی که وجود داشته باشد خطا صادر میشود
        if (Boolean.TRUE.equals(isFront)) {
            if (securityPermissionRepository.findByAuthorityAndIsFrontIsTrue(authority).isPresent()) {
                Optional<SecurityPermission> securityPermission = securityPermissionRepository.findByAuthorityAndIsFrontIsTrue(authority);
                //بررسی میکند که در ویرایش ، آیدی خالی نباشد و آیدی ها باهم برابر باشند
                if (id == null) {
                    throw new SecurityRoleException(authority, BUSINESS_EXCEPTION_SECURITY_PERMISSION_AUTHORITY_IS_DUPLICATE, "authority:" + authority);
                } else if (securityPermission.isPresent() && !Objects.equals(id, securityPermission.get().getId()))
                    throw new SecurityRoleException(authority, BUSINESS_EXCEPTION_SECURITY_PERMISSION_AUTHORITY_IS_DUPLICATE, "authority:" + authority);
            }
        } else {
            if (securityPermissionRepository.findByAuthorityAndIsFrontIsFalse(authority).isPresent()) {
                Optional<SecurityPermission> securityPermission = securityPermissionRepository.findByAuthorityAndIsFrontIsFalse(authority);
                //بررسی میکند که در ویرایش ، آیدی خالی نباشد و آیدی ها باهم برابر باشند
                if (id == null) {
                    throw new SecurityRoleException(authority, BUSINESS_EXCEPTION_SECURITY_PERMISSION_AUTHORITY_IS_DUPLICATE, "authority:" + authority);
                } else if (securityPermission.isPresent() && !Objects.equals(id, securityPermission.get().getId()))
                    throw new SecurityRoleException(authority, BUSINESS_EXCEPTION_SECURITY_PERMISSION_AUTHORITY_IS_DUPLICATE, "authority:" + authority);

            }
        }
    }


    /**
     * جستجوی دسترسی های یک نقش کاربری
     *
     * @param roleId آیدی نقش کاربری
     * @return List<SecurityPermissionDto> لیست مدل خروجی
     */
    public List<SecurityPermissionReadResponseDto> serviceReadAllByRoleId(@NotNull Long roleId) {
        List<SecurityPermission> securityPermissionList = securityPermissionRepository.findBySecurityRoleId(roleId);
        return securityPermissionList.stream().map(securityPermissionMapper::toDto).collect(Collectors.toList());
    }
    //-------------------------------------------------------------
    //CRUD
    //-------------------------------------------------------------


    /**
     * متد جستجو دسترسی با آیدی برای کاربران برنامه فرانت
     *
     * @param id آیدی دسترسی
     * @return خروجی: مدل مشاهده دسترسی
     */
    @Override
    public SecurityPermissionReadResponseDto readByIdForFront(@NotNull Long id) {
        //جستجو با آیدی
        SecurityPermission securityPermission = this.serviceReadByIdForFront(id);
        //تبدیل انتیتی به مدل
        return securityPermissionMapper.toDto(securityPermission);
    }

    /**
     * متد جستجو دسترسی برای کاربران برنامه فرانت
     *
     * @param parentId     آیدی دسترسی
     * @param authoritySet لیست دسترسی های شخصی که لاگین کرده است
     * @return
     */
    @Override
    public List<SecurityPermissionReadResponseDto> readAllForFront(Long parentId, Set<String> authoritySet) {
        List<SecurityPermission> securityPermissionList = new ArrayList<>();
        if (parentId == null) {
            securityPermissionList = securityPermissionRepository.findAllByParentIdIsNullAndIsFrontIsTrueOrderByMenuOrder();
        } else {
            securityPermissionList = securityPermissionRepository.findAllByParentIdAndIsFrontIsTrueOrderByMenuOrder(parentId);
        }
        List<SecurityPermissionReadResponseDto> permissionDtoList = new ArrayList<>();
        for (SecurityPermission securityPermission : securityPermissionList) {
            if (authoritySet == null || authoritySet.contains(securityPermission.getAuthority())) {
                //تبدیل انتیتی به مدل
                SecurityPermissionReadResponseDto securityPermissionReadResponseDto = securityPermissionMapper.toDto(securityPermission);
                securityPermissionReadResponseDto.setChildrenList(this.readAllForBack(securityPermission.getId(), authoritySet));
                permissionDtoList.add(securityPermissionReadResponseDto);
            }
        }
        return permissionDtoList;
    }

    /**
     * متد ثبت دسترسی برای کاربران برنامه بک
     *
     * @param id آیدی دسترسی
     * @return خروجی: مدل مشاهده دسترسی
     */
    @Override
    public SecurityPermissionReadResponseDto readByIdForBack(@NotNull Long id) {
        //جستجو با آیدی
        SecurityPermission securityPermission = this.serviceReadByIdForBack(id);
        //تبدیل انتیتی به مدل
        return securityPermissionMapper.toDto(securityPermission);
    }

    /**
     * متد جستجو دسترسی جهت جنریت کردن درخت دسترسی برای کاربران برنامه بک
     *
     * @param parentId     آیدی دسترسی
     * @param authoritySet لیست دسترسی های شخصی که لاگین کرده است
     * @return
     */
    @Override
    public List<SecurityPermissionReadResponseDto> readAllForBack(Long parentId, Set<String> authoritySet) {
        List<SecurityPermission> securityPermissionList = new ArrayList<>();
        if (parentId == null) {
            securityPermissionList = securityPermissionRepository.findAllByParentIdIsNullAndIsFrontIsFalseOrderByMenuOrder();
        } else {
            securityPermissionList = securityPermissionRepository.findAllByParentIdAndIsFrontIsFalseOrderByMenuOrder(parentId);
        }
        List<SecurityPermissionReadResponseDto> permissionDtoList = new ArrayList<>();
        for (SecurityPermission securityPermission : securityPermissionList) {
            if (authoritySet == null || authoritySet.contains(securityPermission.getAuthority())) {
                //تبدیل انتیتی به مدل
                SecurityPermissionReadResponseDto securityPermissionReadResponseDto = securityPermissionMapper.toDto(securityPermission);
                securityPermissionReadResponseDto.setChildrenList(this.readAllForBack(securityPermission.getId(), authoritySet));
                permissionDtoList.add(securityPermissionReadResponseDto);
            }
        }
        return permissionDtoList;
    }


    /**
     * متد ثبت دسترسی
     *
     * @param dto مدل ثبت دسترسی
     * @return خروجی: مدل دسترسی
     */
    @Override
    public SecurityPermissionResponseDto create(@NotNull SecurityPermissionCreateRequestDto dto) {
        //چک میکند که Authority تکراری نباشد
        this.serviceCheckDuplicateAuthorityWithException(dto.getAuthority(), dto.getIsFront(), null);

        //تبدیل مدل به انتیتی
        SecurityPermission securityPermission = securityPermissionMapper.toEntity(dto);
        //جستجو پرنت با آیدی
        if (dto.getParentId() != null) {
            SecurityPermission parent;
            if (dto.getIsFront()) {
                parent = this.serviceReadByIdForFront(dto.getParentId());
            } else {
                parent = this.serviceReadByIdForBack(dto.getParentId());
            }
            securityPermission.setParent(parent);
        }
        securityPermissionRepository.save(securityPermission);
        //[to do ] ->create Redis cache
        return new SecurityPermissionResponseDto(securityPermission.getId());
    }

    /**
     * متد ویرایش دسترسی
     *
     * @param dto مدل ویرایش دسترسی
     * @return خروجی: مدل دسترسی
     */
    @Override
    public SecurityPermissionResponseDto update(@NotNull SecurityPermissionUpdateRequestDto dto) {

        //جستجو با آیدی
        SecurityPermission securityPermission = this.serviceReadById(dto.getId());
        //چک میکند که Authority تکراری نباشد
        this.serviceCheckDuplicateAuthorityWithException(dto.getAuthority(), dto.getIsFront(), securityPermission.getId());
        //ست کردن داده های مدل در انتیتی
        securityPermission.setTitle(dto.getTitle());
        securityPermission.setAuthority(dto.getAuthority());
        securityPermission.setMenuLink(dto.getMenuLink());
        securityPermission.setMenuOrder(dto.getMenuOrder());
        securityPermission.setTypeEnum(dto.getTypeEnum());
        securityPermission.setIsFront(dto.getIsFront());
        //جستجو پرنت با آیدی
        if (dto.getParentId() != null) {
            SecurityPermission parent;
            if (dto.getIsFront()) {
                parent = this.serviceReadByIdForFront(dto.getParentId());
            } else {
                parent = this.serviceReadByIdForBack(dto.getParentId());
            }
            securityPermission.setParent(parent);
        }
        securityPermissionRepository.save(securityPermission);
        //[to do ] -> update Redis cache
        return new SecurityPermissionResponseDto(dto.getId());
    }

    /**
     * متد حذف دسترسی
     *
     * @param id آیدی دسترسی
     */
    @Override
    public void delete(@NotNull Long id) {
        //جستجو با آیدی
        SecurityPermission securityPermission = this.serviceReadById(id);
        if ((CollectionUtils.isNotEmpty(securityPermission.getSecurityRoleSet())) || (CollectionUtils.isNotEmpty(securityPermission.getSecurityUserIncludeSet())) || (CollectionUtils.isNotEmpty(securityPermission.getSecurityUserExcludeSet()))) {
            throw new SecurityPermissionException("", BUSINESS_EXCEPTION_SECURITY_PERMISSION_HAS_DEPENDENCY, "");
        }
        securityPermissionRepository.delete(securityPermission);
    }
}
