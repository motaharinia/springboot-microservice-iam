package com.motaharinia.ms.iam.modules.appuser.presentation.backcall;

import com.motaharinia.ms.iam.modules.appuser.business.service.AppUserService;
import com.motaharinia.ms.iam.modules.appuser.presentation.dto.AppUserAnnualPointDto;
import com.motaharinia.ms.iam.modules.appuser.presentation.dto.AppUserTotalCountResponseDto;
import com.motaharinia.ms.iam.modules.appuser.presentation.dto.AppUserValidReadDto;
import com.motaharinia.msutility.custom.customdto.ClientResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

/**
 * @author eng.motahari@gmail.com<br>
 * کلاس کنترلر کاربر برنامه فرانت
 */
@RestController
@RequestMapping("/api/v1.0/back/app-user")
public class AppUserBackController {

    private final AppUserService appUserService;

    /**
     * پیام موفقیت فرم
     */
    private static final String FORM_SUBMIT_SUCCESS = "USER_MESSAGE.FORM_SUBMIT_SUCCESS";

    @Autowired
    public AppUserBackController(AppUserService appUserService) {
        this.appUserService = appUserService;
    }

    /**
     * متد جستجو با شناسه کاربری
     *
     * @param id شناسه
     * @return خروجی: مدل جستجو شده AppUserReadResponseDto
     * call in ( userpanel(IamExternalCallService) )
     */
    @GetMapping("/{id}")
    public ClientResponseDto<AppUserValidReadDto> readById(@PathVariable Long id) {
        return new ClientResponseDto<>((appUserService.readById1(id)), FORM_SUBMIT_SUCCESS);
    }

    /**
     * متد گرفتن تعداد کل کاربرهای برنامه
     *
     * @return AppUserTotalCountResDto خروجی: مدل جستجو شده
     * call in ( userpanel(IamExternalCallService) )
     */
    @GetMapping("/total-count")
    public ClientResponseDto<AppUserTotalCountResponseDto> readAllUsersCount() {
        return new ClientResponseDto<>(appUserService.readTotalCount(), FORM_SUBMIT_SUCCESS);
    }

    /**
     * متد جستجو با لیست شناسه ملی
     *
     * @param idSet لیست شناسه ملی
     * @return Set<AppUserValidReadDto> خروجی: لیست مدل جستجو شده
     * call in (pointtracker(IamExternalCallServiceImpl) )
     */
    @PostMapping("/read-by-ids")
    public ClientResponseDto<Set<AppUserValidReadDto>> readByIds(@RequestBody @NotNull Set<Long> idSet) {
        return new ClientResponseDto<>((appUserService.readByIds(idSet)), FORM_SUBMIT_SUCCESS);
    }

    /**
     * متد گرفتن اشخاصی که در روز و ماه جاری تولدشان هست
     * @return Set<AppUserAnnualPointDto> خروجی: لیست مدل جستجو شده
     * call in (pointtracker(IamExternalCallServiceImpl) )
     */
    @GetMapping("/read-all-by-date-of-birth")
    public ClientResponseDto<Set<AppUserAnnualPointDto>> readAllByDateOfBirth() {
        return new ClientResponseDto<>(appUserService.readAllByDateOfBirth(), FORM_SUBMIT_SUCCESS);
    }

    /**
     * متد گرفتن اشخاصی که در روز و ماه جاری ثبت نام کرده اند
     * @return Set<AppUserAnnualPointDto> خروجی: لیست مدل جستجو شده
     * call in (pointtracker(IamExternalCallServiceImpl) )
     */
    @Operation(security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/read-all-by-date-of-signup")
    public ClientResponseDto<Set<AppUserAnnualPointDto>> readAllByDateOfSignUp() {
        return new ClientResponseDto<>(appUserService.readAllByDateOfSignUp(), FORM_SUBMIT_SUCCESS);
    }

    /**
     * متد جستجو با شناسه ملی
     *
     * @param nationalCode شناسه ملی
     * @return خروجی: مدل جستجو شده AppUserReadResponseDto
     * call in ( userpanel(IamExternalCallService) )
     */
    @GetMapping("/read-by-national-code/{nationalCode}")
    public ClientResponseDto<AppUserValidReadDto> readByNationalCode(@PathVariable String nationalCode) {
        //خواندن اولین شناسه ملی از لیست
        return new ClientResponseDto<>((appUserService.readByNationalCodes(Set.of(nationalCode)).stream().findFirst().orElse(null)), FORM_SUBMIT_SUCCESS);
    }

    /**
     * متد جستجو باشماره موبایل
     *
     * @param mobileNoSet لیست شماره موبایل
     * @return خروجی: مدل جستجو شده AppUserReadResponseDto
     * call in ( pointtracker(IamExternalCallService) )
     */
    @PostMapping("/read-by-mobile-nos")
    public ClientResponseDto<Set<AppUserValidReadDto>> readByMobileNos(@RequestBody @NotNull Set<String> mobileNoSet) {
        //خواندن اولین شناسه ملی از لیست
        return new ClientResponseDto<>((appUserService.readByMobileNos(mobileNoSet)), FORM_SUBMIT_SUCCESS);
    }
}
