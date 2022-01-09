package com.motaharinia.ms.iam.modules.securityuser.persistence.orm;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import th.co.geniustree.springdata.jpa.repository.JpaSpecificationExecutorWithProjection;

import java.util.List;
import java.util.Optional;

@Repository
public interface SecurityPermissionRepository extends JpaRepository<SecurityPermission, Long>, JpaSpecificationExecutorWithProjection<SecurityPermission> {

    Optional<SecurityPermission> findById(@NotNull Long id);
    Optional<SecurityPermission> findByIdAndIsFrontIsTrue(@NotNull Long Id);
    Optional<SecurityPermission> findByIdAndIsFrontIsFalse(@NotNull Long Id);
    Optional<SecurityPermission> findByAuthorityAndIsFrontIsTrue(@NotNull String authority);
    Optional<SecurityPermission> findByAuthorityAndIsFrontIsFalse(@NotNull String authority);

    //---------------------------parent for front
    @Query("select sp from SecurityPermission sp where sp.parent.id is null and sp.isFront=true ORDER BY sp.menuOrder ASC")
    List<SecurityPermission> findAllByParentIdIsNullAndIsFrontIsTrueOrderByMenuOrder();

    @Query("select sp from SecurityPermission sp where sp.parent.id = ?1 and sp.isFront=true ORDER BY sp.menuOrder ASC")
    List<SecurityPermission> findAllByParentIdAndIsFrontIsTrueOrderByMenuOrder(@NotNull Long id);

    //---------------------------parent for back
    @Query("select sp from SecurityPermission sp where sp.parent.id is null and sp.isFront=false ORDER BY sp.menuOrder ASC")
    List<SecurityPermission> findAllByParentIdIsNullAndIsFrontIsFalseOrderByMenuOrder();

    @Query("select sp from SecurityPermission sp where sp.parent.id = ?1 and sp.isFront=false ORDER BY sp.menuOrder ASC")
    List<SecurityPermission> findAllByParentIdAndIsFrontIsFalseOrderByMenuOrder(@NotNull Long id);


    //----------------------------sample of manytomany
    @Query("select sp from SecurityPermission sp join sp.securityRoleSet sr where sr.id = :securityRoleId ORDER BY sp.menuOrder ASC")
    List<SecurityPermission> findBySecurityRoleId(@NotNull @Param("securityRoleId") Long securityRoleId);


}
