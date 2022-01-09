package com.motaharinia.ms.iam.config.security.oauth2.authorization;

import com.motaharinia.ms.iam.modules.securityclient.business.service.SecurityClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;

import javax.sql.DataSource;

/**
 * @author eng.motahari@gmail.com<br>
 * کلاس تنظیمات سرور احراز هویت
 */
@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfiguration extends AuthorizationServerConfigurerAdapter {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private SecurityClientService securityClientService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private DataSource dataSource;

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints.authenticationManager(authenticationManager);
        endpoints.setClientDetailsService(securityClientService);
    }

    @Override
    public void configure(ClientDetailsServiceConfigurer clientDetailsServiceConfigurer) throws Exception {
//        clientDetailsServiceConfigurer.jdbc(dataSource).passwordEncoder(passwordEncoder);

        //https://www.gitmemory.com/issue/spring-projects/spring-security-oauth/1800/596780646
        clientDetailsServiceConfigurer.withClientDetails(securityClientService);

//        //https://dzone.com/articles/how-to-configure-an-oauth2-authentication-with-spr
//        clientDetailsServiceConfigurer.inMemory()
//                .withClient("client1")
//                .secret("{noop}123456")
//                .authorizedGrantTypes("client_credentials")
//                .scopes("all")
//                .accessTokenValiditySeconds(1 * 60 * 60)
//                .refreshTokenValiditySeconds(6 * 60 * 60)
//        ;
    }
}
