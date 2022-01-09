package com.motaharinia.ms.iam.modules.geo.Business.service;

import com.motaharinia.ms.iam.modules.geo.Business.exception.GeoException;
import com.motaharinia.ms.iam.modules.geo.Business.mapper.GeoMapper;
import com.motaharinia.ms.iam.modules.geo.persistence.orm.GeoCity;
import com.motaharinia.ms.iam.modules.geo.persistence.orm.repository.GeoCityRepository;
import com.motaharinia.ms.iam.modules.geo.persistence.orm.repository.GeoCountryRepository;
import com.motaharinia.ms.iam.modules.geo.persistence.orm.repository.GeoEparchyRepository;
import com.motaharinia.ms.iam.modules.geo.persistence.orm.repository.GeoProvinceRepository;
import com.motaharinia.ms.iam.modules.geo.presentation.GeoCityReadDto;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@Service
public class GeoServiceImpl implements GeoService {


    private static final String BUSINESS_EXCEPTION_NO_SUCH_EPORCHY = "BUSINESS_EXCEPTION.NO_SUCH_EPORCHY";
    private static final String BUSINESS_EXCEPTION_NO_SUCH_PROVINCE = "BUSINESS_EXCEPTION.NO_SUCH_PROVINCE";
    private static final String BUSINESS_EXCEPTION_NO_SUCH_COUNTRY = "BUSINESS_EXCEPTION.NO_SUCH_COUNTRY";
    private static final String BUSINESS_EXCEPTION_CITY_ID_NOT_FOUND = "BUSINESS_EXCEPTION.CITY_ID_NOT_FOUND";
    private final GeoCountryRepository geoCountryRepository;
    private final GeoProvinceRepository geoProvinceRepository;
    private final GeoEparchyRepository geoEparchyRepository;
    private final GeoCityRepository geoCityRepository;
    private final GeoMapper geoMapper;

    public GeoServiceImpl(GeoCityRepository geoCityRepository, GeoCountryRepository geoCountryRepository,
                          GeoProvinceRepository geoProvinceRepository, GeoEparchyRepository geoEparchyRepository, GeoMapper geoMapper) {
        this.geoCityRepository = geoCityRepository;
        this.geoCountryRepository = geoCountryRepository;
        this.geoProvinceRepository = geoProvinceRepository;
        this.geoEparchyRepository = geoEparchyRepository;
        this.geoMapper = geoMapper;
    }


    /**
     * مشاهده تمامی شهرها
     * @return List<GeoCityReadDto> لیست مدل اطلاعات شهرها
     */
    @Override
    public List<GeoCityReadDto> readAllGeoCities() {
        return geoCityRepository.findAll().stream().map(geoMapper::toDto).collect(Collectors.toList());
    }

    /**
     * متد جستجوی شهر با شناسه و در صورت پیدا نکردن شهر بیزینس اکسپشن برمیگردد
     *
     * @param id شناسه
     * @return GeoCity انتیتی شهر
     */
    @Override
    public GeoCity serviceReadById(@NotNull Long id) {
        return geoCityRepository.findById(id).orElseThrow(() -> new GeoException(id.toString(), BUSINESS_EXCEPTION_CITY_ID_NOT_FOUND, ""));
    }

//    @Override
//    public List<GeoCountry> getAllGeoCountries() {
//        return geoCountryRepository.findAll();
//    }
//    @Override
//    public List<GeoCity> getAllGeoCitiesByEparchy(String eparchyName) {
//        GeoEparchy geoEparchy = geoEparchyRepository.findFirstByTitle(eparchyName).orElseThrow(() ->
//                new GeoException(eparchyName, BUSINESS_EXCEPTION_NO_SUCH_EPORCHY, ""));
//        return geoCityRepository.findAllByGeoEparchy(geoEparchy);
//    }
//
//    @Override
//    public List<GeoProvince> getAllGeoProvinces() {
//        return geoProvinceRepository.findAll();
//
//    }
//
//    @Override
//    public List<GeoProvince> getAllGeoProvincesByCountry(String countryName) {
//        GeoCountry geoCountry = geoCountryRepository.findFirstByTitle(countryName).orElseThrow(() ->
//                new GeoException(countryName, BUSINESS_EXCEPTION_NO_SUCH_COUNTRY, ""));
//        return geoProvinceRepository.findAllByGeoCountry(geoCountry);
//    }
//
//    @Override
//    public List<GeoEparchy> getAllGeoEparchies() {
//        return geoEparchyRepository.findAll();
//    }
//
//    @Override
//    public List<GeoEparchy> getAllGeoEparchiesByProvince(String provinceName) {
//        GeoProvince geoProvince = geoProvinceRepository.findFirstByTitle(provinceName).orElseThrow(() ->
//                new GeoException(provinceName, BUSINESS_EXCEPTION_NO_SUCH_PROVINCE, ""));
//        return geoEparchyRepository.findAllByGeoProvince(geoProvince);
//    }
//

}
