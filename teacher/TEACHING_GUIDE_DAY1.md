# 第 1 天教学讲义（约 20 分钟）

> 适合一起阅读与动手。目标：认识项目、准备环境、在 IDE 中成功导入并能看到各模块。

## 今日目标
- 知道项目做什么：一个基于 Spring Boot 3 + Spring Cloud 的微服务管理系统。
- 了解整体模块构成与职责。
- 搭建本地环境（JDK17、Maven、MySQL、Redis、Nacos）。
- 用 IDE 成功导入项目，能浏览代码结构。

## 课程节奏（20 分钟）
1. 5 分钟：整体介绍与模块地图。
2. 10 分钟：环境准备与克隆/导入项目。
3. 5 分钟：在 IDE 中浏览项目结构与关键文档。

## 预备知识（她需要的）
- 会基本的 Spring Boot 启动与查看 `application.yml`。
- 会使用 Git 克隆仓库，知道 Maven 的基本概念。
- 了解 MySQL、Redis、Nacos 的用途（不知道也没关系，下面有白话解释）。

## 白话速通
- 微服务 = 商场的多家店铺；网关是入口安检+导览；公共模块是共享水电。
- Redis = 小账本，记黑名单、登录失败次数、Refresh Token 唯一性。
- Nacos = 配置大本营 + 服务通讯录。

## 环境准备清单（先装好或对照确认）
- JDK 17（必需）  
- Maven 3.6+  
- MySQL 8.0+（导入 `mysql/init_mms_dev_core.sql`）  
- Redis（本地开启即可）  
- Nacos（本地或远端都行，需能拉取配置）  
- IDE：IntelliJ IDEA（推荐）  
- Git：用于克隆仓库  

## Step-by-Step（边讲边做）

### 1) 克隆项目（2 分钟）
```bash
git clone https://github.com/city-of-star/MyManagementSystem.git
cd MyManagementSystem/bc
```
提示：若已在本地，可跳过。

### 2) IDE 导入（5 分钟）
- 用 IntelliJ IDEA 打开 `bc` 目录（这是多模块 Maven 项目）。  
- 等待 Maven 同步完成（如遇下载慢，可配置国内镜像）。

### 3) 快速浏览目录（5 分钟）
- `README.md`：项目简介、模块说明、技术栈。
- `mms-common-bc/`：公共能力（core、web-mvc、security、database）。
- `mms-usercenter-bc/`：登录与用户中心（controller/service/common/server）。
- `mms-gateway-bc/`：网关与过滤器（JWT/TraceId/ClientIp/白名单/统一异常）。
- `mms-base-bc/`：基础数据模块（可扩展业务）。
- `mysql/init_mms_dev_core.sql`：10 张表的初始化 SQL；`mysql/最终审查报告.md`：表设计点评。
- `网关与登录服务最佳实践评估报告.md`：现状与改进方向。

### 4) 环境服务检查（3 分钟）
- MySQL：确认能连接，准备导入 SQL（Day2 再实际导入）。
- Redis：确认本地可启动。
- Nacos：确认能访问控制台（默认 8848），Day2/Day3 会用到配置。

### 5) 小结与提问（5 分钟）
- 让她复述：项目有哪几个大模块、网关负责什么、Redis/Nacos 各做什么。
- 预告 Day2：深入公共模块（统一响应体、异常、Security、日志/TraceId）。

## 课后（可选 5 分钟内）
- 在 IDE 中定位 `mms-usercenter-bc-controller` 的登录接口，看看参数和返回值；下节会详细讲。
- 阅读 `README.md` 的“项目结构”一节，标记不懂的词，下节集中答疑。

## 故障排查速记
- Maven 同步慢：配置阿里云镜像或本地仓库。
- JDK 版本不对：确认 Project SDK 选择 17。
- Git 克隆失败：检查网络或使用代理。

## 期待成果
- 你们俩都能在 IDE 里看到完整的多模块项目结构。
- 对“网关/用户中心/公共模块”的角色有直观认识。
- 环境组件（MySQL/Redis/Nacos）已准备就绪或清楚如何启动。

