package com.motaharinia.ms.iam.config.websocket;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * @author eng.motahari@gmail.com<br>
 * کلاس تنظیمات ارسال پیام از طریق سوکت بر پایه وب
 * <p>
 * https://helptechcommunity.wordpress.com/2020/01/28/websocket-chat-application-using-spring-boot-and-react-js/
 * <p>
 * @EnableWebSocketMessageBroker annotation enables broker implementation for web socket.
 * By default, spring uses in-memory broker using STOMP.
 * But Spring exposes easy way of replacing to RabbitMQ, ActiveMQ broker etc.
 */


@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry stompEndpointRegistry) {
        //Register a STOMP over WebSocket endpoint at the given mapping path.
        //You must add .setAllowedOrigins(“*”) when you are calling the client from different domain.
        stompEndpointRegistry.addEndpoint("/websocket")
                .setAllowedOrigins("*")
                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry messageBrokerRegistry) {
        //Enable a simple message broker and configure one or more prefixes to filter destinations targeting the broker (e.g. destinations prefixed with "/topic").
        //If you don't specify the relevant topic name, then client will fail to subscribe to given topic.
        messageBrokerRegistry.enableSimpleBroker("/topic");
        //Configure one or more prefixes to filter destinations targeting application annotated methods.
        //When messages are processed, the matching prefix is removed from the destination in order to form the lookup path.
        //This means annotations should not contain the destination prefix.
        messageBrokerRegistry.setApplicationDestinationPrefixes("/app");
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        //You must implement CORS [Cross-Origin Resource Sharing] when websocket is accessible by other web servers.

        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**").allowCredentials(true).allowedOrigins("*").allowedMethods("*");
            }
        };
    }
}