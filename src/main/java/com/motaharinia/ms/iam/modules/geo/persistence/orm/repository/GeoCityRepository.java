package com.motaharinia.ms.iam.modules.geo.persistence.orm.repository;

import com.motaharinia.ms.iam.modules.geo.persistence.orm.GeoCity;
import com.motaharinia.ms.iam.modules.geo.persistence.orm.GeoEparchy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GeoCityRepository extends JpaRepository<GeoCity,Long> {
    List<GeoCity> findAllByGeoEparchy(GeoEparchy geoEparchy);
    Optional<GeoCity> findFirstByTitle(String title);
}
