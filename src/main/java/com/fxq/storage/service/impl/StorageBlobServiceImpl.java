package com.fxq.storage.service.impl;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fxq.storage.autoconfigure.StorageProperties;
import com.fxq.storage.entity.StorageBlob;
import com.fxq.storage.mapper.StorageBlobMapper;
import com.fxq.storage.service.StorageBlobService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Created by xiangqing'fan on 2024/03/29
 */
public class StorageBlobServiceImpl extends ServiceImpl<StorageBlobMapper, StorageBlob> implements StorageBlobService {
    private final Logger logger = LoggerFactory.getLogger(StorageBlobServiceImpl.class);

    private final AmazonS3 amazonS3;

    private final StorageProperties properties;

    @Value("${spring.mvc.servlet.path:}")
    private String routePrefix = "";

    public StorageBlobServiceImpl(AmazonS3 amazonS3, StorageProperties properties) {
        this.amazonS3 = amazonS3;
        this.properties = properties;
    }

    @Override
    public StorageBlob createAndUpload(File file) {
        if (file == null) {
            throw new RuntimeException("The uploaded file is empty");
        }
        String contentType = "";
        try {
            contentType = Files.probeContentType(file.getAbsoluteFile().toPath());
        } catch (IOException e) {
            logger.warn("Unknown file type");
        }
        String key = UUID.randomUUID().toString().replaceAll("-", "");
        PutObjectResult result = amazonS3.putObject(properties.getBucketName(), key, file);
        StorageBlob blob = new StorageBlob();
        blob.setKey(key);
        blob.setServiceName(properties.getServiceName());
        blob.setFilename(file.getName());
        blob.setContentType(contentType);
        blob.setByteSize(file.length());
        blob.setChecksum(result.getContentMd5());
        blob.setCreatedAt(LocalDateTime.now());

        if (!this.save(blob)) {
            throw new RuntimeException("Upload File failed .");
        }

        return blob;
    }

    @Override
    public String permanentUrl(StorageBlob blob) {
        if (blob == null) {
            throw new RuntimeException("The StorageBlob is empty");
        }
        if (StringUtils.isEmpty(blob.getKey())) {
            throw new RuntimeException("The StorageBlob Key is empty");
        }
        return amazonS3.getUrl(properties.getBucketName(), blob.getKey()).toString();
    }

    @Override
    public String temporaryUrl(StorageBlob blob) {
        if (blob == null) {
            throw new RuntimeException("The StorageBlob is empty");
        }
        if (StringUtils.isEmpty(blob.getKey())) {
            throw new RuntimeException("The StorageBlob Key is empty");
        }
        return amazonS3.generatePresignedUrl(
                properties.getBucketName(),
                blob.getKey(),
                new Date(System.currentTimeMillis() + properties.getExpireTime())
        ).toString();
    }

    @Override
    public StorageBlob findByKey(String key) {
        return this.lambdaQuery()
                .eq(StorageBlob::getKey, key)
                .last("limit 1")
                .one();
    }

    @Override
    public StorageBlob deleteById(Long id) {
        StorageBlob blob = this.getById(id);
        if (blob == null) {
            throw new RuntimeException("The StorageBlob is empty");
        }
        if (!this.removeById(id)) {
            throw new RuntimeException("Delete StorageBlob failed .");
        }
        amazonS3.deleteObject(properties.getBucketName(), blob.getKey());
        return blob;
    }

    @Override
    public String redirectUrl(StorageBlob blob) {
        if (blob == null) {
            throw new RuntimeException("The StorageBlob is empty");
        }
        return String.format("%s/storage/redirect/%s", routePrefix, blob.getKey());
    }

    @Override
    public boolean removeById(StorageBlob blob) {
        if (blob == null) {
            throw new RuntimeException("The StorageBlob is empty");
        }
        amazonS3.deleteObject(properties.getBucketName(), blob.getKey());
        return super.removeById(blob);
    }

    @Override
    public boolean removeByMap(Map<String, Object> columnMap) {
        this.listByMap(columnMap).forEach(blob -> amazonS3.deleteObject(properties.getBucketName(), blob.getKey()));
        return super.removeByMap(columnMap);
    }

    @Override
    public boolean remove(Wrapper<StorageBlob> queryWrapper) {
        this.list(queryWrapper).forEach(blob -> amazonS3.deleteObject(properties.getBucketName(), blob.getKey()));
        return super.remove(queryWrapper);
    }

    @Override
    public boolean removeByIds(Collection<?> list, boolean useFill) {
        return super.removeByIds(list, useFill);
    }

    @Override
    public boolean removeBatchByIds(Collection<?> list) {
        return super.removeBatchByIds(list);
    }

    @Override
    public boolean removeBatchByIds(Collection<?> list, boolean useFill) {
        return super.removeBatchByIds(list, useFill);
    }
}
