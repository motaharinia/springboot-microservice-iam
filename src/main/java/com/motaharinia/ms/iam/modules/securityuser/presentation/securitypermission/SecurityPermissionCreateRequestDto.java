package com.motaharinia.ms.iam.modules.securityuser.presentation.securitypermission;

import com.motaharinia.ms.iam.modules.securityuser.business.enumeration.PermissionTypeEnum;
import com.motaharinia.msutility.custom.customvalidation.required.Required;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SecurityPermissionCreateRequestDto {
    /**
     * عنوان دسترسی
     */
    private String title;
    /**
     * نام دسترسی
     */
    @Required
    private String authority;
    /**
     *یوآرال صفحه موردنظر
     */
    private String menuLink;
    /**
     *ترتیب نمایش فرزندان هر پرنت در منو
     */
    @Required
    private Integer menuOrder;
    /**
     *نوع
     */
    @Required
    private PermissionTypeEnum typeEnum;
    /**
     * آیدی دسترسی والد-پرنت
     */
    private Long parentId;
    /**
     *آیا دسترسی امنیت برای فرانت است؟
     */
    @Required
    private Boolean isFront;
}
