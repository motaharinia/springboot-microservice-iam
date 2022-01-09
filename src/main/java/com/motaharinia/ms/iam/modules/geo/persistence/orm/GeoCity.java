package com.motaharinia.ms.iam.modules.geo.persistence.orm;

import com.motaharinia.msjpautility.entity.CustomEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.io.Serializable;

/**
 * کلاس انتیتی شهر
 */
@Entity
@Table(name = "geo_city")
@Data
@EqualsAndHashCode(callSuper = true)
public class GeoCity extends CustomEntity implements Serializable {

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
     *شهرستان
     */
    @JoinColumn(name= "geo_eparchy_id", referencedColumnName ="id")
    @ManyToOne(fetch=FetchType.LAZY)
    private GeoEparchy geoEparchy;
}
