package com.motaharinia.ms.iam.modules.appuserinvitationlog.orm;

import com.motaharinia.ms.iam.modules.appuser.persistence.orm.AppUser;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import th.co.geniustree.springdata.jpa.repository.JpaSpecificationExecutorWithProjection;

import java.util.Optional;


/**
 * کلاس ریپازیتوری ثبت لاگ کد معرف کاربر برنامه فرانت
 */

@Repository
public interface AppUserInvitationLogRepository extends JpaRepository<AppUserInvitationLog, Long>, JpaSpecificationExecutorWithProjection<AppUser> {

    @Query("select  count (appuil.id) from AppUserInvitationLog appuil where appuil.appUser.id = :appUserId ")
    Optional<Integer> readIdByAppUserId(@NotNull @Param("appUserId") Long appUserId );
}
