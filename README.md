### 1. application.yml 配置

    service-name: 服务名称(可选)
    bucket-name: 桶名称
    access-key: ""
    secret-key: ""
    endpoint: ""
    region: ""
    expire-time: 临时url链接有效时长单位毫秒(可选),默认: 600000

### 2. 启动时会自动创建 storage_blob 数据表

### 3. 使用

    @Resource
    private StorageBlobService storageBlobService;
    1. 上传
        storageBlobService.createAndUpload(new File("header.png"));
    2. 获取临时访问链接
        storageBlobService.temporaryUrl(blob) // bolb为 StorageBlob类型
    3. 获取永久访问链接
        storageBlobService.permanentUrl(blob) // bolb为 StorageBlob类型 

### 4. 重定向控制器

    /storage/redirect/{key}