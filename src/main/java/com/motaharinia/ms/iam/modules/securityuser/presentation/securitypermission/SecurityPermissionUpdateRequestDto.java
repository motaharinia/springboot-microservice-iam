package com.motaharinia.ms.iam.modules.securityuser.presentation.securitypermission;

import com.motaharinia.ms.iam.modules.securityuser.business.enumeration.PermissionTypeEnum;
import com.motaharinia.msutility.custom.customvalidation.required.Required;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SecurityPermissionUpdateRequestDto extends  SecurityPermissionCreateRequestDto{
    /**
     * شناسه
     */
    @Required
    private Long id;

    public SecurityPermissionUpdateRequestDto(Long id ,String title,String authority, String menuLink, Integer menuOrder, PermissionTypeEnum typeEnum, Long parentId, Boolean isFront) {
        super(title,authority, menuLink, menuOrder, typeEnum, parentId, isFront);
        this.id = id;
    }

}
