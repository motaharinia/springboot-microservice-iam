package com.motaharinia.ms.iam.modules.geo.Business.service;

import com.motaharinia.ms.iam.modules.geo.persistence.orm.GeoCity;
import com.motaharinia.ms.iam.modules.geo.presentation.GeoCityReadDto;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface GeoService {

    /**
     * مشاهده تمامی شهرها
     * @return List<GeoCityReadDto> لیست مدل اطلاعات شهرها
     */
    List<GeoCityReadDto> readAllGeoCities();
    /**
     * متد جستجوی شهر با شناسه
     * @param id شناسه
     * @return GeoCity انتیتی شهر
     */
    GeoCity serviceReadById(@NotNull Long id);
//    List<GeoCountry> getAllGeoCountries();
//    List<GeoCity> getAllGeoCitiesByEparchy(String eparchyName);
//    List<GeoProvince> getAllGeoProvinces();
//    List<GeoProvince> getAllGeoProvincesByCountry(String countryName);
//    List<GeoEparchy> getAllGeoEparchies();
//    List<GeoEparchy> getAllGeoEparchiesByProvince(String provinceName);
//
}
