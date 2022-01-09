package com.motaharinia.ms.iam.external.captchaotp.presentation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author m.azish
 * کلاس مدل کلمه کاربری برای انوتیشن های کاربر محور
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AspectUsernameDto implements Serializable {
    private String aspectUsername;
}
