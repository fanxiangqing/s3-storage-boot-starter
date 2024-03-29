package com.fxq.storage.service.impl;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fxq.storage.entity.StorageBlob;
import com.fxq.storage.mapper.StorageBlobMapper;
import com.fxq.storage.service.StorageBlobService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

/**
 * Created by xiangqing'fan on 2024/03/29
 */
public class StorageBlobServiceImpl extends ServiceImpl<StorageBlobMapper, StorageBlob> implements StorageBlobService {
    private final Logger logger = LoggerFactory.getLogger(StorageBlobServiceImpl.class);

    private final AmazonS3 amazonS3;

    private final String bucketName;

    private final String serviceName;

    private final Long expireTime;

    public StorageBlobServiceImpl(AmazonS3 amazonS3, String bucketName, String serviceName, Long expireTime) {
        this.amazonS3 = amazonS3;
        this.bucketName = bucketName;
        this.serviceName = serviceName;
        this.expireTime = expireTime;
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
        PutObjectResult result = amazonS3.putObject(this.bucketName, key, file);
        StorageBlob blob = new StorageBlob();
        blob.setKey(key);
        blob.setServiceName(serviceName);
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
        return amazonS3.getUrl(bucketName, blob.getKey()).toString();
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
                bucketName,
                blob.getKey(),
                new Date(System.currentTimeMillis() + expireTime)
        ).toString();
    }

    @Override
    public StorageBlob findByKey(String key) {
        return this.lambdaQuery()
                .eq(StorageBlob::getKey, key)
                .last("limit 1")
                .one();
    }
}
