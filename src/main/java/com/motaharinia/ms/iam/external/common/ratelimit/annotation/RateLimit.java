package com.motaharinia.ms.iam.external.common.ratelimit.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author eng.motahari@gmail.com<br>
 * کلاس انوتیشن محدودیت بازدید متد
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RateLimit {
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
