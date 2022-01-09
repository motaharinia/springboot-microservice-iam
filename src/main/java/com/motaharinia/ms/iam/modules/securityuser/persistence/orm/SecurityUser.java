package com.motaharinia.ms.iam.modules.securityuser.persistence.orm;

import com.motaharinia.msjpautility.entity.CustomEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;


/**
 * @author eng.motahari@gmail.com<br>
 * کلاس انتیتی کاربر امنیت
 */
@Entity
@Table(name = "security_user")
@Data
@EqualsAndHashCode(callSuper = true)
public class SecurityUser extends CustomEntity implements Serializable, UserDetails {
    /**
     * شناسه
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    /**
     * کلمه کاربری
     */
    @Column(name = "username")
    private String username;
    /**
     * رمز عبور
     */
    @Column(name = "password")
    private String password;

    /**
     * شماره تلفن همراه جهت بازیابی رمز عبور
     */
    @Column(name = "mobile_no", length = 11)
    private String mobileNo;

    /**
     * نشانی پست الکترونیک جهت بازیابی رمز عبور
     */
    @Column(name = "email_address")
    private String emailAddress;

    /**
     *آیدی کاربر برنامه فرانت
     */
    @Column(name = "app_user_id")
    private Long appUserId;

    /**
     *آیدی کاربر برنامه بک
     */
    @Column(name = "back_user_id")
    private Long backUserId;

    /**
     * حساب کاربری منقضی شده است؟
     */
    @Column(name = "account_expired")
    private Boolean accountExpired = false;
    /**
     * حساب کاربری قفل شده است؟
     */
    @Column(name = "account_locked")
    private Boolean accountLocked = false;
    /**
     * اطلاعات لاگین منقضی شده است؟
     */
    @Column(name = "credential_expired")
    private Boolean credentialExpired = false;
    /**
     * کاربر فعال است؟
     */
    @Column(name = "enabled")
    private Boolean enabled = true;
    /**
     * مجموعه نقش های کاربری
     */
    @JoinTable(name = "security_user_jt_security_role", joinColumns = {
            @JoinColumn(name = "security_user_id", referencedColumnName = "id")}, inverseJoinColumns = {
            @JoinColumn(name = "security_role_id", referencedColumnName = "id")})
    @ManyToMany(fetch = FetchType.LAZY)
    private Set<SecurityRole> securityRoleSet = new HashSet<>();

    /**
     * لیست دسترسی های خاص اضافه شده
     * یعنی علاوه بر دسترسی هایی که در رول ها(نقش کاربری) یوزر وجود دارد، دسترسی های خاص دیگر هم بتوانیم به یوزر بدهیم
     */
    @JoinTable(name = "security_user_jt_security_permission_include", joinColumns = {
            @JoinColumn(name = "security_user_id", referencedColumnName = "id")}, inverseJoinColumns = {
            @JoinColumn(name = "security_permission_id", referencedColumnName = "id")})
    @ManyToMany(fetch = FetchType.LAZY)
    private Set<SecurityPermission> specialPermissionIncludeSet = new HashSet<>();


    /**
     * لیست دسترسی های خاص کم شده
     * یعنی علاوه بر دسترسی هایی که در رول ها(نقش کاربری) یوزر وجود دارد، دسترسی های خاص دیگر را بتوانیم از یوزر بگیریم
     */
    @JoinTable(name = "security_user_jt_security_permission_exclude", joinColumns = {
            @JoinColumn(name = "security_user_id", referencedColumnName = "id")}, inverseJoinColumns = {
            @JoinColumn(name = "security_permission_id", referencedColumnName = "id")})
    @ManyToMany(fetch = FetchType.LAZY)
    private Set<SecurityPermission> specialPermissionExcludeSet = new HashSet<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> grantedAuthoritySet = new HashSet<>();
        securityRoleSet.stream().forEach(role -> {
            grantedAuthoritySet.add(new SimpleGrantedAuthority(role.getTitle()));
            role.getPermissionSet().stream().forEach(permission -> grantedAuthoritySet.add(new SimpleGrantedAuthority(permission.getAuthority())));
        });
        return grantedAuthoritySet;
    }

    @Override
    public boolean isAccountNonExpired() {
        return !accountExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !accountLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return !credentialExpired;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

}
