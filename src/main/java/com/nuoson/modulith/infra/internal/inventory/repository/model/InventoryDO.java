package com.nuoson.modulith.infra.internal.inventory.repository.model;

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
 * @since 2024-08-31 14:18:25
 */
@Getter
@Setter
@TableName("INVENTORY")
@SuppressWarnings("PMD.PojoMustOverrideToStringRule")
public class InventoryDO {

    /**
     * 主键ID
     */
    @TableId(value = "ID", type = IdType.AUTO)
    private Long id;

    /**
     * 产品ID
     */
    @TableField("PRODUCT_ID")
    private String productId;

    /**
     * 产品名称
     */
    @TableField("NAME")
    private String name;

    /**
     * 库存产品数量
     */
    @TableField("COUNT")
    private Integer count;
}