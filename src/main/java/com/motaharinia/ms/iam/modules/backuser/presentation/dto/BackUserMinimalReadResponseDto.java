package com.motaharinia.ms.iam.modules.backuser.presentation.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * کلاس مدل مینیمال کاربر بک برنامه
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BackUserMinimalReadResponseDto {

    private Long id;

    /**
     * نام
     */
    private String firstName;

    /**
     * نام خانوادگی
     */
    private String lastName;


}
