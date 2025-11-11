# MMS 管理系统

一个基于Spring Boot 3.2.4和Spring Cloud的微服务管理系统项目（目前正在开发中...）

## 项目简介

MMS（Management System）是一个企业级管理系统，采用微服务架构设计，提供用户管理、基础数据管理等功能模块

## 技术栈

- **Java**: 17
- **Spring Boot**: 3.2.4
- **Spring Cloud**: 2023.0.1
- **Spring Cloud Alibaba**: 2023.0.1.0
- **MyBatis Plus**: 3.5.7
- **MySQL**: 8.0.33
- **Maven**: 3.x

## 项目结构

```
bc/
├── mms-common-bc/                  # 公共模块
│   ├── mms-common-bc-core/         # 核心工具类
│   ├── mms-common-bc-web-common/   # Web通用模块（Response等纯POJO，无MVC依赖）
│   ├── mms-common-bc-web-mvc/      # Web MVC模块（全局异常处理器、Swagger配置）
│   ├── mms-common-bc-database/     # 数据库配置
│   └── mms-common-bc-all/          # Common模块聚合包
├── mms-base-bc/                # 基础数据模块
│   ├── mms-base-bc-common/     # 公共组件
│   ├── mms-base-bc-controller/ # 控制器层
│   ├── mms-base-bc-feign-api/  # Feign接口
│   ├── mms-base-bc-server/     # 服务启动类
│   └── mms-base-bc-service/    # 业务逻辑层
└── mms-usercenter-bc/          # 用户中心模块
    ├── mms-usercenter-bc-common/     # 公共组件
    ├── mms-usercenter-bc-controller/ # 控制器层
    ├── mms-usercenter-bc-feign-api/  # Feign接口
    ├── mms-usercenter-bc-server/     # 服务启动类
    └── mms-usercenter-bc-service/    # 业务逻辑层
```

## 环境要求

- JDK 17+
- Maven 3.6+
- MySQL 8.0+

## 快速开始

### 1. 克隆项目

```bash
git clone https://github.com/city-of-star/MyManagementSystem.git
```

## 开发说明

- 项目采用Maven多模块管理
- 使用Spring Cloud进行微服务治理
- 数据库操作使用MyBatis Plus
- 接口调用使用OpenFeign

## 联系方式

- 开发团队：MMS开发团队
- 邮箱：2825646787@qq.com

## 许可证

本项目采用 [MIT License](LICENSE) 许可证
