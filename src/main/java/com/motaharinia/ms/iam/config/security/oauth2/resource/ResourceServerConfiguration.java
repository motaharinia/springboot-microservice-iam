package com.motaharinia.ms.iam.config.security.oauth2.resource;

import com.motaharinia.ms.iam.config.log.ExceptionLogger;
import com.motaharinia.ms.iam.config.security.oauth2.resource.filter.BearerConfigurer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.util.ObjectUtils;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;

import java.util.List;

/**
 * @author eng.motahari@gmail.com<br>
 * کلاس تنظیمات سرور ریسورس امنیت
 */
@Configuration
@EnableResourceServer
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {

    /**
     * رشته کلید عمومی که با دستور keytool از فایل jks قبلا دریافت شده است
     */
    @Value("${app.security.public-key-user}")
    private String securityPublicKey;

    /**
     * از چه ادرسهایی بتوان بکند را فراخوانی کرد
     */
    @Value("${app.security.cors.allowed-origins}")
    private String allowedOrigins;
    /**
     * با چه متدهایی بتوان بکند را فراخوانی کرد. مانند GET,POST,PUT,DELETE,OPTION
     */
    @Value("#{'${app.security.cors.allowed-methods}'.split(',')}")
    private List<String> allowedMethods;
    /**
     * چه هدرهایی را میتوان به سمت بکند ارسال کرد
     */
    @Value("${app.security.cors.allowed-headers}")
    private String allowedHeaders;
    /**
     * هدر ریسپانس تایید مجوز کاربر به فرانت اند
     */
    @Value("${app.security.cors.allow-credentials}")
    private Boolean allowCredentials;
    /**
     * چه هدرهایی از بکند به فرانت اند بتواند در ریسپانس ارسال شود
     */
    @Value("${app.security.cors.exposed-headers}")
    private String exposedHeaders;


    private final ResourceUserTokenProvider resourceUserTokenProvider;
    private final ResourceClientTokenProvider resourceClientTokenProvider;
    private final MessageSource messageSource;

    public ResourceServerConfiguration(ResourceUserTokenProvider resourceUserTokenProvider, ResourceClientTokenProvider resourceClientTokenProvider, MessageSource messageSource) {
        this.resourceUserTokenProvider = resourceUserTokenProvider;
        this.resourceClientTokenProvider = resourceClientTokenProvider;
        this.messageSource = messageSource;
    }

    /**
     * این متد
     *
     * @return
     */
    @Bean
    public SimpleMappingExceptionResolver exceptionResolver() {
        SimpleMappingExceptionResolver exceptionResolver = new SimpleMappingExceptionResolver();
        exceptionResolver.setExcludedExceptions(AccessDeniedException.class);
        return exceptionResolver;
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http
                .cors().configurationSource(corsConfigurationSource()).and()
                .csrf().disable()
                //.csrf().csrfTokenRepository(getCsrfTokenRepository()).and()
                //.csrf().csrfTokenRepository(new HttpSessionCsrfTokenRepository()).and()
                //.csrf().csrfTokenRepository(new CookieCsrfTokenRepository()).and()
                .httpBasic().disable()
                .headers().frameOptions().deny()
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .exceptionHandling()
                //اگر خطای 401 داشتیم
                .authenticationEntryPoint((httpServletRequest, httpServletResponse, authException) -> {
                    ExceptionLogger.securityException401(httpServletRequest, httpServletResponse, messageSource);
                })
                //اگر خطای 403 عادی و بدون کنترل با preAuthorize داشتیم
                .accessDeniedHandler((httpServletRequest, httpServletResponse, authException) -> {
                    ExceptionLogger.securityException403(httpServletRequest, httpServletResponse, messageSource);
                })
                .and()
                .authorizeRequests()
                //مسیرهایی که نیاز به کنترل دسترسی ندارند
                .antMatchers("/").permitAll()
                .antMatchers("/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()//swagger-OpenApi3
                .antMatchers("/actuator/**").permitAll() //actuator for discovery admin panel
                .antMatchers("/graphql").permitAll() //graphql api
                .antMatchers("/gui").permitAll() //graphql gui page
                .antMatchers("/api/v1.0/fso/**").permitAll() //مدیریت فایلهای پروژه
                .antMatchers("/api/v1.0/dev/**").permitAll()//تست توسعه دهنده
                .antMatchers("/api/v1.0/load-test/**").permitAll()//تست لود
                .antMatchers("/api/v1.0/app-user/signup-check-credential").permitAll()//مرحله اول ثبت نام فرانت پنل
                .antMatchers("/api/v1.0/app-user/signup-check-otp").permitAll()//مرحله دوم ثبت نام فرانت پنل
                .antMatchers("/api/v1.0/app-user/signin-check-credential").permitAll()//مرحله اول ورود فرانت پنل
                .antMatchers("/api/v1.0/app-user/signin-check-otp").permitAll()//مرحله دوم ورود فرانت پنل
                .antMatchers("/api/v1.0/app-user/forget-password-check-username/**").permitAll()//مرحله اول فراموشی رمز عبور فرانت پنل
                .antMatchers("/api/v1.0/app-user/forget-password-check-otp").permitAll()//مرحله دوم فراموشی رمز عبور فرانت پنل
                .antMatchers("/api/v1.0/back-user/signin-check-credential").permitAll()//ورود بک پنل
                .antMatchers("/api/v1.0/back-user/signin-check-otp").permitAll()//ورود بک پنل
                .antMatchers("/api/v1.0/back-user/forget-password-check-username/**").permitAll()//مرحله اول فراموشی رمز عبور بک پنل
                .antMatchers("/api/v1.0/back-user/forget-password-check-otp").permitAll()//مرحله دوم فراموشی رمز عبور بک پنل
                .antMatchers("/api/v1.0/security-user-token/renew-token/**").permitAll()//به روز رسانی توکن دسترسی سکیوریتی یوزر از توکن رفرش
                .antMatchers("/api/v1.0/security-client-token/renew-token/**").permitAll()//به روز رسانی توکن دسترسی سکیوریتی کلاینت از توکن رفرش
                .antMatchers("/api/v1.0/security-client/**").permitAll()//دریافت توکن کلاینت
                .antMatchers("/api/v1.0/signin-qrcode-generate/**").permitAll()//دریافت qrCode
                .antMatchers("/websocket/**").permitAll()//وب سوکت
                .antMatchers("/api/v1.0/captcha/**").permitAll()//تصویر کپچا
                .antMatchers("/api/v1.0/back/**").permitAll()//فراخوانی های بک تو بک


                //------------------------------------------------------------------------------------------------------------
                //این دسترسی ها فقط برای اجرای متد تست ایجاد شده اند و باید حذف شوند
                //.antMatchers("/api/v1.0/security-permission/**").permitAll()//addedByAzish
                //.antMatchers("/api/v1.0/security-role/**").permitAll()//addedByAzish
                //.antMatchers("/api/v1.0/app-user/**").permitAll()//addedByAzish
                //.antMatchers("/api/v1.0/back-user/**").permitAll()//addedByAzish
                //.antMatchers("/api/v1.0/security-token/**").permitAll()//addedByAzish
                //.antMatchers("/api/v1.0/security-user/**").permitAll()//addedByAzish
                //------------------------------------------------------------------------------------------------------------
                .anyRequest().authenticated()
                .and()
                .apply(securityConfigurerAdapter());
    }

//    @Override
//    public void configure(ResourceServerSecurityConfigurer resources) {
//        resources.resourceId("USER_ADMIN_RESOURCE").tokenStore(tokenStore);
//    }

    @Bean
    public CsrfTokenRepository getCsrfTokenRepository() {
        HttpSessionCsrfTokenRepository repository = new HttpSessionCsrfTokenRepository();
        repository.setHeaderName("X-XSRF-TOKEN");
        repository.setParameterName("_csrf");
        return repository;
    }

    @Override
    public void configure(ResourceServerSecurityConfigurer config) {
        config.tokenServices(tokenServices());
    }

    @Bean
    public TokenStore tokenStore() {
        return new JwtTokenStore(accessTokenConverter());
    }

    @Bean
    public JwtAccessTokenConverter accessTokenConverter() {
        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        converter.setVerifierKey(securityPublicKey);
        return converter;
    }

    @Bean
    @Primary
    public DefaultTokenServices tokenServices() {
        DefaultTokenServices defaultTokenServices = new DefaultTokenServices();
        defaultTokenServices.setTokenStore(tokenStore());
        return defaultTokenServices;
    }

    private BearerConfigurer securityConfigurerAdapter() {
        return new BearerConfigurer(resourceUserTokenProvider, resourceClientTokenProvider);
    }

    /**
     * تنظیمات CORS برای فراخوانی ریسورس از دامین و پورت متفاوت
     *
     * @return خروجی: فیلتر CorsConfigurationSource بصورت OpenApi
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(allowCredentials);
        config.setAllowedOrigins(List.of(allowedOrigins));
        config.setAllowedHeaders(List.of(allowedHeaders));
        config.setAllowedMethods(allowedMethods);
        /*
        config.setAllowCredentials(true);
        config.setAllowedOrigins(List.of("*"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowedMethods(List.of("POST", "GET", "DELETE", "PUT"));
         */
        if (!ObjectUtils.isEmpty(exposedHeaders) && !exposedHeaders.equals("*")) {
            config.setExposedHeaders(List.of(exposedHeaders.split(",")));
        }
        source.registerCorsConfiguration("/**", config);
        return source;
    }

}