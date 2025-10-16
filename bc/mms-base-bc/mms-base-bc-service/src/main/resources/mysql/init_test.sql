CREATE TABLE test
(
    id          bigint auto_increment comment '主键'
        primary key,
    title       varchar(512) not null default '' comment '测试标题',
    content     text         comment '测试内容',
    create_time datetime     not null default CURRENT_TIMESTAMP comment '创建时间',
    update_time datetime     not null default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP comment '更新时间',
    INDEX idx_title (title),
    INDEX idx_create_time (create_time)
)
    CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci
    comment '测试表-用于测试服务基础功能';

