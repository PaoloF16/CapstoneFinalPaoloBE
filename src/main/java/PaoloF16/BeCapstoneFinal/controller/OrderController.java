// src/main/java/PaoloF16/BeCapstoneFinal/controller/OrderController.java
package PaoloF16.BeCapstoneFinal.controller;

import PaoloF16.BeCapstoneFinal.entities.Order;
import PaoloF16.BeCapstoneFinal.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "http://localhost:5173")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping("/table/{tableId}")
    public Order getActiveOrderByTable(@PathVariable UUID tableId) {
        return orderService.getActiveOrderByTableId(tableId);
    }

    @PostMapping
    public Order createOrder(@RequestBody Map<String, Object> body) {
        UUID tableId = UUID.fromString(body.get("tableId").toString());
        List<Map<String, Object>> items = (List<Map<String, Object>>) body.get("items");
        return orderService.createOrder(tableId, items);
    }

    @PutMapping("/{id}/items")
    public Order updateOrderItems(@PathVariable UUID id, @RequestBody List<Map<String, Object>> items) {
        return orderService.updateOrderItems(id, items);
    }

    @PostMapping("/{id}/checkout")
    public Order checkoutOrder(@PathVariable UUID id, @RequestBody Map<String, Object> checkoutData) {
        return orderService.checkoutOrder(id, checkoutData);
    }
}