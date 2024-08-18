package com.nuoson.modulith.adapter.inventory;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nuoson.modulith.app.foundationmodel.BasicResultDTO;
import com.nuoson.modulith.app.inventory.InventoryDTO;
import com.nuoson.modulith.app.inventory.InventoryExecutor;
import com.nuoson.modulith.app.inventoryrequestparam.InventoryByCountQuery;
import com.nuoson.modulith.app.inventoryrequestparam.InventoryByIdQuery;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;

@Tag(name = "库存 数据库 API 接口")
@RestController
@RequestMapping("inventory/database")
@RequiredArgsConstructor
public class InventoryDatabaseController {
    private final InventoryExecutor inventoryExecutor;

    @Operation(description = "根据库存产品Id, 查询库存信息")
    @PostMapping("getById")
    public BasicResultDTO<InventoryDTO> getById(@Valid @RequestBody InventoryByIdQuery query) {
        // @Valid作用：指定对 Java 对象（query）进行约束验证
        // @RequestBody 的作用： 将请求中 Body 内容绑定（反序列化）到 Java 对象（query）
        return inventoryExecutor.getById(query);
    }

    @Operation(description = "根据库存产品Id, 查询库存信息")
    @GetMapping("getByIdWithParam")
    public BasicResultDTO<InventoryDTO> getByIdWithParam(
            @RequestParam(value = "id", required = true) @NotBlank String id) {
        InventoryByIdQuery query = new InventoryByIdQuery();
        query.setProductId(id);
        return inventoryExecutor.getById(query);
    }

    /**
     * 
     * 注：在 SpringBoot框架的 RequestResponseBodyMethodProcessor.resolveArgument 方法中 <br>
     * 会根据注解，调用 jakarta.validation.Validator.validate 方法， <br>
     * 就是说不需要引入 @Validated 注解进行触发，当然如果需要按 Group 进行触发，还是需要 @Validated <br>
     * 如果验证不通过，会抛出
     * {@link org.springframework.web.bind.MethodArgumentNotValidException} <br>
     * 
     * @param query
     * @return
     */
    @Operation
    @PostMapping("queryByCount")
    public BasicResultDTO<List<InventoryDTO>> queryByCount(
            // Valid 注解会被SpringBoot框架解析，并触发约束验证
            @Valid @RequestBody InventoryByCountQuery query) {
        return inventoryExecutor.queryByCount(query);
    }

}
