package com.motaharinia.ms.iam.modules.geo.persistence.orm;

import com.motaharinia.msjpautility.entity.CustomEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.io.Serializable;

/**
 * کلاس انتیتی استان
 */
@Entity
@Table(name = "geo_province", uniqueConstraints = {@UniqueConstraint(columnNames = {"title"})})
@Data
@EqualsAndHashCode(callSuper = true)
public class GeoProvince extends CustomEntity implements Serializable {

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
     *پیش کد تلفن استان
     */
    @Column(name = "phone_code")
    private String phoneCode;

    /**
     *کشور
     */
    @JoinColumn(name= "geo_country_id", referencedColumnName ="id")
    @ManyToOne(fetch=FetchType.LAZY)
    private GeoCountry geoCountry;
}
