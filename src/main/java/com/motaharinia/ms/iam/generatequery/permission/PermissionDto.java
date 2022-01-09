package com.motaharinia.ms.iam.generatequery.permission;
import com.motaharinia.ms.iam.modules.securityuser.business.enumeration.PermissionTypeEnum;
import com.motaharinia.msutility.custom.customvalidation.required.Required;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PermissionDto {
    /**
     *آیا دسترسی امنیت برای فرانت است؟
     */
    @Required
    private Boolean isFront;
    /**
     *نوع
     */
    @Required
    private PermissionTypeEnum typeEnum;
    /**
     * نام دسترسی
     */
    @Required
    public String authority;
    /**
     *عنوان فارسی
     */
    @Required
    public String title;

}
