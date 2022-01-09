package com.motaharinia.ms.iam.modules.securityclient.persistence.orm;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import th.co.geniustree.springdata.jpa.repository.JpaSpecificationExecutorWithProjection;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


/**
 * کلاس ریپازیتوری توکن امنیت
 */

@Repository
public interface SecurityClientTokenRepository extends JpaRepository<SecurityClientToken, Long>, JpaSpecificationExecutorWithProjection<SecurityClientToken> {

    //renew
    Optional<SecurityClientToken> findByRefreshTokenAndInvalidIsFalseAndRefreshTokenExpiredAtIsGreaterThanEqual(@NotNull String refreshToken, @NotNull LocalDateTime nowLocalDateTime);

    //read all
    Page<SecurityClientToken> findAllByUsernameAndCreateAtBetween(@NotNull String username, @NotNull LocalDateTime fromDate, @NotNull LocalDateTime toDate, Pageable pageable);


    //اسکجل
    Optional<List<SecurityClientToken>> findByInvalidIsFalseAndRefreshTokenExpiredAtIsLessThan(@NotNull LocalDateTime nowLocalDateTime);

}
