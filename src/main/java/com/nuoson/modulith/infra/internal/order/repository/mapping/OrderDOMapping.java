package com.nuoson.modulith.infra.internal.order.repository.mapping;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

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
    @Mapping(target = "name", ignore = true)
    OrderEntity toEntity(ProductOrderDO doObj);

    /**
     * entity to do
     *
     * @param entity
     * @return
     */
    @Mapping(target = "id", ignore = true)
    ProductOrderDO fromEntity(OrderEntity entity);
}