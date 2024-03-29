package com.fxq.storage.controller;

import com.fxq.storage.entity.StorageBlob;
import com.fxq.storage.service.StorageBlobService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by xiangqing'fan on 2024/03/29
 */
@RestController
@RequestMapping("storage")
public class StorageController {

    private final StorageBlobService storageBlobService;

    public StorageController(StorageBlobService storageBlobService) {
        this.storageBlobService = storageBlobService;
    }

    @GetMapping("redirect/{key}")
    public void redirect(@PathVariable("key") String key, HttpServletResponse response) throws IOException {
        StorageBlob blob = storageBlobService.findByKey(key);
        response.sendRedirect(storageBlobService.temporaryUrl(blob));
    }
}
