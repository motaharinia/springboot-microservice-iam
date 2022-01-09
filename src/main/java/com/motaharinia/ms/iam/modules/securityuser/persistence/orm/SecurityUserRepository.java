package com.motaharinia.ms.iam.modules.securityuser.persistence.orm;

import com.motaharinia.ms.iam.modules.securityuser.presentation.securityuser.SecurityUserReadDto;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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
public interface SecurityUserRepository extends JpaRepository<SecurityUser, Long>, JpaSpecificationExecutorWithProjection<SecurityUser> {
    Optional<SecurityUser> findByUsername(@NotNull String username);

    Optional<SecurityUser> findByUsernameAndAppUserIdNotNull(@NotNull String username);

    Optional<SecurityUser> findByUsernameAndBackUserIdNotNull(@NotNull String username);

    @Query("select  su.id from SecurityUser su where su.username = :username")
    Optional<Long> readByUsername(@NotNull @Param("username") String username);

    Optional<List<SecurityUser>> findAllBySecurityRoleSetEquals(@NotNull SecurityRole securityRole);

    Optional<SecurityUser> findByBackUserId(@NotNull Long backUserId);

    Optional<SecurityUser> findByAppUserId(@NotNull Long appUserId);

    @Query(value = "select new com.motaharinia.ms.iam.modules.securityuser.presentation.securityuser.SecurityUserReadDto(su.id,su.appUserId,su.backUserId,su.mobileNo) from SecurityUser su where su.username = :username and su.appUserId IS NOT null " )
    Optional<SecurityUserReadDto> findAppUserIdByUsername(@Param("username") String username);

    @Query(value = "select  new com.motaharinia.ms.iam.modules.securityuser.presentation.securityuser.SecurityUserReadDto(su.id,su.appUserId,su.backUserId,su.mobileNo) from SecurityUser su where su.username = :username and su.backUserId IS NOT null " )
    Optional<SecurityUserReadDto> findBackUserIdByUsername(@Param("username") String username);

    @Query("select  su.username from SecurityUser su where su.appUserId IN :appUserIdSet")
    List<String> findByAppUserIdSet(@NotNull Set<Long> appUserIdSet);

    @Query("select  su.username from SecurityUser su where su.backUserId IN :backUserIdSet")
    List<String> findByBackUserIdSet(@NotNull Set<Long> backUserIdSet);


}
