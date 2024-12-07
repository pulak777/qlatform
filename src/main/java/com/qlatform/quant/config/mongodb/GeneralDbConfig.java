package com.qlatform.quant.config.mongodb;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories(
        basePackages = "com.qlatform.quant.repository.generaldb",
        mongoTemplateRef = "generalDbTemplate"
)
public class GeneralDbConfig extends AbstractMongoClientConfiguration {
    @Value("${spring.data.mongodb.generaldb.uri}")
    private String mongoUri;

    @Override
    @Bean(name = "generalDbName")
    protected @NonNull String getDatabaseName() {
        return "generaldb";
    }

    @Override
    @Bean(name = "generalMongoClient")
    public @NonNull MongoClient mongoClient() {
        return MongoClients.create(mongoUri);
    }

    @Bean(name = "generalDbTemplate")
    public MongoTemplate generalDbTemplate() throws Exception {
        return new MongoTemplate(mongoClient(), getDatabaseName());
    }
}
