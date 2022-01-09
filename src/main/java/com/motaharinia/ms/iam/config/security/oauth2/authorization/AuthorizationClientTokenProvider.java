package com.motaharinia.ms.iam.config.security.oauth2.authorization;


import com.motaharinia.ms.iam.config.security.oauth2.dto.LoggedInClientDto;
import com.motaharinia.ms.iam.config.security.oauth2.resource.ResourceClientTokenProvider;
import com.motaharinia.msutility.custom.customjson.CustomObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.jackson.io.JacksonSerializer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;

/**
 * کلاس مدیریت توکن ها در Authorization server
 * <p>
 * Reserved Claims:
 * iss – Issuer,
 * sub – Subject,
 * aud – Audience,
 * exp – Expiration,
 * nbf – Not Before,
 * iat – Issued At,
 * jti – JWT ID
 * <p>
 * https://svlada.com/jwt-token-authentication-with-spring-boot/
 * https://www.appsdeveloperblog.com/add-and-validate-custom-claims-in-jwt/
 * https://github.com/jwtk/jjwt/issues/131
 */
@Component
@Slf4j
public class AuthorizationClientTokenProvider {

    /**
     * فیلد کاستوم توکن : مدل کاربر امنیت
     */
    public static final String CUSTOM_CLAIM_LOGGED_IN_CLIENT = "logged_in_client";

    //----------------------------------------
    //Authorization Server variables
    //----------------------------------------

    /**
     * فایل jks که حاوی کلید عمومی و رمزنگاری توکنها است
     */
    @Value("${app.security.jwt.keystore-location-client}")
    private String KEY_STORE_LOCATION;
    /**
     * رمز فایل استور jks که در زمان تولید فایل تنظیم شده است
     */
    @Value("${app.security.jwt.keystore-password-client}")
    private String KEY_STORE_PASSWORD;
    /**
     * نام دیگر سرور در فایل jks که در زمان تولید فایل تنظیم شده است
     */
    @Value("${app.security.jwt.key-alias-client}")
    private String KEY_ALIAS;
    /**
     * آیا کنترل محدوده دسترسی صورت بگیرد؟
     */
    @Value("${app.security.check-client-scopes-client}")
    private Boolean CHECK_USER_SCOPE;


    /**
     * کلید خصوصی  که با آن توکنها رمز نگاری میشوند
     */
    private static PrivateKey privateKey;


    /**
     * این متد از فایل jks در ریسورس پروژه ، شی privateKey میسازد
     *
     * @return
     */
    private PrivateKey getPrivateKey() {
        //اگر کلید خصوصی از قبل ایجاد نشده باشد آن را یکبار ایجاد میکنیم
        if (privateKey == null) {
            try {
                //خواندن مخزن کلید و به دست آوردن کلید خصوصی داخل آن برای رمزنگاری توکن
                final KeyStore keyStore = KeyStore.getInstance("PKCS12", "SUN");
                keyStore.load(new ClassPathResource(KEY_STORE_LOCATION).getInputStream(), KEY_STORE_PASSWORD.toCharArray());
                privateKey = (PrivateKey) keyStore.getKey(KEY_ALIAS, KEY_STORE_PASSWORD.toCharArray());
            } catch (Exception exception) {
                log.error("Exception: {}", exception);
            }
            return privateKey;
        } else {
            return privateKey;
        }
    }

    /**
     * متد تولید اکسس توکن Bearer از شناسه کاربری و دسترسی های او
     *
     * @param loggedInClientDto        مدل کاربر لاگین شده
     * @param additionalClaimHashMap هش مپ اطلاعات دیگر مورد نیاز در توکن
     * @return خروجی: رشته توکن
     */
    public String createAccessToken(LoggedInClientDto loggedInClientDto, HashMap<String, Object> additionalClaimHashMap, long expiresIn) {

        //تاریخ صدور
        Date issuedAt = new Date();

        //تنظیم میزان عمر توکن به میلی ثانیه
        long now = (new Date()).getTime();
        Date validity = new Date(now + (expiresIn * 1000));


        //فیلدهای دلخواهی که با استفاده از دستور claim به توکن اضافه میکنیم
        //additionalClaimHashMap.put(CUSTOM_CLAIM_TOKEN_ID, UUID.randomUUID().toString());
        //اضافه کردن مدل کاربر به توکن
        additionalClaimHashMap.put(CUSTOM_CLAIM_LOGGED_IN_CLIENT, loggedInClientDto);

        //تولید توکن
        return Jwts.builder()
                //تنظیم مبدل جیسون
                .serializeToJsonWith(new JacksonSerializer(new CustomObjectMapper()))
                //فیلدهای دلخواهی که با استفاده از دستور claim به توکن اضافه میکنیم
                //.claim(CUSTOM_CLAIM_TOKEN_ID, UUID.randomUUID().toString())
                //اضافه کردن سایر claim ها به توکن
                .setClaims(additionalClaimHashMap)
                //تاریخ صدور
                .setIssuedAt(issuedAt)
                //کلمه کاربری
                .setSubject(loggedInClientDto.getClientId())
                //طول عمر
                .setExpiration(validity)

                //رمزگذازی با کلید خصوصی
                .signWith(SignatureAlgorithm.RS256, getPrivateKey())
                .compact();
    }

    public String resolveBasic(HttpServletRequest httpServletRequest) {
        String basic = httpServletRequest.getHeader(ResourceClientTokenProvider.AUTHORIZATION_HEADER);
        if (StringUtils.hasText(basic) && basic.startsWith("Basic ")) {
            return new String(Base64.getDecoder().decode(basic.substring(6)), StandardCharsets.UTF_8);
        }
        return null;
    }

}
