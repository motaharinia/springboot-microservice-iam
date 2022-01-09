package com.motaharinia.ms.iam.modules.securityuser.presentation.securityuser.signup;

import com.motaharinia.ms.iam.config.security.oauth2.dto.AppUserDto;
import com.motaharinia.ms.iam.modules.securityuser.presentation.securityuser.SecurityUserCreateRequestDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * مدل ثبت کاربر امنیت
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignupDto implements Serializable {
    /**
     * مدل ثبت کاربر امنیت
     */
    private SecurityUserCreateRequestDto securityUserCreateRequestDto;
    /**
     *  نقشهای کاربری
     */
    private Set<Long> securityRoleIdSet;
    /**
     *دسترسی های اضافه بر نقش های کاربری انتخاب شده
     */
    private Set<Long> securityPermissionIncludeIdSet = new HashSet<>();
    /**
     *  مدل کاربر برنامه فرانت
     */
    private AppUserDto appUserDto;
    /**
     * مرا به خاطر بسپار(در صورتی که مقدارش نال باشد نیاز به تولید توکن نمی باشد)
     */
    private Boolean rememberMe;

    /**
     * آیا توکن تولید شود؟
     */
    private Boolean generateBearerToken;

}
