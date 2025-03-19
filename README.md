# Demo Trade Flow Orchestration Service

## 项目概述

Demo Trade是一个交易流程编排服务，用于管理和协调交易相关的业务流程。该项目基于Spring Boot和Spring Cloud构建，集成了Nacos服务发现与配置中心、Seata分布式事务以及Redis分布式锁等技术，旨在提供高可用、高性能的交易处理能力。

## 技术栈

- **基础框架**：Spring Boot 3.2.1, Spring Cloud 2023.0.0
- **服务治理**：Alibaba Nacos (服务发现与配置中心)
- **分布式事务**：Seata
- **数据存储**：MySQL, Spring Data JPA
- **分布式锁**：Redis, Redisson
- **Java版本**：JDK 17

## 功能特点

- 服务注册与发现
- 集中化配置管理
- 分布式事务处理
- 分布式锁实现
- RESTful API接口
- 健康检查与监控

## 环境要求

- JDK 17+
- Maven 3.6+
- MySQL 8.0+
- Redis 6.0+
- Nacos 2.x
- Seata 1.6+

## 快速开始

### 1. 环境准备

确保已安装并启动以下服务：
- MySQL
- Redis
- Nacos
- Seata

### 2. 配置修改

根据实际环境修改配置文件：
- `application.yml`
- `bootstrap.yml`

### 3. 构建与运行

```bash
# 克隆项目
git clone https://your-repository/demo-trade.git

# 进入项目目录
cd demo-trade

# 编译打包
mvn clean package

# 运行应用
java -jar target/demo-trade-0.0.1-SNAPSHOT.jar