package com.motaharinia.ms.iam.modules.geo.persistence.orm.repository;

import com.motaharinia.ms.iam.modules.geo.persistence.orm.GeoCountry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GeoCountryRepository extends JpaRepository<GeoCountry,Long> {
    Optional<GeoCountry> findFirstByTitle(String  title);
}
