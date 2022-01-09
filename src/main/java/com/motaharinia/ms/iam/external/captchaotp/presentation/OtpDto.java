package com.motaharinia.ms.iam.external.captchaotp.presentation;


import com.motaharinia.ms.iam.external.captchaotp.business.enumeration.OtpStatusEnum;
import com.motaharinia.ms.iam.external.common.enumeration.SourceProjectEnum;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


/**
 * @author eng.motahari@gmail.com<br>
 *  کلاس مدل رمز یکبار مصرف
 */

@Data
@NoArgsConstructor
public class OtpDto implements Serializable {

    /**
     * سرویس درخواست دهنده
     */
    private SourceProjectEnum sourceProjectEnum;
    /**
     * کلید رمز یکبار مصرف
     */
    private String key;
    /**
     * رمز یکبار مصرف
     */
    private String value;
    /**
     * تاریخ تولید
     */
    private Long createAt;
    /**
     * تعداد دفعات ورود اشتباه توسط کاربر
     */
    private Integer failedRetryCount =0;
    /**
     * تاریخ آخرین تلاش اشتباه
     */
    private Long failedRetryLastDate;
    /**
     * وضعیت بررسی
     */
    private OtpStatusEnum statusEnum;


}
