package com.nuoson.modulith.adapter.order;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nuoson.modulith.app.foundationmodel.BasicResultDTO;
import com.nuoson.modulith.app.order.OrderCreationCommand;
import com.nuoson.modulith.app.order.OrderDTO;
import com.nuoson.modulith.app.order.OrderExecutor;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * 提供 REDIS 访问样例
 */
@Tag(name = "订单 REDIS API 接口")
@RestController
@RequestMapping("order/redis")
@RequiredArgsConstructor
public class OrderRedisController {
    private final OrderExecutor orderExecutor;

    @Operation(description = "创建订单")
    @PostMapping("save")
    public BasicResultDTO<OrderDTO> save(@Valid @RequestBody OrderCreationCommand command) {
        // @Valid作用：指定对 Java 对象（query）进行约束验证
        // @RequestBody 的作用： 将请求中 Body 内容绑定（反序列化）到 Java 对象（query）
        return orderExecutor.save(command);
    }

    @Operation(description = "根据 id 获取订单")
    @PostMapping("getById")
    public BasicResultDTO<OrderDTO> getById(@Valid @RequestBody OrderCreationCommand command) {
        // @Valid作用：指定对 Java 对象（query）进行约束验证
        // @RequestBody 的作用： 将请求中 Body 内容绑定（反序列化）到 Java 对象（query）
        return orderExecutor.save(command);
    }
}
