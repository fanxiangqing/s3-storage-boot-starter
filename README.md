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

    表结构:
        create table storage_blob
        (
        id           bigint auto_increment
        primary key,
        `key`        varchar(255) not null,
        filename     varchar(255) null,
        content_type varchar(255) null,
        metadata     json         null,
        service_name varchar(255) null,
        byte_size    bigint       null,
        checksum     varchar(255) null,
        created_at   datetime     null,
        constraint storage_blob_key_uindex
        unique (`key`)
        );
    备注: 无需自主创建，配置数据源，启动 SpringBoot 项目自动创建

### 3. 使用

    @Resource
    private StorageBlobService storageBlobService;
    1. 上传对象
        StorageBlob blob = storageBlobService.createAndUpload(new File("header.png"));
    2. 获取临时访问链接
        storageBlobService.temporaryUrl(blob) // bolb为 StorageBlob类型
    3. 获取永久访问链接
        storageBlobService.permanentUrl(blob) // bolb为 StorageBlob类型
    4. 删除对象
        StorageBlob blob = storageBlobService.deleteById(id); // id为 Long类型
    5. 获取对象重定向地址
        String redirectUrl = storageBlobService.redirectUrl(blob) // bolb为 StorageBlob类型
        redirectUrl: /storage/redirect/36d2567930e54f6f8d1fadaf63c044e7

### 4. 内置重定向地址控制器

    直接访问获取的重定向地址(由服务地址拼接获取对象重定向地址拼接获得):
        例如: http://localhost:8080/storage/redirect/36d2567930e54f6f8d1fadaf63c044e7