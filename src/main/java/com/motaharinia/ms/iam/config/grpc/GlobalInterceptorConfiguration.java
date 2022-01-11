package com.motaharinia.ms.iam.config.grpc;

import com.motaharinia.ms.iam.config.log.grpc.GrpcExceptionTranslator;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.config.GrpcServerProperties;
import net.devh.boot.grpc.server.interceptor.GlobalServerInterceptorConfigurer;
import net.devh.boot.grpc.server.security.authentication.CompositeGrpcAuthenticationReader;
import net.devh.boot.grpc.server.security.authentication.GrpcAuthenticationReader;
import net.devh.boot.grpc.server.security.authentication.SSLContextGrpcAuthenticationReader;
import net.devh.boot.grpc.server.serverfactory.GrpcServerConfigurer;
import net.devh.boot.grpc.server.serverfactory.ShadedNettyGrpcServerFactory;
import net.devh.boot.grpc.server.service.GrpcServiceDefinition;
import net.devh.boot.grpc.server.service.GrpcServiceDiscoverer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.util.ArrayList;
import java.util.List;


/**
 * @author eng.motahari@gmail.com<br>
 * کلاس مدیریت interceptor grpc برای ثبت interceptor مدیریت خطاها
 */

@Configuration
@Slf4j
public class GlobalInterceptorConfiguration {

    @Bean
    public GlobalServerInterceptorConfigurer globalInterceptorConfigurerAdapter(GrpcExceptionTranslator grpcExceptionTranslator) {
//        return new GlobalServerInterceptorConfigurer() {
//            @Override
//            public void addServerInterceptors(GlobalServerInterceptorRegistry registry) {
//                registry.addServerInterceptors(grpcExceptionTranslator);
//            }
//        };
        return registry -> registry.addServerInterceptors(grpcExceptionTranslator);
    }

    @Bean
    GrpcAuthenticationReader authenticationReader() {
        final List<GrpcAuthenticationReader> readers = new ArrayList<>();
        readers.add(new SSLContextGrpcAuthenticationReader());
        return new CompositeGrpcAuthenticationReader(readers);
    }

    @Bean
    public ShadedNettyGrpcServerFactory shadedNettyGrpcServerFactory(
            final GrpcServerProperties properties,
            final GrpcServiceDiscoverer serviceDiscoverer,
            final List<GrpcServerConfigurer> serverConfigurers) {

        log.info("Detected grpc-netty-shaded: Creating ShadedNettyGrpcServerFactory");
        final ShadedNettyGrpcServerFactory factory = new ShadedNettyGrpcServerFactory(properties, serverConfigurers);
        for (final GrpcServiceDefinition service : serviceDiscoverer.findGrpcServices()) {
            factory.addService(service);
        }
        return factory;
    }

    @Bean
    public TaskScheduler taskScheduler() {
        return new ThreadPoolTaskScheduler();
    }
}
