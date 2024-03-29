package com.fxq.storage.autoconfigure;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.fxq.storage.service.StorageBlobService;
import com.fxq.storage.service.impl.StorageBlobServiceImpl;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;
import org.springframework.jdbc.core.JdbcTemplate;


/**
 * Created by xiangqing'fan on 2024/03/28
 */
@EnableConfigurationProperties({StorageProperties.class})
@ComponentScans({
        @ComponentScan("com.fxq.storage.controller"),
        @ComponentScan("com.fxq.storage.service")
})
@MapperScan("com.fxq.storage.mapper")
public class StorageAutoConfiguration {

    private final StorageProperties properties;

    public StorageAutoConfiguration(StorageProperties properties) {
        this.properties = properties;
    }

    @Bean
    public AmazonS3 amazonS3() {
        AWSCredentials credentials = new BasicAWSCredentials(
                properties.getAccessKey(),
                properties.getSecretKey()
        );

        return AmazonS3ClientBuilder
                .standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(properties.getEndpoint(), properties.getRegion()))
                .build();
    }

    @Bean
    public StorageBlobService storageBlobService(AmazonS3 amazonS3) {
        return new StorageBlobServiceImpl(
                amazonS3,
                properties.getBucketName(),
                properties.getServiceName(),
                properties.getExpireTime()
        );
    }

    @Bean
    public String initializeDatabase(JdbcTemplate jdbcTemplate) {
        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS storage_blob (id bigint auto_increment primary key, `key` varchar(255) not null, filename varchar(255) null, content_type varchar(255) null, metadata json null, service_name varchar(255) null, byte_size bigint null, checksum varchar(255) null, created_at datetime null, constraint storage_blob_key_uindex unique (`key`));");
        return "ok";
    }
}
