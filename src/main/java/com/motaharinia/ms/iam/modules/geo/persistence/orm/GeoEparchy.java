package com.motaharinia.ms.iam.modules.geo.persistence.orm;

import com.motaharinia.msjpautility.entity.CustomEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.io.Serializable;

/**
 * کلاس انتیتی شهرستان
 */
@Entity
@Table(name = "geo_eparchy")
@Data
@EqualsAndHashCode(callSuper = true)
public class GeoEparchy extends CustomEntity implements Serializable {

    /**
     * شناسه
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     *عنوان
     */
    @Column(name = "title")
    private String title;

    /**
     *استان
     */
    @JoinColumn(name= "geo_province_id", referencedColumnName ="id")
    @ManyToOne(fetch=FetchType.LAZY)
    private GeoProvince geoProvince;
}
