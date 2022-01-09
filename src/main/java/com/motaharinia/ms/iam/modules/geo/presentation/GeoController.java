package com.motaharinia.ms.iam.modules.geo.presentation;

import com.motaharinia.ms.iam.modules.geo.Business.service.GeoService;
import com.motaharinia.msutility.custom.customdto.ClientResponseDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1.0/geo")
public class GeoController {

    private static final String FORM_SUBMIT_SUCCESS = "USER_MESSAGE.FORM_SUBMIT_SUCCESS";

    private final GeoService geoService;

    public GeoController(GeoService geoService) {
        this.geoService = geoService;
    }

    /**
     * مشاهده تمامی شهرها
     * @return List<GeoCityReadDto> لیست مدل اطلاعات شهرها
     */
    @GetMapping("/city/read-all")
    public ClientResponseDto<List<GeoCityReadDto>> readAllGeoCities(){
        return new ClientResponseDto<>(geoService.readAllGeoCities(),FORM_SUBMIT_SUCCESS);
    }
//    @GetMapping("/country")
//    public ClientResponseDto<List<GeoCountry>> getAllGeoCountry(){
//        return new ClientResponseDto<>(geoService.getAllGeoCountries(),FORM_SUBMIT_SUCCESS);
//    }
//    @GetMapping("/province")
//    public ClientResponseDto<List<GeoProvince>> getAllGeoProvince(){
//        return new ClientResponseDto<>(geoService.getAllGeoProvinces(),FORM_SUBMIT_SUCCESS);
//    }
//    @GetMapping("/province/{countryName}")
//    public ClientResponseDto<List<GeoProvince>> getAllGeoProvincesByCountry(@PathVariable(name = "countryName") String countryName){
//        return new ClientResponseDto<>(geoService.getAllGeoProvincesByCountry(countryName),FORM_SUBMIT_SUCCESS);
//    }
//    @GetMapping("/eparchy")
//    public ClientResponseDto<List<GeoEparchy>> getAllGeoEparchy(){
//        return new ClientResponseDto<>(geoService.getAllGeoEparchies(),FORM_SUBMIT_SUCCESS);
//    }
//    @GetMapping("/eparchy/{provinceName}")
//    public ClientResponseDto<List<GeoEparchy>> getAllGeoEparchiesByProvince(@PathVariable(name = "provinceName") String provinceName){
//        return new ClientResponseDto<>(geoService.getAllGeoEparchiesByProvince(provinceName),FORM_SUBMIT_SUCCESS);
//    }
//    @GetMapping("/city/{eparchyName}")
//    public ClientResponseDto<List<GeoCity>> getAllGeoCitiesByEparchy(@PathVariable(name = "eparchyName") String eparchyName){
//        return new ClientResponseDto<>(geoService.getAllGeoCitiesByEparchy(eparchyName),FORM_SUBMIT_SUCCESS);
//    }
}
