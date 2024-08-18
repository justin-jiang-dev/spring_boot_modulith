package com.nuoson.modulith.app.order;

import org.mapstruct.Mapper;

import com.nuoson.modulith.domain.ordermodel.OrderEntity;

@Mapper(componentModel = "spring")
@SuppressWarnings("PMD.ClassNamingShouldBeCamelRule")
public interface OrderDTOMapping {
    /**
     * from entity to dto
     * 
     * @param entity
     * @return
     */
    OrderDTO fromEntity(OrderEntity entity);

}
