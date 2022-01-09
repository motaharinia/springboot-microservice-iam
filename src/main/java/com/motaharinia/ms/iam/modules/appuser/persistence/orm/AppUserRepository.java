package com.motaharinia.ms.iam.modules.appuser.persistence.orm;

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
 * @author eng.motahari@gmail.com<br>
 * کلاس ریپازیتوری کاربر
 */

@Repository
public interface AppUserRepository extends JpaRepository<AppUser, Long>, JpaSpecificationExecutorWithProjection<AppUser> {
    Optional<AppUser> findByNationalCode(@NotNull String nationalCode);
    Optional<AppUser> findById(@NotNull Long id);
    Optional<AppUser> findByIdAndInvalidIsFalse(@NotNull Long id);
    Page<AppUser> findAll(@NotNull Pageable pageable);
    List<AppUser> findAll();
    List<AppUser> findByIdIn(@NotNull Set<Long> ids , Pageable pageable);
    List<AppUser> findByIdInAndInvalidIsFalse(@NotNull Set<Long> idSet);
    List<AppUser> findByNationalCodeInAndInvalidIsFalse(@NotNull Set<String> nationalCodeSet);
    List<AppUser> findByMobileNoInAndInvalidIsFalse(@NotNull Set<String> mobileNo);
    Page<AppUser> findAllByFirstNameContaining(@NotNull String firstName, Pageable pageable);
    Page<AppUser> findAllByLastNameContaining(@NotNull String lastName, Pageable pageable);
    Page<AppUser> findAllByNationalCodeContaining(@NotNull String nationalCode, Pageable pageable);

    @Query("select  appu.id from AppUser appu")
    List<Long> readAllId();

    @Query("select  appu.invalid,appu.hidden from AppUser appu where appu.id = :id ")
    List<Object[]> readInvalidById(@NotNull @Param("id") Long id );

    //جستجو آیدی با کدملی
    @Query("select  appu.id from AppUser appu where appu.nationalCode = :nationalCode ")
    Long readIdByNationalCode(@NotNull @Param("nationalCode") String nationalCode );

    //جستجو آیدی با موبایل
    @Query("select  appu.id from AppUser appu where appu.mobileNo = :mobileNo ")
    Long readIdByMobileNo(@NotNull @Param("mobileNo") String mobileNo );

    @Query(value = "SELECT * FROM app_user as au WHERE au.date_of_birth Like %?1%", nativeQuery = true)
    List<AppUser> readAllByDateOfBirth(@NotNull @Param ("eventDate") String eventDate);

    @Query(value = "SELECT * FROM app_user as au WHERE au.create_at Like %?1%", nativeQuery = true)
    List<AppUser> readAllByDateOfSignUp(@NotNull @Param ("eventDate") String eventDate);

    //جستجو آیدی با کدمعرف
    @Query("select  appu.id from AppUser appu where appu.invitationCode = :invitationCode ")
    Optional<Long> readIdByInvitationCode(@NotNull @Param("invitationCode") String invitationCode );
}
