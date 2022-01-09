package com.motaharinia.ms.iam.modules.geo.persistence.orm.repository;


import com.motaharinia.ms.iam.modules.geo.persistence.orm.GeoCountry;
import com.motaharinia.ms.iam.modules.geo.persistence.orm.GeoProvince;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GeoProvinceRepository extends JpaRepository<GeoProvince,Long> {
    List<GeoProvince> findAllByGeoCountry(GeoCountry geoCountry);
    Optional<GeoProvince> findFirstByTitle(String title);
}
