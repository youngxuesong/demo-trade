-- 流程定义表
CREATE TABLE IF NOT EXISTS process_definition (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    process_code VARCHAR(64) NOT NULL COMMENT '流程编码',
    process_name VARCHAR(128) NOT NULL COMMENT '流程名称',
    process_version INT NOT NULL DEFAULT 1 COMMENT '流程版本号',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    description VARCHAR(512) COMMENT '流程描述',
    creator VARCHAR(64) NOT NULL COMMENT '创建人',
    modifier VARCHAR(64) COMMENT '修改人',
    gmt_create DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    gmt_modified DATETIME ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    UNIQUE KEY uk_code_version (process_code, process_version)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='流程定义表';

-- 流程节点表
CREATE TABLE IF NOT EXISTS process_node (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    process_definition_id BIGINT NOT NULL COMMENT '流程定义ID',
    node_code VARCHAR(64) NOT NULL COMMENT '节点编码',
    node_name VARCHAR(128) NOT NULL COMMENT '节点名称',
    node_type VARCHAR(32) NOT NULL COMMENT '节点类型：START-开始节点，END-结束节点，TASK-任务节点，GATEWAY-网关节点',
    next_nodes VARCHAR(512) COMMENT '下一节点列表，多个节点用逗号分隔',
    config JSON COMMENT '节点配置，JSON格式',
    description VARCHAR(512) COMMENT '节点描述',
    creator VARCHAR(64) NOT NULL COMMENT '创建人',
    modifier VARCHAR(64) COMMENT '修改人',
    gmt_create DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    gmt_modified DATETIME ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    UNIQUE KEY uk_definition_code (process_definition_id, node_code),
    FOREIGN KEY (process_definition_id) REFERENCES process_definition(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='流程节点表';

-- 流程实例表
CREATE TABLE IF NOT EXISTS process_instance (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    process_definition_id BIGINT NOT NULL COMMENT '流程定义ID',
    business_key VARCHAR(64) NOT NULL COMMENT '业务标识',
    current_node_code VARCHAR(64) NOT NULL COMMENT '当前节点编码',
    status VARCHAR(32) NOT NULL COMMENT '实例状态：RUNNING-运行中，COMPLETED-已完成，TERMINATED-已终止',
    start_time DATETIME NOT NULL COMMENT '开始时间',
    end_time DATETIME COMMENT '结束时间',
    creator VARCHAR(64) NOT NULL COMMENT '创建人',
    modifier VARCHAR(64) COMMENT '修改人',
    gmt_create DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    gmt_modified DATETIME ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    UNIQUE KEY uk_business_key (business_key),
    FOREIGN KEY (process_definition_id) REFERENCES process_definition(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='流程实例表';

-- 流程变量表
CREATE TABLE IF NOT EXISTS process_variable (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    process_instance_id BIGINT NOT NULL COMMENT '流程实例ID',
    variable_name VARCHAR(64) NOT NULL COMMENT '变量名称',
    variable_value TEXT NOT NULL COMMENT '变量值',
    variable_type VARCHAR(32) NOT NULL COMMENT '变量类型',
    creator VARCHAR(64) NOT NULL COMMENT '创建人',
    modifier VARCHAR(64) COMMENT '修改人',
    gmt_create DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    gmt_modified DATETIME ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    UNIQUE KEY uk_instance_name (process_instance_id, variable_name),
    FOREIGN KEY (process_instance_id) REFERENCES process_instance(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='流程变量表';