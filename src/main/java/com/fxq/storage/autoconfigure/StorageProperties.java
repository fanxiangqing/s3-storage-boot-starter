package com.fxq.storage.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Created by xiangqing'fan on 2024/03/28
 */
@ConfigurationProperties(
        prefix = "storage"
)
public class StorageProperties {
    private String accessKey;
    private String secretKey;
    private String endpoint;
    private String region;
    private String bucketName;
    private String serviceName;
    private Long expireTime = 600000L;

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public Long getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(Long expireTime) {
        this.expireTime = expireTime;
    }

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }


}
