package com.motaharinia.ms.iam.modules.theme.presentation.dto;


import com.motaharinia.msutility.tools.fso.view.FileViewDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.HashMap;


/**
 * کلاس مدل درخواست ویرایش تم
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ThemeUpdateRequestDto extends ThemeCreateRequestDto{

    @NotNull
    private Long id;

    public ThemeUpdateRequestDto(@NotNull Long id,@NotNull String title, HashMap<String, String> settingHashMap, ArrayList<FileViewDto> imageList) {
        super(title, settingHashMap, imageList);
        this.id = id;
    }
}
