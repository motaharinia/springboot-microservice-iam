package com.motaharinia.ms.iam.modules.fso.persistence.orm.fso;


import com.motaharinia.ms.iam.modules.fso.business.enumeration.SubSystemEnum;
import com.motaharinia.msjpautility.entity.CustomEntity;
import com.motaharinia.msjpautility.entity.DbColumnDefinition;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;


/**
 * @author eng.motahari@gmail.com<br>
 * کلاس انتیتی فایلهای آپلود شده
 */

@Entity
@Table(name = "fso_uploaded_file")
@Data
@EqualsAndHashCode(callSuper = true)
public class FsoUploadedFile extends CustomEntity implements Serializable {
    /**
     * شناسه
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    /**
     * کلید فایل
     */
    @Column(name = "file_key")
    private String fileKey;
    /**
     * نام فایل
     */
    @Column(name = "file_name")
    private String fileName;
    /**
     * پسوند فایل
     */
    @Column(name = "file_extension")
    private String fileExtension;
    /**
     * نام و پسوند فایل
     */
    @Column(name = "file_full_name")
    private String fileFullName;
    /**
     * حجم فایل
     */
    @Column(name = "file_size")
    private Long fileSize;
    /**
     * نوع فایل
     */
    @Column(name = "file_mime_type")
    private String fileMimeType;
    /**
     * مسیر آپلود فایل روی سرور
     */
    @Column(name = "file_uploaded_path")
    private String fileUploadedPath;
    /**
     * زمان آپلود
     */
    @Column(name = "file_upload_date_time", columnDefinition = DbColumnDefinition.COLUMN_DEFINITION_DATE)
    private Date fileUploadDateTime;
    /**
     * زیرسیستم فایل
     *مثلا catalog
     */
    @Column(name = "file_subsystem")
    @Enumerated(EnumType.STRING)
    private SubSystemEnum fileSubSystem;
    /**
     * انتیتی فایل
     * مثلا product
     */
    @Column(name = "file_entity")
    private String fileEntity;
    /**
     * نوع فایل داخل انتیتی
     * مثلا coverImage
     */
    @Column(name = "file_kind")
    private String fileKind;

}
