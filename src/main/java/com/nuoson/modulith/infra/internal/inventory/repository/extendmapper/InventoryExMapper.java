package com.nuoson.modulith.infra.internal.inventory.repository.extendmapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.context.annotation.Primary;

import com.nuoson.modulith.infra.internal.inventory.repository.mapper.InventoryMapper;
import com.nuoson.modulith.infra.internal.inventory.repository.model.InventoryDO;

/**
 * 继承自自动生成的 Mapper 接口， 用于扩展自定义的 SQL 语句
 */
@Mapper
@Primary
public interface InventoryExMapper extends InventoryMapper {

    /**
     * 根据 id 查询一条记录
     * 
     * @param id
     * @return
     */
    @Select("SELECT id, name FROM users WHERE id = #{id}")
    public InventoryDO queryOne(String id);
}
