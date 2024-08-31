package com.nuoson.modulith.infra.internal.order.repository.mapping;

import org.mapstruct.Mapper;

import com.nuoson.modulith.domain.ordermodel.OrderEntity;
import com.nuoson.modulith.infra.internal.order.repository.model.ProductOrderDO;

@Mapper(componentModel = "spring")
@SuppressWarnings("PMD.ClassNamingShouldBeCamelRule")
public interface OrderDOMapping {
    /**
     * from do to entity
     * 
     * @param doObj
     * @return
     */
    OrderEntity toEntity(ProductOrderDO doObj);

    /**
     * entity to do
     * 
     * @param entity
     * @return
     */
    ProductOrderDO fromEntity(OrderEntity entity);
}