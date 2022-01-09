//package com.motaharinia.ms.iam.config.security.oauth2.authorization.last;
//
//import SecurityUserServiceImpl;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.core.io.ClassPathResource;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
//import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
//import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
//import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
//import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
//import org.springframework.security.oauth2.provider.ClientDetailsService;
//import org.springframework.security.oauth2.provider.OAuth2RequestFactory;
//import org.springframework.security.oauth2.provider.endpoint.TokenEndpointAuthenticationFilter;
//import org.springframework.security.oauth2.provider.token.TokenStore;
//import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
//import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
//import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;
//
//import javax.sql.DataSource;
//
///**
// * @author eng.motahari@gmail.com<br>
// * کلاس تنظیمات سرور احراز هویت
// */
//@Configuration
//@EnableAuthorizationServer
//public class AuthorizationServerConfiguration extends AuthorizationServerConfigurerAdapter {
//
//    /**
//     * فایل jks که حاوی کلید عمومی و رمزنگاری توکنها است
//     */
//    @Value("${app.security.jwt.keystore-location}")
//    private String KEY_STORE_LOCATION;
//    /**
//     * رمز فایل استور jks که در زمان تولید فایل تنظیم شده است
//     */
//    @Value("${app.security.jwt.keystore-password}")
//    private String KEY_STORE_PASSWORD;
//    /**
//     * نام دیگر سرور در فایل jks که در زمان تولید فایل تنظیم شده است
//     */
//    @Value("${app.security.jwt.key-alias}")
//    private String KEY_ALIAS;
//    /**
//     * آیا کنترل محدوده دسترسی صورت بگیرد؟
//     */
//    @Value("${app.security.check-user-scopes}")
//    private Boolean CHECK_USER_SCOPE;
//
//    @Autowired
//    private DataSource dataSource;
//
//    @Autowired
//    private PasswordEncoder passwordEncoder;
//
//    @Autowired
//    private SecurityUserServiceImpl userDetailsService;
//
//    @Autowired
//    private ClientDetailsService clientDetailsService;
//
//    @Autowired
//    @Qualifier("authenticationManagerBean")
//    private AuthenticationManager authenticationManager;
//
//
////    /**
////     * از این تنظمیات در زمانی استفاده میشود که بخواهیم امنیت را در گراف کیو ال استفاده کنیم
////     * @return
////     */
////    @Bean
////    public GrpcAuthenticationReader grpcAuthenticationReader() {
////        return new BasicGrpcAuthenticationReader();
////    }
//
//
//    @Bean
//    public OAuth2RequestFactory requestFactory() {
//        CustomOauth2RequestFactory requestFactory = new CustomOauth2RequestFactory(clientDetailsService);
//        requestFactory.setCheckUserScopes(CHECK_USER_SCOPE);
//        return requestFactory;
//    }
//
//    //@Bean
//    public TokenStore tokenStore() {
//        return new JwtTokenStore(jwtAccessTokenConverter());
//    }
//
//    //@Bean
//    public JwtAccessTokenConverter jwtAccessTokenConverter() {
//        JwtAccessTokenConverter converter = new CustomTokenEnhancer();
//        converter.setKeyPair(new KeyStoreKeyFactory(new ClassPathResource(KEY_STORE_LOCATION), KEY_STORE_PASSWORD.toCharArray()).getKeyPair(KEY_ALIAS));
//        return converter;
//    }
//
//
//    @Override
//    public void configure(ClientDetailsServiceConfigurer clientDetailsServiceConfigurer) throws Exception {
////        clientDetailsServiceConfigurer.jdbc(dataSource).passwordEncoder(passwordEncoder);
//
//        //https://www.gitmemory.com/issue/spring-projects/spring-security-oauth/1800/596780646
////        clientDetailsServiceConfigurer.withClientDetails(clientDetailsService);
//
//
//        //https://dzone.com/articles/how-to-configure-an-oauth2-authentication-with-spr
//        clientDetailsServiceConfigurer.inMemory()
//                .withClient("client1")
//                .secret("{noop}123456")
//                .authorizedGrantTypes("client_credentials", "refresh_token")
//                .scopes("all")
//                .accessTokenValiditySeconds(1 * 60 * 60)
//                .refreshTokenValiditySeconds(6 * 60 * 60)
//        ;
//    }
//
//
//    @Bean
//    public TokenEndpointAuthenticationFilter tokenEndpointAuthenticationFilter() {
//        return new TokenEndpointAuthenticationFilter(authenticationManager, requestFactory());
//    }
//
//
//    @Override
//    public void configure(AuthorizationServerSecurityConfigurer oauthServer) throws Exception {
//        oauthServer.tokenKeyAccess("permitAll()").checkTokenAccess("isAuthenticated()");
//    }
//
//    @Override
//    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
//        endpoints.tokenStore(tokenStore()).tokenEnhancer(jwtAccessTokenConverter()).authenticationManager(authenticationManager).userDetailsService(userDetailsService);
//        endpoints.requestFactory(requestFactory());
//    }
//}
