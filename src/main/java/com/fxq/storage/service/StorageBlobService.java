package com.fxq.storage.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fxq.storage.entity.StorageBlob;

import java.io.File;

/**
 * Created by xiangqing'fan on 2024/03/29
 */
public interface StorageBlobService extends IService<StorageBlob> {
    StorageBlob createAndUpload(File file);

    String permanentUrl(StorageBlob blob);

    String temporaryUrl(StorageBlob blob);

    StorageBlob findByKey(String key);

    StorageBlob deleteById(Long id);

    String redirectUrl(StorageBlob blob);
}
