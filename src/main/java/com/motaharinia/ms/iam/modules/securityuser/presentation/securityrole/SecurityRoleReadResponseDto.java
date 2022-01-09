package com.motaharinia.ms.iam.modules.securityuser.presentation.securityrole;

import com.motaharinia.ms.iam.modules.securityuser.presentation.securitypermission.SecurityPermissionReadResponseDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author eng.motahari@gmail.com<br>
 * کلاس مدل مشاهده نقش کاربری
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SecurityRoleReadResponseDto implements Serializable {

    /**
     * شناسه
     */
    private Long id;
    /**
     * نام نقش کاربری سکیوریتی
     */
    private String title;
    /**
     * فعال و غیرفعال
     */
    private Boolean invalid;
    /**
     * لیست دسترسی
     */
    List<SecurityPermissionReadResponseDto> securityPermissionReadResponseDtoSet =new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
