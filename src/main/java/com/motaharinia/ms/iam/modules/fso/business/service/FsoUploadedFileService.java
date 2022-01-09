package com.motaharinia.ms.iam.modules.fso.business.service;


import com.motaharinia.ms.iam.modules.fso.presentation.FsoUploadedFileDto;
import com.motaharinia.ms.iam.modules.fso.presentation.frontuploader.FineUploaderChunkDto;
import org.springframework.stereotype.Component;

import java.io.IOException;


/**
 * @author eng.motahari@gmail.com<br>
 * اینترفییس سرویس فایلهای آپلود شده<br>
 */
@Component
public interface FsoUploadedFileService {

    /**
     * این متد قسمتی از اطلاعات آپلود مربوط به فرانت پنل کلاینت ساید را از ورودی دریافت میکند و فایل و اطلاعات دیتابیسی آن را ذخیره مینماید
     *
     * @param fineUploaderChunkDto مدل داده ارسالی از کلاینت
     * @return خروجی: مدل اطلاعات فایل آپلود شده
     * @throws IOException خطا
     */
    FsoUploadedFileDto uploadToFileDto( FineUploaderChunkDto fineUploaderChunkDto) throws IOException;


    /**
     * این متد یک لاگ دیتابیس از اطلاعات فایل آپلود شده در دیتابیس ذخیره مینماید
     *
     * @param fsoUploadedFileDto مدل فایل آپلود شده
     * @return خروجی: مدل فایل آپلود شده
     */
    FsoUploadedFileDto create(FsoUploadedFileDto fsoUploadedFileDto);

    /**
     * این متد کلید فایل مورد نظر فایل آپلود شده را از ورودی دریافت کرده و مدل آن را خروجی میدهد
     *
     * @param fileKey کلید فایل آپلود شده مورد نظر
     * @return خروجی: مدل اطلاعات فایل آپلود شده
     * @throws IOException خطا
     */
    FsoUploadedFileDto readByFileKey(String fileKey) throws IOException;

    /**
     * این متد کلید فایل آپلود شده مورد نظر را از ورودی دریافت کرده و آن را حذف مینماید
     *
     * @param fileKey کلید فایل آپلود شده مورد نظر
     */
    void delete(String fileKey);

}
