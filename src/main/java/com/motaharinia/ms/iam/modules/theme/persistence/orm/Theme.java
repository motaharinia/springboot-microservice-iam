//package com.motaharinia.ms.iam.modules.theme.persistence.orm;
//
//import com.motaharinia.msjpautility.entity.CustomEntity;
//import GenderEnum;
//import lombok.EqualsAndHashCode;
//import lombok.Getter;
//import lombok.Setter;
//
//import javax.persistence.*;
//import javax.validation.constraints.NotNull;
//import java.io.Serializable;
//
///**
// * کلاس انتیتی تم
// */
//@Entity
//@Table(name = "theme", uniqueConstraints = {
//        @UniqueConstraint(columnNames = {"title"})})
//@Getter
//@Setter
//@EqualsAndHashCode
//public class Theme extends CustomEntity implements Serializable {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    /**
//     * عنوان
//     */
//    @NotNull
//    @Column(name = "title", nullable = false)
//    private String title;
//
//    /**
//     * جیسون تنظیمات تم
//     */
//    @Column(name = "setting")
//    private String setting;
//
//
//
//}
