package com.nuoson.modulith.app.inventory;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import com.nuoson.modulith.app.foundationmodel.BasicResultDTO;
import com.nuoson.modulith.app.internal.ExecutorArgsValidationGroup;
import com.nuoson.modulith.app.inventoryrequestparam.InventoryByCountQuery;
import com.nuoson.modulith.app.inventoryrequestparam.InventoryByIdQuery;
import com.nuoson.modulith.domain.inventory.InventoryRepositoryGateway;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Validated(ExecutorArgsValidationGroup.class)

public class InventoryExecutor {

    // #region [Variables] -- variables
    /**
     * 使用 InventoryRepositoryGateway 来访问存储库
     */
    private final InventoryRepositoryGateway inventoryRepositoryGateway;

    /**
     * 使用 InventoryDTOMapping 来将DTO映射到实体类
     */
    private final InventoryDTOMapping inventoryDTOMapping;
    // #endregion

    /**
     * 根据id查询
     * 
     * @param query
     * @return
     */
    public BasicResultDTO<InventoryDTO> getById(InventoryByIdQuery query) {

        return BasicResultDTO.success(inventoryDTOMapping.fromEntity(
                inventoryRepositoryGateway.getById(query.getProductId())));
    }

    /**
     * 根据指定数量进行查询
     * 
     * @param query
     * @return
     */
    public BasicResultDTO<List<InventoryDTO>> queryByCount(
            @Valid InventoryByCountQuery query) {
        log.info("queryByCount from: {} to: {}", query.getFrom(), query.getTo());
        return BasicResultDTO.success(inventoryDTOMapping.fromEntity(
                inventoryRepositoryGateway.queryByCount(query.getFrom(), query.getTo())));
    }
}
