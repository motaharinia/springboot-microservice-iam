package com.motaharinia.ms.iam.modules.securityuser.presentation.securitypermission;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * مدل مشاهده اطلاعات دسترسی ها
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SecurityPermissionReadResponseDto implements Serializable {
    /**
     * شناسه
     */
    private Long id;
    /**
     * عنوان دسترسی
     */
    private String title;
    /**
     * نام دسترسی
     */
    private String authority;
    /**
     *ترتیب نمایش فرزندان هر پرنت در منو
     */
    private String menuOrder;
    /**
     * آیدی دسترسی والد(پرنت)
     */
    private Long parentId;
//    /**
//     *یوآرال صفحه موردنظر
//     */
//    private String menuLink;
//    /**
//     *نوع
//     */
//    private PermissionTypeEnum typeEnum;
//
//    /**
//     *آیا دسترسی امنیت برای فرانت است؟
//     */
//    private Boolean isFront;
    /**
     * لیست فرزندان
     */
    List<SecurityPermissionReadResponseDto> childrenList = new ArrayList<>();

}
