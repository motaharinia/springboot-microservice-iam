package com.motaharinia.ms.iam.modules.securityuser.persistence.orm;

import com.motaharinia.msjpautility.entity.CustomEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;


/**
 * @author eng.motahari@gmail.com<br>
 * کلاس انتیتی نقش امنیت
 */
@Entity
@Table(name = "security_role")
@Data
@EqualsAndHashCode(callSuper = true)
public class SecurityRole extends CustomEntity implements Serializable {
    /**
     * شناسه
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * عنوان نقش
     */
    @Column(name = "title")
    private String title;

    /**
     *آیا نقش امنیت برای فرانت است؟
     */
    @Column(name = "is_front", nullable = false)
    private Boolean isFront;


    /**
     * لیست دسترسی ها
     */
    @JoinTable(name = "security_role_jt_security_permission", joinColumns = {
            @JoinColumn(name = "security_role_id", referencedColumnName = "id")}, inverseJoinColumns = {
            @JoinColumn(name = "security_permission_id", referencedColumnName = "id")})
    @ManyToMany(fetch = FetchType.LAZY)
    private Set<SecurityPermission> permissionSet = new HashSet<>();

    /**
     * سمت ضعیف ارتباط با کاربر امنیت
     */
    @ManyToMany(mappedBy = "securityRoleSet" , fetch = FetchType.LAZY)
    private Set<SecurityUser> securityUserSet = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SecurityRole)) return false;
        if (!super.equals(o)) return false;
        SecurityRole that = (SecurityRole) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), id);
    }
}
