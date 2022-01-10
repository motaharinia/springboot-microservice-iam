package com.motaharinia.ms.iam.config.security.oauth2.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * @author eng.motahari@gmail.com<br>
 * لاگین شده client کلاس مدل
 * */
@Data
@NoArgsConstructor
public class LoggedInClientDto implements Serializable {
    /**
     * شناسه
     */
    private Long id;
    /**
     *کلمه کاربری
     */
    private String clientId;

    /**
     *عنوان کاربری
     */
    private String clientTitle;

    /**
     *اختیاراتی را که به کلاینت OAuth اعطا شده است .
     */
    private Set<String> authoritySet = new HashSet<>();

}
