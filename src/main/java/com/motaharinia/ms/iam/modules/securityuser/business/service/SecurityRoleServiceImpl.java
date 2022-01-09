package com.motaharinia.ms.iam.modules.securityuser.business.service;

import com.motaharinia.ms.iam.modules.backuser.business.exception.BackUserException;
import com.motaharinia.ms.iam.modules.securityuser.business.enumeration.SecurityRoleGridSearchTypeEnum;
import com.motaharinia.ms.iam.modules.securityuser.business.enumeration.SecurityTokenInvalidTypeEnum;
import com.motaharinia.ms.iam.modules.securityuser.business.enumeration.SecurityUserInvalidTokenEnum;
import com.motaharinia.ms.iam.modules.securityuser.business.exception.SecurityRoleException;
import com.motaharinia.ms.iam.modules.securityuser.business.mapper.SecurityRoleMapper;
import com.motaharinia.ms.iam.modules.securityuser.business.service.advancedsearch.SecurityRoleSearchViewTypeBrief;
import com.motaharinia.ms.iam.modules.securityuser.persistence.orm.SecurityPermission;
import com.motaharinia.ms.iam.modules.securityuser.persistence.orm.SecurityRole;
import com.motaharinia.ms.iam.modules.securityuser.persistence.orm.SecurityRoleRepository;
import com.motaharinia.ms.iam.modules.securityuser.persistence.orm.SecurityUser;
import com.motaharinia.ms.iam.modules.securityuser.persistence.orm.advancedsearch.SecurityUserSpecification;
import com.motaharinia.ms.iam.modules.securityuser.presentation.securityrole.SecurityRoleCreateRequestDto;
import com.motaharinia.ms.iam.modules.securityuser.presentation.securityrole.SecurityRoleReadResponseDto;
import com.motaharinia.ms.iam.modules.securityuser.presentation.securityrole.SecurityRoleResponseDto;
import com.motaharinia.ms.iam.modules.securityuser.presentation.securityrole.SecurityRoleUpdateRequestDto;
import com.motaharinia.msjpautility.page.CustomPageResponseDto;
import com.motaharinia.msjpautility.search.SearchTools;
import com.motaharinia.msutility.custom.customdto.search.data.SearchDataDto;
import com.motaharinia.msutility.custom.customdto.search.filter.SearchFilterDto;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author eng.motahari@gmail.com<br>
 * کلاس پیاده سازی سرویس نقش کاربری
 */

