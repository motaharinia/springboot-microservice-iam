package com.motaharinia.ms.iam.external.captchaotp.business.service;

import java.lang.annotation.*;

/**
 *  زمان استفاده از انوتیشن @CaptchaCheck در صورت لاگین نبودن جهت به دست آوردن username یا باید مدلAspectUsernameDto ایجاد شود یا از مدلAspectUsernameDto اکستند انجام شود و مقدار فیلد aspectUsername را برنامه نویس بصورت هاردکد پر نماید
 * در غیر این صورت در صورت لاگین بودن، username از کانتکس اسپرینگ سکیوریتی به دست میاید
 */
@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface CaptchaCheck {
    /**
     * تعداد تلاش
     *
     * @return خروجی:  تعداد تلاش
     */
    int tryCount();

    /**
     * مدت زمان فاصله ی بین هر تلاش برای فراخوانی هر متد
     *
     * @return خروجی:  مدت زمان فاصله ی بین هر تلاش برای فراخوانی هر متد
     */
    int tryTtlInMinutes();

    /**
     * مدت زمان محدود شدن کاربر بلاک شده روی متد
     *
     * @return خروجی:  مدت زمان محدود شدن کاربر بلاک شده روی متد
     */
    int banTtlInMinutes();

}
