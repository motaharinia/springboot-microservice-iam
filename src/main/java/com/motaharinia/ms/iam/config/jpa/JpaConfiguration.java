package com.motaharinia.ms.iam.config.jpa;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import th.co.geniustree.springdata.jpa.repository.support.JpaSpecificationExecutorWithProjectionImpl;

/**
 * @author eng.motahari@gmail.com<br>
 * کلاس تنظیمات jpa و دیتابیس
 */

@Configuration
@EnableJpaRepositories(repositoryBaseClass = JpaSpecificationExecutorWithProjectionImpl.class, basePackages = {"com.motaharinia"})
@EnableTransactionManagement
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
public class JpaConfiguration {
    @Bean
    @Autowired
    @Primary
    public JpaTransactionManager customTransactionManager(LocalContainerEntityManagerFactoryBean entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory.getObject());
    }
}
