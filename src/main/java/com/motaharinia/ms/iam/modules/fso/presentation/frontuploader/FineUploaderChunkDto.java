package com.motaharinia.ms.iam.modules.fso.presentation.frontuploader;


import com.motaharinia.ms.iam.modules.fso.business.enumeration.SubSystemEnum;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author eng.motahari@gmail.com<br>
 * کلاس مدل درخواست تکه ای فایل برای آپلودر فرانت پنل
 */
@Data
@NoArgsConstructor
public class FineUploaderChunkDto implements Serializable {

    /**
     * کلید فایلی که در کلاینت تولید میشود و برای هر فایل در حال آپلود یونیک است
     */
    private String fileKey;
    /**
     * نام و پسوند فایل در کلاینت
     * user1.jpg
     */
    private String fileFullName;
    /**
     * مسیر فایل در کلاینت
     */
    private String filePath;
    /**
     * حجم فایل
     * 120000
     */
    private Long fileSize=0L;
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
     * تعداد تکه های داده فایل
     */
    private Integer fileChunkCount = -1;
    /**
     * اندیس تکه داده فعلی از داده فایل در حال ارسال
     */
    private Integer fileChunkIndex = -1;
    /**
     * سایز تکه داده فعلی در حال ارسال
     */
    private Long fileChunkSize =0L;
    /**
     * داده فایل
     */
    private byte[] fileByteArray;

}
