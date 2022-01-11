package com.motaharinia.ms.iam.config.mongo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;


/**
 * @author eng.motahari@gmail.com<br>
 * کلاس تنظیمات mongo
 */

@Configuration
@EnableMongoRepositories(basePackages = {"com.motaharinia"})
@EnableMongoAuditing(auditorAwareRef = "auditorAware")
public class MongoConfiguration {
    @Bean
    MongoTransactionManager mongoTransactionManager(MongoDatabaseFactory dbFactory) {
        return new MongoTransactionManager(dbFactory);
    }
}
