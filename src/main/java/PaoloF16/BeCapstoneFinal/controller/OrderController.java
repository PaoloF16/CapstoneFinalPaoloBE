package PaoloF16.BeCapstoneFinal.controller;

import PaoloF16.BeCapstoneFinal.dto.CheckoutRequestDTO;
import PaoloF16.BeCapstoneFinal.dto.CreateOrderDTO;
import PaoloF16.BeCapstoneFinal.dto.OrderItemRequestDTO;
import PaoloF16.BeCapstoneFinal.entities.Order;
import PaoloF16.BeCapstoneFinal.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping
    public Order createOrder(@RequestBody CreateOrderDTO dto) {
        return orderService.createOrder(dto);
    }

    @GetMapping("/table/{tableId}")
    public Order getActiveOrderByTable(@PathVariable UUID tableId) {
        return orderService.getActiveOrderByTableId(tableId);
    }

    @PutMapping("/{id}/items")
    public Order updateOrderItems(@PathVariable UUID id, @RequestBody List<OrderItemRequestDTO> items) {
        return orderService.updateOrderItems(id, items);
    }

    @PostMapping("/{id}/checkout")
    public Order checkoutOrder(@PathVariable UUID id, @RequestBody(required = false) CheckoutRequestDTO checkoutDto) {
        return orderService.checkoutOrder(id, checkoutDto);
    }
}