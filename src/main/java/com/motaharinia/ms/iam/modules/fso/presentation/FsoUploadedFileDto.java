package com.motaharinia.ms.iam.modules.fso.presentation;


import com.motaharinia.ms.iam.modules.fso.business.enumeration.SubSystemEnum;
import com.motaharinia.msutility.tools.fso.upload.FileUploadedDto;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * @author eng.motahari@gmail.com<br>
 * کلاس مدل فایل آپلود شده
 */
@Data
@NoArgsConstructor
public class FsoUploadedFileDto implements Serializable {
    /**
     * کلید فایلی که در کلاینت تولید میشود و برای هر فایل در حال آپلود یونیک است
     */
    private String fileKey;
    /**
     * نام فایل
     */
    private String fileName;
    /**
     * پسوند فایل
     */
    private String fileExtension;
    /**
     * نام و پسوند فایل
     */
    private String fileFullName;
    /**
     * حجم فایل
     */
    private Long fileSize;
    /**
     * نوع فایل
     */
    private String fileMimeType;
    /**
     * مسیر آپلود فایل روی سرور
     */
    private String fileUploadedPath;
    /**
     * زمان آپلود
     */
    private Date fileUploadDateTime;
    /**
     * زیرسیستم فایل
     *مثلا catalog
     */
    private SubSystemEnum fileSubSystem;
    /**
     * انتیتی فایل
     * مثلا product
     */
    private String fileEntity;
    /**
     * نوع فایل داخل انتیتی
     * مثلا coverImage
     */
    private String fileKind;
    /**
     * داده فایل
     */
    private byte[] fileByteArray;
    /**
     * مسیر دایرکتوری واقعی
     */
    private String directoryRealPath;
    /**
     * مسیر دایرکتوری هش شده
     */
    private String directoryHashedPath;


    public FileUploadedDto getFileUploadedDto(){
        FileUploadedDto fileUploadedDto=new FileUploadedDto();
        fileUploadedDto.setUploadDateTime(this.getFileUploadDateTime());
        fileUploadedDto.setDataByteArray(this.getFileByteArray());
        fileUploadedDto.setDirectoryHashedPath(this.getDirectoryHashedPath());
        fileUploadedDto.setDirectoryRealPath(this.getDirectoryRealPath());
        fileUploadedDto.setEntity(this.getFileEntity());
        fileUploadedDto.setSubSystem(this.getFileSubSystem().getValue());
        fileUploadedDto.setUploadedPath(this.getFileUploadedPath());
        fileUploadedDto.setExtension(this.getFileExtension());
        fileUploadedDto.setFullName(this.getFileFullName());
        fileUploadedDto.setMimeType(this.getFileMimeType());
        fileUploadedDto.setName(this.getFileName());
        Long size=this.getFileSize();
        fileUploadedDto.setSize(size);
        return fileUploadedDto;
    }
}
