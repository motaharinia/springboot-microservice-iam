package com.motaharinia.ms.iam.modules.securityclient.persistence.orm;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import th.co.geniustree.springdata.jpa.repository.JpaSpecificationExecutorWithProjection;

import java.util.Optional;


/**
 * @author eng.motahari@gmail.com<br>
 * کلاس ریپازیتوری کاربر امنیت
 */

@Repository
public interface SecurityClientRepository extends JpaRepository<SecurityClient, Long>, JpaSpecificationExecutorWithProjection<SecurityClient> {
    Optional<SecurityClient> findByClientId(@NotNull String clientId);

}
