package com.motaharinia.ms.iam.modules.fso.presentation.frontuploader;


import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author eng.motahari@gmail.com<br>
 * کلاس مدل پاسخ درخواست تکه ای فایل برای آپلودر فرانت پنل
 */
@Data
@NoArgsConstructor
public class FineUploaderResponseDto implements Serializable {
    /**
     * آپلود موفقیت آمیز بوده است؟
     */
    private Boolean success;
    /**
     * خطای آپلود
     */
    private String error;
    /**
     * جلوگیری از تلاش مجدد
     */
    private Boolean preventRetry;
}
