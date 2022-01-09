package com.motaharinia.ms.iam.modules.appuser.business.randomString;

import java.util.Locale;
import java.util.Objects;
import java.util.Random;

/**
 * @author m.azish
 */
public class GenerateRandomString {

    /**
     * Generate a random string.
     */
    public String nextString() {
        for (int idx = 0; idx < buf.length; ++idx)
            buf[idx] = symbols[random.nextInt(symbols.length)];
        return new String(buf);
    }

    static String upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    static String lower = upper.toLowerCase(Locale.ROOT);

    static String digits = "0123456789";

    static String alphanum = upper + lower + digits;

    private final Random random;

    private final char[] symbols;

    private final char[] buf;

    public GenerateRandomString(int length, Random random, String symbols) {
        if (length < 1) throw new IllegalArgumentException();
        if (symbols.length() < 2) throw new IllegalArgumentException();
        this.random = Objects.requireNonNull(random);
        this.symbols = symbols.toCharArray();
        this.buf = new char[length];
    }

    /**
     * Create an alphanumeric string generator.
     */
    public GenerateRandomString(int length, Random random) {
        this(length, random, alphanum);
    }

}
