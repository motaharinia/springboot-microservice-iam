//package com.motaharinia.ms.iam.config.security.oauth2.authorization.last;
//
//import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
//import org.springframework.security.oauth2.common.OAuth2AccessToken;
//import org.springframework.security.oauth2.provider.OAuth2Authentication;
//import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
//
//import java.util.LinkedHashMap;
//import java.util.Map;
//
///**
// * @author eng.motahari@gmail.com<br>
// * کلاس کمکی که بین مقادیر رمزگذاری شده کد JWT و اطلاعات احراز هویت OAuth ترجمه می شود (در هر دو جهت). همچنین هنگام اعطای توکن به عنوان TokenEnhancer عمل می کند.
// */
//public class CustomTokenEnhancer extends JwtAccessTokenConverter {
//    @Override
//    public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
//
//        Map<String, Object> info = new LinkedHashMap<String, Object>(accessToken.getAdditionalInformation());
//
////        info.put("email", securityUser.getEmail());
//
//        DefaultOAuth2AccessToken customAccessToken = new DefaultOAuth2AccessToken(accessToken);
//        customAccessToken.setAdditionalInformation(info);
//
//        return super.enhance(customAccessToken, authentication);
//    }
//}
