//package com.motaharinia.ms.iam.config.security.oauth2.authorization.last;
//
//import SecurityUserServiceImpl;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.oauth2.provider.ClientDetails;
//import org.springframework.security.oauth2.provider.ClientDetailsService;
//import org.springframework.security.oauth2.provider.OAuth2Authentication;
//import org.springframework.security.oauth2.provider.TokenRequest;
//import org.springframework.security.oauth2.provider.request.DefaultOAuth2RequestFactory;
//import org.springframework.security.oauth2.provider.token.TokenStore;
//import org.springframework.util.ObjectUtils;
//
//import java.util.Map;
//
//
///**
// * @author eng.motahari@gmail.com<br>
// * پیاده سازی پیش فرض OAuth2RequestFactory که فیلدها را از مپ پارامترها مقداردهی اولیه می کند ، انواع اعطای اعتبار و دامنه ها را تأیید می کند و در صورت عدم وجود ، دامنه ها را با مقادیر پیش فرض کلاینت پر می کند.
// */
//public class CustomOauth2RequestFactory extends DefaultOAuth2RequestFactory {
//    @Autowired
//    private TokenStore tokenStore;
//
//    @Autowired
//    private SecurityUserServiceImpl userDetailsService;
//
//    public CustomOauth2RequestFactory(ClientDetailsService clientDetailsService) {
//        super(clientDetailsService);
//    }
//
//    @Override
//    public TokenRequest createTokenRequest(Map<String, String> requestParameters, ClientDetails authenticatedClient) {
//        if (!ObjectUtils.isEmpty(requestParameters) && !ObjectUtils.isEmpty(requestParameters.get("grant_type")) && requestParameters.get("grant_type").equals("refresh_token")) {
//            OAuth2Authentication authentication = tokenStore.readAuthenticationForRefreshToken(tokenStore.readRefreshToken(requestParameters.get("refresh_token")));
//            SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(authentication.getName(), null, userDetailsService.loadUserByUsername(authentication.getName()).getAuthorities()));
//        }
//        return super.createTokenRequest(requestParameters, authenticatedClient);
//    }
//}
