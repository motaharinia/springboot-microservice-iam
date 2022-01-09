package com.motaharinia.ms.iam.modules.geo.persistence.orm;

import com.motaharinia.msjpautility.entity.CustomEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.io.Serializable;

/**
 * کلاس انتیتی کشور
 */
@Entity
@Table(name = "geo_country", uniqueConstraints = {@UniqueConstraint(columnNames = {"title"})})
@Data
@EqualsAndHashCode(callSuper = true)
public class GeoCountry extends CustomEntity implements Serializable {

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
     *پیش کد تلفن کشور
     */
    @Column(name = "phone_code")
    private String phoneCode;
}