@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class SecurityRoleServiceImpl implements SecurityRoleService {

    private final SecurityRoleRepository securityRoleRepository;
    private final SecurityPermissionService securityPermissionService;
    private final SecurityRoleMapper securityRoleMapper;
    private final SecurityUserTokenService securityUserTokenService;

    private static final String BUSINESS_EXCEPTION_SECURITY_ROLE_ID_NOT_FOUND = "BUSINESS_EXCEPTION.SECURITY_ROLE_ID_NOT_FOUND";
    private static final String BUSINESS_EXCEPTION_SECURITY_ROLE_TITLE_IS_DUPLICATE = "BUSINESS_EXCEPTION.SECURITY_ROLE_TITLE_IS_DUPLICATE";
    private static final String BUSINESS_EXCEPTION_SECURITY_ROLE_TITLE_NOT_FOUND = "BUSINESS_EXCEPTION.SECURITY_ROLE_TITLE_NOT_FOUND";
    private static final String BUSINESS_EXCEPTION_SECURITY_ROLE_IS_INVALID = "BUSINESS_EXCEPTION.SECURITY_ROLE_IS_INVALID";
    private static final String BUSINESS_EXCEPTION_SECURITY_ROLE_MUST_BE_INVALID = "BUSINESS_EXCEPTION.SECURITY_ROLE_MUST_BE_INVALID";
    private static final String BUSINESS_EXCEPTION_SECURITY_ROLE_HAS_DEPENDENCY = "BUSINESS_EXCEPTION.SECURITY_ROLE_HAS_DEPENDENCY";


    public SecurityRoleServiceImpl(SecurityRoleRepository securityRoleRepository, SecurityPermissionService securityPermissionService, SecurityRoleMapper securityRoleMapper, SecurityUserTokenService securityUserTokenService) {
        this.securityRoleRepository = securityRoleRepository;
        this.securityPermissionService = securityPermissionService;
        this.securityRoleMapper = securityRoleMapper;
        this.securityUserTokenService = securityUserTokenService;
    }

    //-------------------------------------------------------------
    //Read method
    //-------------------------------------------------------------

    /**
     * متد خواندن نقش کاربری با عنوان
     *
     * @param title   عنوان
     * @param isFront آیا نقش کاریری برای فرانت است؟
     * @return خروجی: انتیتی نقش کاربری
     */
    @NotNull
    public SecurityRole serviceReadByTitle(@NotNull String title, Boolean isFront) {
        //بررسی وجود نام و جستجو-در صورتی که وجود نداشته باشد خطا صادر میشود
        if (isFront) {
            return securityRoleRepository.findByTitleAndIsFrontIsTrue(title).orElseThrow(() -> new SecurityRoleException(title, BUSINESS_EXCEPTION_SECURITY_ROLE_TITLE_NOT_FOUND, ""));
        } else {
            return securityRoleRepository.findByTitleAndIsFrontIsFalse(title).orElseThrow(() -> new SecurityRoleException(title, BUSINESS_EXCEPTION_SECURITY_ROLE_TITLE_NOT_FOUND, ""));

        }
    }

    /**
     * جستجو با آیدی
     *
     * @param id آیدی دسترسی
     * @return خروجی: انتیتی دسترسی
     */
    public @NotNull SecurityRole serviceReadById(Long id) {
        return securityRoleRepository.findById(id).orElseThrow(() -> new SecurityRoleException(id.toString(), BUSINESS_EXCEPTION_SECURITY_ROLE_ID_NOT_FOUND, "id:" + id));
    }


    /**
     * جستجو نقش  کاربری کاربر امنیت که فقط برای کاربر برنامه فرانت میباشد با شناسه
     *
     * @param id آیدی نقش کاربری
     * @return SecurityRole خروجی:انتیتی نقش کاربری
     */
    @Override
    public SecurityRole serviceReadByIdForFront(@NotNull Long id) {
        return securityRoleRepository.findByIdAndIsFrontIsTrue(id).orElseThrow(() -> new SecurityRoleException(id.toString(), BUSINESS_EXCEPTION_SECURITY_ROLE_ID_NOT_FOUND, "id:" + id));
    }

    /**
     * جستجو نقش  کاربری کاربر امنیت با شناسه که فقط برای کاربر برنامه فرانت میباشد که وضعیت غیرفعال هم چک میکند
     *
     * @param id آیدی نقش کاربری
     * @return SecurityRole خروجی:انتیتی نقش کاربری
     */
    @Override
    public SecurityRole serviceReadByIdAndCheckInvalidForFront(@NotNull Long id) {
        SecurityRole securityRole = this.serviceReadByIdForFront(id);
        if (Boolean.TRUE.equals(securityRole.getInvalid()))
            throw new SecurityRoleException(securityRole.getTitle(), BUSINESS_EXCEPTION_SECURITY_ROLE_IS_INVALID + "::" + securityRole.getTitle(), "");
        return securityRole;
    }

    /**
     * جستجو نقش  کاربری کاربر امنیت که فقط برای کاربر برنامه بک میباشد با شناسه
     *
     * @param id آیدی نقش کاربری
     * @return SecurityRole خروجی:انتیتی نقش کاربری
     */
    @Override
    public SecurityRole serviceReadByIdForBack(@NotNull Long id) {
        return securityRoleRepository.findByIdAndIsFrontIsFalse(id).orElseThrow(() -> new SecurityRoleException(id.toString(), BUSINESS_EXCEPTION_SECURITY_ROLE_ID_NOT_FOUND, "id:" + id));
    }

    /**
     * جستجو نقش  کاربری کاربر امنیت با شناسه  که فقط برای کاربر برنامه بک میباشد که وضعیت غیرفعال هم چک میکند
     *
     * @param id آیدی نقش کاربری
     * @return SecurityRole خروجی:انتیتی نقش کاربری
     */
    @Override
    public SecurityRole serviceReadByIdAndCheckInvalidForBack(@NotNull Long id) {
        SecurityRole securityRole = this.serviceReadByIdForBack(id);
        if (Boolean.TRUE.equals(securityRole.getInvalid()))
            throw new SecurityRoleException(securityRole.getTitle(), BUSINESS_EXCEPTION_SECURITY_ROLE_IS_INVALID + "::" + securityRole.getTitle(), "");
        return securityRole;
    }
//-------------------------------------------------------------
    //Check method
    //-------------------------------------------------------------

    /**
     * چک کردن تکراری نبودن نام نقش کاربری (title)
     *
     * @param title   نام نقش کاربری
     * @param isFront آیا نقش کاربری برای فرانت است؟
     * @param id      شناسه نقش کاربری جهت چک کردن تکراری نبودن عنوان در هنگام ویرایش
     */
    private void serviceCheckDuplicateTitleWithException(String title, Boolean isFront, Long id) {
        String errorMsg = "title:";
        //بررسی وجود نام و جستجو-در صورتی که وجود داشته باشد خطا صادر میشود
        if (Boolean.TRUE.equals(isFront)) {
            if (securityRoleRepository.findByTitleAndIsFrontIsTrue(title).isPresent()) {
                Optional<SecurityRole> securityRole = securityRoleRepository.findByTitleAndIsFrontIsTrue(title);
                //بررسی میکند که در ویرایش ، آیدی ها باهم برابر نباشند
                if (id == null) {
                    throw new SecurityRoleException(title, BUSINESS_EXCEPTION_SECURITY_ROLE_TITLE_IS_DUPLICATE, errorMsg + title);
                } else if (securityRole.isPresent() && !Objects.equals(id, securityRole.get().getId()))
                    throw new SecurityRoleException(title, BUSINESS_EXCEPTION_SECURITY_ROLE_TITLE_IS_DUPLICATE, errorMsg + title);
            }
        } else {
            if (securityRoleRepository.findByTitleAndIsFrontIsFalse(title).isPresent()) {
                Optional<SecurityRole> securityRole = securityRoleRepository.findByTitleAndIsFrontIsFalse(title);
                //بررسی میکند که در ویرایش ، آیدی ها باهم برابر نباشند
                if (id == null) {
                    throw new SecurityRoleException(title, BUSINESS_EXCEPTION_SECURITY_ROLE_TITLE_IS_DUPLICATE, errorMsg + title);
                } else if (securityRole.isPresent() && !id.equals(securityRole.get().getId()))
                    throw new SecurityRoleException(title, BUSINESS_EXCEPTION_SECURITY_ROLE_TITLE_IS_DUPLICATE, errorMsg + title);
            }
        }
    }

    //-------------------------------------------------------------
    //CRUD
    //-------------------------------------------------------------

    /**
     * متد جستجو نقش کاربری با آیدی
     *
     * @param id آیدی نقش کاربری
     * @return خروجی: مدل مشاهده نقش کاربری
     */
    @Override
    public SecurityRoleReadResponseDto readById(@NotNull Long id) {
        //جستجو با آیدی
        SecurityRole securityRole = this.serviceReadById(id);
        //تبدیل انتیتی به مدل
        SecurityRoleReadResponseDto securityRoleReadResponseDto = securityRoleMapper.toDto(securityRole);
        //ست کردن دسترسی های نقش کاربری
        securityRoleReadResponseDto.setSecurityPermissionReadResponseDtoSet(securityPermissionService.serviceReadAllByRoleId(id));
        return securityRoleReadResponseDto;
    }

    /**
     * متد جستجو تمامی نقش های کاربری
     *
     * @param searchType  نوع سرچ
     * @param searchValue مقدار سرچ
     * @param pageable    برای صفحه بندی
     * @return CustomPageResponseDto<SecurityRoleDto> لیست از مدل نقش کاربری
     */
    @Override
    public CustomPageResponseDto<SecurityRoleReadResponseDto> readAll(SecurityRoleGridSearchTypeEnum searchType, String searchValue, Pageable pageable) {
        Page<SecurityRole> securityRolePage = null;
        if (!ObjectUtils.isEmpty(searchType) && !ObjectUtils.isEmpty(searchValue)) {
            switch (searchType) {
                case TITLE:
                    securityRolePage = securityRoleRepository.findAllByTitleContaining(searchValue, pageable);
                    break;
                case INVALID:
                    securityRolePage = securityRoleRepository.findAllByInvalid(Boolean.parseBoolean(searchValue), pageable);
                    break;
            }
        } else {
            //گذاشتن صفحه بندی
            securityRolePage = securityRoleRepository.findAll(pageable);
        }

        if (!ObjectUtils.isEmpty(securityRolePage)) {
            Page<SecurityRoleReadResponseDto> finalPage = securityRolePage.map(securityRole -> {
                //تبدیل انتیتی به مدل
                SecurityRoleReadResponseDto dto = securityRoleMapper.toDto(securityRole);
                //ست کردن دسترسی های نقش کاربری
                dto.setSecurityPermissionReadResponseDtoSet(securityPermissionService.serviceReadAllByRoleId(securityRole.getId()));
                return dto;
            });

            return new CustomPageResponseDto<>(finalPage);
        }
        return null;
    }

    /**
     * متد ثبت نقش کاربری
     *
     * @param dto     مدل ثبت نقش کاربری
     * @param isFront آیا نقش کاریری برای فرانت است؟
     * @return خروجی: مدل نقش کاربری
     */
    @Override
    public SecurityRoleResponseDto create(SecurityRoleCreateRequestDto dto, Boolean isFront) {
        //چک میکند که نام نقش تکراری نباشد
        this.serviceCheckDuplicateTitleWithException(dto.getTitle(), isFront, null);
        //تبدیل مدل به انتیتی
        SecurityRole securityRole = securityRoleMapper.toEntity(dto);
        //ست کردن دسترسی
        if (!CollectionUtils.isEmpty(dto.getSecurityPermissionSet())) {
            if (Boolean.TRUE.equals(isFront)) {
                //چک میکند که دسترسی فرانت برای نقش کاربری فرانت باید ست شود
                //چک میکند که دسترسی فرانت برای نقش کاربری فرانت باید ست شود
                securityRole.setPermissionSet(dto.getSecurityPermissionSet().stream().map(securityPermissionService::serviceReadByIdForFront).collect(Collectors.toSet()));
            } else {
                securityRole.setPermissionSet(dto.getSecurityPermissionSet().stream().map(securityPermissionService::serviceReadByIdForBack).collect(Collectors.toSet()));
            }
        }
        securityRoleRepository.save(securityRole);
        return new SecurityRoleResponseDto(securityRole.getId());
    }

    /**
     * متد ویرایش نقش کاربری
     *
     * @param dto     مدل ویرایش نقش کاربری
     * @param isFront آیا نقش کاریری برای فرانت است؟
     * @return خروجی: مدل نقش کاربری
     */
    @Override
    public SecurityRoleResponseDto update(SecurityRoleUpdateRequestDto dto, Boolean isFront) {
        //جستجو با آیدی
        SecurityRole securityRole = this.serviceReadById(dto.getId());
        //چک میکند که نام نقش تکراری نباشد
        this.serviceCheckDuplicateTitleWithException(dto.getTitle(), isFront, securityRole.getId());
        //ست کردن داده های مدل در انتیتی
        securityRole.setTitle(dto.getTitle());


        //----------------------------------------------
        //مقایسه دسترسی ها : در صورتی که دسترسی های یک نقش کم یا زیاد شود باید توکن کاربرانی که نقش موردنظر را دارند را غیرفعال کنیم
        //----------------------------------------------
        //جستجو آیدی های دسترسی یک رول
        Set<Long> securityPermissionIdDBList = securityRole.getPermissionSet().stream().map(SecurityPermission::getId).collect(Collectors.toSet());
        //اگر تعداد آیدی دسترسی ها با هم متفاوت باشد یعنی توکن کاربران باید غیر فعال شود
        if (dto.getSecurityPermissionSet().size() != securityPermissionIdDBList.size()) {
            //غیرفعال کردن توکن با توجه به نوع فیلد isFront
            this.serviceInvalidToken(securityRole, Boolean.TRUE.equals((isFront)) ? SecurityUserInvalidTokenEnum.JUST_FRONT : SecurityUserInvalidTokenEnum.JUST_BACK, SecurityTokenInvalidTypeEnum.SECURITY_ROLE_UPDATE);
        } else {
            //اگر تعداد آیدی دسترسی ها با هم یکی باشد ولی آیدی هایشان باهم متفاوت باشند یعنی توکن کاربران باید غیر فعال شود
            securityPermissionIdDBList.removeAll(dto.getSecurityPermissionSet());
            if (!ObjectUtils.isEmpty(securityPermissionIdDBList)) {
                //غیرفعال کردن توکن با توجه به نوع فیلد isFront
                this.serviceInvalidToken(securityRole, (isFront) ? SecurityUserInvalidTokenEnum.JUST_FRONT : SecurityUserInvalidTokenEnum.JUST_BACK, SecurityTokenInvalidTypeEnum.SECURITY_USER_UPDATE);
            }
        }

        //ویرایش دسترسی های یک نقش
        securityRole.getPermissionSet().clear();
        //ست کردن دسترسی
        if (!CollectionUtils.isEmpty(dto.getSecurityPermissionSet())) {
            if (Boolean.TRUE.equals(isFront)) {
                securityRole.setPermissionSet(dto.getSecurityPermissionSet().stream().map(securityPermissionService::serviceReadByIdForFront).collect(Collectors.toSet()));
            } else {
                securityRole.setPermissionSet(dto.getSecurityPermissionSet().stream().map(securityPermissionService::serviceReadByIdForBack).collect(Collectors.toSet()));
            }
        }
        securityRoleRepository.save(securityRole);
        return new SecurityRoleResponseDto(dto.getId());
    }

    /**
     * متد فعال یا غیرقعال کردن نقش کاربری
     *
     * @param invalid فعال/ غیرفعال
     * @param ids     رشته شناسه نقش کاربری بصورت csv
     * @param isFront آیا نقش کاریری برای فرانت است؟
     */
    @Override
    public void invalid(@NotNull Boolean invalid, @NotNull String ids, @NotNull Boolean isFront) {
        //تبدیل csv آیدیها به لیست
        Set<Long> idSet = Stream.of(ids.split(",")).map(Long::parseLong).collect(Collectors.toSet());

        //جستجوی نقش با شناسه
        List<SecurityRole> securityRoleList = securityRoleRepository.findByIdIn(idSet, null);
        // فعال یا غیرفعال شدن
        securityRoleList.forEach(securityRole -> {
            //اگر غیرفعال میشود
            if (invalid) {
                //در صورتی که هیچ کاربری به نقش متصل نبود می توان آن را غیر فعال کرد
                if (!CollectionUtils.isEmpty(securityRole.getSecurityUserSet()))
                    throw new BackUserException(securityRole.getTitle(), BUSINESS_EXCEPTION_SECURITY_ROLE_HAS_DEPENDENCY + "::" + securityRole.getTitle(), "");

                //غیرفعال کردن توکن با توجه به نوع فیلد isFront
                //this.serviceInvalidToken(securityRole, (isFront) ? SecurityUserInvalidTokenEnum.JUST_FRONT : SecurityUserInvalidTokenEnum.JUST_BACK, SecurityTokenInvalidTypeEnum.SECURITY_ROLE_INVALID);
            }
            securityRole.setInvalid(invalid);
            securityRoleRepository.save(securityRole);
        });

    }

    /**
     * متد حذف نقش کاربری
     *
     * @param ids رشته شناسه کاربران بک بصورت csv
     */
    @Override
    public void delete(@NotNull String ids) {
//        //جستجو با آیدی
//        SecurityRole securityRole = this.serviceReadById(id);
//        if ((org.apache.commons.collections4.CollectionUtils.isNotEmpty(securityRole.getSecurityUserSet())) || (org.apache.commons.collections4.CollectionUtils.isNotEmpty((securityRole.getSecurityUserSet())))) {
//            throw new SecurityRoleException("", BUSINESS_EXCEPTION_SECURITY_ROLE_HAS_DEPENDENCY, "");
//        }
//        securityRoleRepository.delete(securityRole);

        //تبدیل csv آیدیها به لیست
        Set<Long> idSet = Stream.of(ids.split(",")).map(Long::parseLong).collect(Collectors.toSet());

        //جستجوی نقش با شناسه
        List<SecurityRole> securityRoleList = securityRoleRepository.findByIdIn(idSet, null);
        // فعال یا غیرفعال شدن
        securityRoleList.forEach(securityRole -> {
            //شرط لازم برای حذف نقش ، غیرفعال بودن نقش است
            if (!securityRole.getInvalid())
                throw new BackUserException("", BUSINESS_EXCEPTION_SECURITY_ROLE_MUST_BE_INVALID, "");
            securityRoleRepository.delete(securityRole);
        });
    }


    /**
     * متد غیرفعال کردن توکن کاربرانی که نقش کاربری موردنظر را دارند
     *
     * @param securityRole                 انتیتی نقش کاربری
     * @param securityUserInvalidTokenEnum کدام توکن کاربر باید غیرفعال شوند؟ توکن های بک کاربر یا توکن های فرانت کاربر یا هردو
     * @param securityTokenInvalidTypeEnum دلیل غیرفعال شدن توکن
     */
    private void serviceInvalidToken(@NotNull SecurityRole securityRole, SecurityUserInvalidTokenEnum securityUserInvalidTokenEnum, SecurityTokenInvalidTypeEnum securityTokenInvalidTypeEnum) {
        Set<String> usernames = securityRole.getSecurityUserSet().stream().map(SecurityUser::getUsername).collect(Collectors.toSet());
        securityUserTokenService.serviceInvalid(usernames, securityTokenInvalidTypeEnum, securityUserInvalidTokenEnum);
    }

    //-------------------------------------------------------------
    //این یک متد تستی است و برای جستجو پیشرفته میباشد. در حال حاضر در پروژه استفاده نمیشود
    //-------------------------------------------------------------
    @Override
    @NotNull
    public SearchDataDto readGrid(@NotNull SearchFilterDto searchFilterDto, @NotNull Class searchViewTypeInterface, @NotNull List<Object> searchValueList) {
        //تعریف فیلترهای جستجو
        SecurityUserSpecification securityUserSpecification = (SecurityUserSpecification) SearchTools.makeSpecificationFromSearchFilter(searchFilterDto, new SecurityUserSpecification());
        //جستجو بر طبق فیلترهای جستجو و کلاس اینترفیس نوع نمایش و صفحه بندی دلخواه کلاینت ساید
        Page<SecurityRoleSearchViewTypeBrief> viewPage = securityRoleRepository.findAll(securityUserSpecification, searchViewTypeInterface, SearchTools.makePageableFromSearchFilter(searchFilterDto));
        //تعریف خروجی بر اساس جستجو
        SearchDataDto searchDataDto = SearchTools.buildSearchDataDto(viewPage, searchFilterDto, SecurityRoleSearchViewTypeBrief.class, null);
        return searchDataDto;

    }

}
