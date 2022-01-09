package com.motaharinia.ms.iam;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.data.ldap.repository.config.EnableLdapRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author eng.motahari@gmail.com<br>
 * کلاس اصلی اجرای اپلیکیشن
 */
@SpringBootApplication(scanBasePackages = {"com.motaharinia"})
@ConfigurationPropertiesScan("com.motaharinia")
@EnableEurekaClient
@EnableScheduling
@EnableLdapRepositories(basePackages = "com.motaharinia.iam.external.ldap")
public class IamApplication {

    public static void main(String[] args) {
        SpringApplication.run(IamApplication.class, args);
    }


}
