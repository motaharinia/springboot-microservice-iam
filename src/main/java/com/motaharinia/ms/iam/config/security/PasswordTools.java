package com.motaharinia.ms.iam.config.security;

import org.jetbrains.annotations.NotNull;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * کلاس تولید و چک کردن پسورد
 */
public interface PasswordTools {
    /**
     * تولید رمز
     * @param passwordEncoder کلاس پیاده سازpasswordEncoder
     * @param password رمز عبور
     * @return
     */
    static String encode(@NotNull PasswordEncoder passwordEncoder , @NotNull String password){
        return passwordEncoder.encode(password);
    }

    /**
     * بررسی رمز
     * @param passwordEncoder کلاس پیاده سازpasswordEncoder
     * @param password رمز عبور
     * @param encodedPassword رمز عبور انکد شده
     * @return
     */
    static Boolean check(@NotNull PasswordEncoder passwordEncoder ,@NotNull String password,@NotNull String encodedPassword){
        return passwordEncoder.matches(password,encodedPassword);
    }
}
