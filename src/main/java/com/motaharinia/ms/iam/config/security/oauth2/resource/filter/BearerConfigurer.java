package com.motaharinia.ms.iam.config.security.oauth2.resource.filter;

import com.motaharinia.ms.iam.config.security.oauth2.resource.ResourceClientTokenProvider;
import com.motaharinia.ms.iam.config.security.oauth2.resource.ResourceUserTokenProvider;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

public class BearerConfigurer extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

    private final ResourceUserTokenProvider resourceUserTokenProvider;
    private final ResourceClientTokenProvider resourceClientTokenProvider;

    public BearerConfigurer(final ResourceUserTokenProvider resourceUserTokenProvider, ResourceClientTokenProvider resourceClientTokenProvider) {
        this.resourceUserTokenProvider = resourceUserTokenProvider;
        this.resourceClientTokenProvider = resourceClientTokenProvider;
    }

    @Override
    public void configure(HttpSecurity http) {
        http.addFilterBefore(new BearerFilter(resourceUserTokenProvider, resourceClientTokenProvider), UsernamePasswordAuthenticationFilter.class);
    }
}