package com.qlatform.quant.config.mongodb;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories(
        basePackages = "com.qlatform.quant.repository.userdb",
        mongoTemplateRef = "userDbTemplate"
)
public class UserDbConfig extends AbstractMongoClientConfiguration {
    @Value("${spring.data.mongodb.userdb.uri}")
    private String mongoUri;

    @Override
    @Bean(name = "userDbName")
    protected @NonNull String getDatabaseName() {
        return "userdb";
    }

    @Override
    @Bean(name = "userMongoClient")
    public @NonNull MongoClient mongoClient() {
        return MongoClients.create(mongoUri);
    }

    @Primary
    @Bean(name = "userDbTemplate")
    public MongoTemplate userDbTemplate() throws Exception {
        return new MongoTemplate(mongoClient(), getDatabaseName());
    }
}
