package com.motaharinia.ms.iam.modules.securityuser.persistence.orm;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import th.co.geniustree.springdata.jpa.repository.JpaSpecificationExecutorWithProjection;

import java.util.List;
import java.util.Optional;
import java.util.Set;


/**
 * @author eng.motahari@gmail.com<br>
 * کلاس ریپازیتوری کاربر امنیت
 */

@Repository
public interface SecurityRoleRepository extends JpaRepository<SecurityRole, Long>, JpaSpecificationExecutorWithProjection<SecurityRole> {
    Optional<SecurityRole> findByTitleAndIsFrontIsTrue(@NotNull String title);
    Optional<SecurityRole> findByTitleAndIsFrontIsFalse(@NotNull String title);
    Optional<SecurityRole> findById(@NotNull Long Id);
    Optional<SecurityRole> findByIdAndIsFrontIsTrue(@NotNull Long Id);
    Optional<SecurityRole> findByIdAndIsFrontIsFalse(@NotNull Long Id);
    Page<SecurityRole> findAll(@NotNull Pageable pageable);
    List<SecurityRole> findAll();
    Page<SecurityRole> findAllByTitleContaining(@NotNull String title,Pageable pageable);
    Page<SecurityRole> findAllByInvalid(@NotNull Boolean invalid,Pageable pageable);
    List<SecurityRole> findByIdIn(@NotNull Set<Long> ids , Pageable pageable);

//    //----------------------------sample of manytomany
//    @Query("select sr.id from SecurityRole sr join sr.securityUserSet su where su.id = :securityId And sr.isFront = false ")
//    List<Long> findBySecurityUserSetIdAndIsFrontIsFalse(@NotNull @Param("securityId") Long securityId);

}
