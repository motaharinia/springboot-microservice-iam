package com.motaharinia.ms.iam.modules.theme.presentation.dto;

import com.motaharinia.msutility.custom.customvalidation.stringlength.StringLength;
import com.motaharinia.msutility.tools.fso.view.FileViewDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * کلاس مدل درخواست ثبت تم
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ThemeCreateRequestDto implements Serializable {
    /**
     * عنوان
     */
    @NotNull
    @StringLength(min = 1 , max = 30)
    private String title;

    /**
     * هش مپ تنظیمات تم
     */
    private HashMap<String,String> settingHashMap = new HashMap<>();

    /**
     * عکس های مربوط به تم
     */
    private ArrayList<FileViewDto> imageList = new ArrayList<>();
}