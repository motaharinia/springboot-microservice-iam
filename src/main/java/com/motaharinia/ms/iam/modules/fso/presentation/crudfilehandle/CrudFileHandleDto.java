package com.motaharinia.ms.iam.modules.fso.presentation.crudfilehandle;


import com.motaharinia.ms.iam.modules.fso.business.enumeration.CrudFileHandleActionEnum;
import com.motaharinia.ms.iam.modules.fso.presentation.validation.FsoSettingDto;
import com.motaharinia.msutility.tools.fso.view.FileViewDto;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.List;


/**
 * @author eng.motahari@gmail.com<br>
 *کلاس مدل فایلها در کراد ماژولها
 */
@Data
@AllArgsConstructor
public class CrudFileHandleDto implements Serializable {
    /**
     * شناسه انتیتی
     */
    private Long entityId;
    /**
     * نوع عملیات فایل که میتواند ثبت ، ویرایش یا حذف باشد
     */
    private CrudFileHandleActionEnum crudFileHandleActionEnum;
    /**
     * لیست مدل مشاهده فایلها
     */
    private List<FileViewDto> fileViewDtoList;
    /**
     * کلاس مدل تنظیمات فایل
     */
    private FsoSettingDto fsoSettingDto;
}
