package com.nuoson.modulith.infra.internal.order.repository.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 
 * </p>
 * ### 自动生成代码，请勿修改 ###
 * @author nuoson auto
 * @since 2024-09-04 22:56:46
 */
@Getter
@Setter
@TableName("PRODUCT_ORDER")
@SuppressWarnings("PMD.PojoMustOverrideToStringRule")
public class ProductOrderDO {

    /**
     * 主键ID
     */
    @TableId(value = "ID", type = IdType.AUTO)
    private Long id;

    /**
     * 订单ID
     */
    @TableField("ORDER_ID")
    private String orderId;

    /**
     * 对应的库存产品ID
     */
    @TableField("PRODUCT_ID")
    private String productId;

    /**
     * 下定的库存产品数量
     */
    @TableField("COUNT")
    private Integer count;
}