package com.motaharinia.ms.iam.config.security.oauth2.resource.filter;

import com.motaharinia.ms.iam.config.security.oauth2.resource.ResourceClientTokenProvider;
import com.motaharinia.ms.iam.config.security.oauth2.resource.ResourceUserTokenProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.ObjectUtils;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class BearerFilter extends GenericFilterBean {

    private final ResourceUserTokenProvider resourceUserTokenProvider;
    private final ResourceClientTokenProvider resourceClientTokenProvider;

    public BearerFilter(ResourceUserTokenProvider resourceUserTokenProvider, ResourceClientTokenProvider resourceClientTokenProvider) {
        this.resourceUserTokenProvider = resourceUserTokenProvider;
        this.resourceClientTokenProvider = resourceClientTokenProvider;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        //ابتدا هدر Authorization را برای یوزرها بررسی میکنیم
        String accessToken = resourceUserTokenProvider.resolveAccessToken(httpServletRequest);
        if (!ObjectUtils.isEmpty(accessToken) && this.resourceUserTokenProvider.isValidToken(accessToken)) {
            Authentication authentication = this.resourceUserTokenProvider.getAuthentication(accessToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }else {
            //سپس هدر X-Authorization را برای کلاینت ها بررسی میکنیم
            accessToken = resourceClientTokenProvider.resolveAccessToken(httpServletRequest);
            if (!ObjectUtils.isEmpty(accessToken) && this.resourceClientTokenProvider.isValidToken(accessToken)) {
                Authentication authentication = this.resourceClientTokenProvider.getAuthentication(accessToken);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }


}
