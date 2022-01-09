package com.motaharinia.ms.iam.external.captchaotp.business.service;

import com.motaharinia.ms.iam.external.captchaotp.presentation.OtpDto;
import com.motaharinia.ms.iam.external.common.enumeration.SourceProjectEnum;
import org.jetbrains.annotations.NotNull;


/**
 * @author eng.motahari@gmail.com<br>
 * کلاس اینترفیس سرویس بیرونی کپچا حامی
 */
public interface CaptchaOtpExternalService {



    /**
     * متد تولیدکننده کلید و مقدار کد کپچا (به همراه تصویر) بر اساس کلید کپچا دلخواه
     *
     * @param sourceProjectEnum پروژه درخواست دهنده
     * @param key               کلید کد کپچا
     * @param captchaLength     طول کپچا
     * @param captchaTtl        طول عمر کپچا
     * @return خروجی: تصویر کپچا
     */
    byte[] captchaCreate(@NotNull SourceProjectEnum sourceProjectEnum, @NotNull String key, @NotNull Integer captchaLength, @NotNull Long captchaTtl);

    /**
     * متد بررسی کپچا
     *
     * @param sourceProjectEnum پروژه درخواست دهنده
     * @param key               کلید کد کپچا
     * @param value             مقدار کد کپچا
     * @param methodName        نام متد
     * @param username          نام کاربری
     * @param tryCount          تعداد تلاش
     * @param tryTtlInMinutes   مدت زمان فاصله ی بین هر تلاش برای فراخوانی هر متد
     * @param banTtlInMinutes   مدت زمان محدود شدن کاربر بلاک شده روی متد
     */
    void captchaCheck(@NotNull SourceProjectEnum sourceProjectEnum, @NotNull String key, @NotNull String value, @NotNull String methodName, @NotNull String username, @NotNull Integer tryCount, @NotNull Integer tryTtlInMinutes, @NotNull Integer banTtlInMinutes);

    /**
     * متد تولیدکننده رمز یکبار مصرف بر اساس کلید رمز دلخواه
     *
     * @param sourceProjectEnum پروژه درخواست دهنده
     * @param key               کلید کد رمز یکبار مصرف
     * @param otpLength         طول رمز یکبار مصرف
     * @param otpTtl            طول عمر رمز یکبار مصرف
     * @return خروجی: مدل رمز یکبار مصرف
     */
    @NotNull
    OtpDto otpCreate(@NotNull SourceProjectEnum sourceProjectEnum, @NotNull String key, @NotNull Integer otpLength, @NotNull Long otpTtl);

    /**
     * متد بررسی رمز یکبار مصرف
     *
     * @param sourceProjectEnum پروژه درخواست دهنده
     * @param key               کلید رمز یکبار مصرف
     * @param value             رمز یکبار مصرف
     * @param methodName        نام متد
     * @param username          نام کاربری
     * @param tryCount          تعداد تلاش
     * @param tryTtlInMinutes   مدت زمان فاصله ی بین هر تلاش برای فراخوانی هر متد
     * @param banTtlInMinutes   مدت زمان محدود شدن کاربر بلاک شده روی متد
     */
    void otpCheck(@NotNull SourceProjectEnum sourceProjectEnum, @NotNull String key, @NotNull String value, @NotNull String methodName, @NotNull String username, @NotNull Integer tryCount, @NotNull Integer tryTtlInMinutes, @NotNull Integer banTtlInMinutes);
}
