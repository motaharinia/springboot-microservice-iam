package com.motaharinia.ms.iam.modules.geo.persistence.orm.repository;

import com.motaharinia.ms.iam.modules.geo.persistence.orm.GeoEparchy;
import com.motaharinia.ms.iam.modules.geo.persistence.orm.GeoProvince;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GeoEparchyRepository extends JpaRepository<GeoEparchy,Long> {
    List<GeoEparchy> findAllByGeoProvince(GeoProvince geoProvince);
    Optional<GeoEparchy> findFirstByTitle(String title);
}
