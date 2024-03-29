### 1. application.yml 配置

    // 数据源配置
    spring.datasource.url: jdbc:mysql://localhost:3306/test?characterEncoding=utf-8&serverTimezone=GMT%2B8
    spring.datasource.username: root
    spring.datasource.password: root

    // 存储配置
    storage.service-name: 服务名称(可选)
    storage.bucket-name: 桶名称
    storage.access-key: ""
    storage.secret-key: ""
    storage.endpoint: ""
    storage.region: ""
    storage.expire-time: 临时url链接有效时长单位毫秒(可选),默认: 600000

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