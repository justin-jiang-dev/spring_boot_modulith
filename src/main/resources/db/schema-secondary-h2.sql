--  此文件用于测试多数据源中的 secondaryH2
DROP TABLE IF EXISTS inventory;

CREATE TABLE inventory (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    product_id VARCHAR(32) NOT NULL COMMENT '产品ID',
    name VARCHAR(30) NULL DEFAULT NULL COMMENT '产品名称',
    count INT NULL DEFAULT NULL COMMENT '库存产品数量',
    PRIMARY KEY (id)
);

/**
 -- 产品订单
 **/
DROP TABLE IF EXISTS product_order;

CREATE TABLE product_order (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    order_id VARCHAR(32) NOT NULL COMMENT '订单ID',
    product_id VARCHAR(32) NOT NULL COMMENT '对应的库存产品ID',
    count INT NULL DEFAULT NULL COMMENT '下定的库存产品数量',
    PRIMARY KEY (id)
);