package com.motaharinia.ms.iam.modules.appuserchangelog.orm;

import com.motaharinia.ms.iam.modules.appuser.persistence.orm.AppUser;
import com.motaharinia.ms.iam.modules.appuserchangelog.business.enumeration.AppUserChangeTypeEnum;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import th.co.geniustree.springdata.jpa.repository.JpaSpecificationExecutorWithProjection;

import java.util.Optional;


/**
 * کلاس ریپازیتوری ثبت لاگ  تغییرات اطلاعات کاربر برنامه فرانت
 */

@Repository
public interface AppUserChangeLogRepository extends JpaRepository<AppUserChangeLog, Long>, JpaSpecificationExecutorWithProjection<AppUser> {

    @Query("select  count (appucl.id) from AppUserChangeLog appucl where appucl.appUser.id = :appUserId AND appucl.changeTypeEnum = :changeTypeEnum")
    Optional<Integer> readIdByAppUserId(@NotNull @Param("appUserId") Long appUserId , @NotNull @Param("changeTypeEnum") AppUserChangeTypeEnum changeTypeEnum );
}
