package com.motaharinia.ms.iam.modules.backuser.persistence.orm;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import th.co.geniustree.springdata.jpa.repository.JpaSpecificationExecutorWithProjection;

import java.util.List;
import java.util.Optional;
import java.util.Set;


/**
 * @author Maryam
 * کلاس ریپازیتوری کاربر بک
 */

@Repository
public interface BackUserRepository extends JpaRepository<BackUser, Long>, JpaSpecificationExecutorWithProjection<BackUser> {

    Optional<BackUser> findById(Long securityUserId);

    Optional<BackUser> findByNationalCode(String nationalCode);

    List<BackUser> findByIdIn(@NotNull Set<Long> ids , Pageable pageable);

    Page<BackUser> findAllByMobileNoContaining(@NotNull String mobileNo, Pageable pageable);

    Page<BackUser> findAllByLastNameContaining(String lastname, Pageable pageable);

    Page<BackUser> findAllByNationalCodeContaining(@NotNull String nationalCode, Pageable pageable);

    Page<BackUser> findAll(Pageable pageable);

    List<BackUser> findAll();

    List<BackUser> findByNationalCodeInAndInvalidIsFalse(@NotNull Set<String> nationalCodeSet);

    //جستجو آیدی با کدملی
    @Query("select  bu.id from BackUser bu where bu.nationalCode = :nationalCode ")
    Long readIdByNationalCode(@NotNull @Param("nationalCode") String nationalCode);

    @Query("select  bu.invalid,bu.hidden from BackUser bu where bu.id = :id ")
    List<Object[]> readInvalidById(@NotNull @Param("id") Long id);

}
