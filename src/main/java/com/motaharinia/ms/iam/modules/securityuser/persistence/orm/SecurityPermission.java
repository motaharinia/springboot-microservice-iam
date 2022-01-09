package com.motaharinia.ms.iam.modules.securityuser.persistence.orm;

import com.motaharinia.ms.iam.modules.securityuser.business.enumeration.PermissionTypeEnum;
import com.motaharinia.msjpautility.entity.CustomEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;


/**
 * @author eng.motahari@gmail.com<br>
 * کلاس انتیتی دسترسی امنیت
 */

@Entity
@Table(name = "security_permission")
@Getter
@Setter
//@EqualsAndHashCode(callSuper = true)
public class SecurityPermission extends CustomEntity implements Serializable {
    /**
     * شناسه
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * عنوان دسترسی
     */
    @Column(name = "title")
    private String title;

    /**
     * نام دسترسی
     */
    @Column(name = "authority")
    private String authority;

    /**
     *یوآرال صفحه موردنظر
     */
    @Column(name = "menu_link")
    private String menuLink;

    /**
     *ترتیب نمایش فرزندان هر پرنت در منو
     */
    @Column(name = "menu_order")
    private Integer menuOrder;

    /**
     *نوع
     */
    @Column(name = "type_enum")
    @Enumerated(EnumType.STRING)
    private PermissionTypeEnum typeEnum;

    /**
     *آیا دسترسی امنیت برای فرانت است؟
     */
    @Column(name = "is_front", nullable = false)
    private Boolean isFront;

    /**
     * دسترسی والد-پرنت
     */
    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "parent_id")
    private SecurityPermission parent;

    /**
     * سمت ضعیف ارتباط با کاربر امنیت
     */
    @ManyToMany(mappedBy = "specialPermissionIncludeSet" , fetch = FetchType.LAZY)
    private Set<SecurityUser> securityUserIncludeSet = new HashSet<>();

    /**
     * سمت ضعیف ارتباط با کاربر امنیت
     */
    @ManyToMany(mappedBy = "specialPermissionExcludeSet" , fetch = FetchType.LAZY)
    private Set<SecurityUser> securityUserExcludeSet = new HashSet<>();

    /**
     * سمت ضعیف ارتباط با نقش کاربری
     */
    @ManyToMany(mappedBy = "permissionSet" , fetch = FetchType.LAZY)
    private Set<SecurityRole> securityRoleSet = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SecurityPermission)) return false;
        if (!super.equals(o)) return false;
        SecurityPermission that = (SecurityPermission) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), id);
    }
}
