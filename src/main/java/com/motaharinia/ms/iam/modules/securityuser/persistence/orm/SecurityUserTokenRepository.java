package com.motaharinia.ms.iam.modules.securityuser.persistence.orm;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import th.co.geniustree.springdata.jpa.repository.JpaSpecificationExecutorWithProjection;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;


/**
 * کلاس ریپازیتوری توکن امنیت
 */

@Repository
public interface SecurityUserTokenRepository extends JpaRepository<SecurityUserToken, Long>, JpaSpecificationExecutorWithProjection<SecurityUserToken> {

    //renew
    Optional<SecurityUserToken> findByRefreshTokenAndInvalidIsFalseAndRefreshTokenExpiredAtIsGreaterThanEqual(@NotNull String refreshToken, @NotNull LocalDateTime nowLocalDateTime);

    //read all
    Page<SecurityUserToken> findAllByUsernameAndCreateAtBetween(@NotNull String username, @NotNull LocalDateTime fromDate, @NotNull LocalDateTime toDate, Pageable pageable);

    //active session
    Page<SecurityUserToken> findByUsernameAndInvalidIsFalse(@NotNull String username, Pageable pageable);

    //terminate
    Optional<List<SecurityUserToken>> findByRefreshTokenAndUsernameAndInvalidIsFalse(@NotNull String refreshToken, @NotNull String username);

    //logout
    Optional<List<SecurityUserToken>> findByAccessToken(@NotNull String accessToken);

    //اسکجل
    Optional<List<SecurityUserToken>> findByInvalidIsFalseAndRefreshTokenExpiredAtIsLessThan(@NotNull LocalDateTime nowLocalDateTime);

    //جستجو با رشته accessToken
    @Query("select  st.id,st.rememberMe from SecurityUserToken st where st.accessToken = :accessToken ")
    Optional<List<Object[]>> readByAccessToken(@NotNull @Param("accessToken") String accessToken );

    //invalid by other modules
    List<SecurityUserToken> findByUsernameInAndInvalidIsFalse(@NotNull Set<String> usernameSet);
    List<SecurityUserToken> findByUsernameInAndInvalidIsFalseAndIsFrontIsTrue(@NotNull Set<String> usernameSet);
    List<SecurityUserToken> findByUsernameInAndInvalidIsFalseAndIsFrontIsFalse(@NotNull Set<String> usernameSet);
}
