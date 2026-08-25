package PaoloF16.BeCapstoneFinal.controller;

import PaoloF16.BeCapstoneFinal.entities.Order;
import PaoloF16.BeCapstoneFinal.enums.OrderStatus;
import PaoloF16.BeCapstoneFinal.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

    // 💡 Obtener consumos consolidados de una mesa activa
    @GetMapping("/table/{tableId}")
    public ResponseEntity<?> getActiveOrderByTable(@PathVariable UUID tableId) {
        Order order = orderService.getActiveOrderByTableId(tableId);
        return ResponseEntity.ok(order != null ? order : Map.of("items", List.of()));
    }

    // 💡 Solo Cocina y Administradores pueden ver la lista del KDS
    @GetMapping("/kitchen")
    @PreAuthorize("hasAuthority('KITCHEN_READ') or hasRole('COOK') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<List<Order>> getKitchenOrders() {
        return ResponseEntity.ok(orderService.getKitchenOrders());
    }

    // 💡 Solo Meseros y Administradores pueden crear órdenes / comandas
    @PostMapping
    @PreAuthorize("hasAuthority('ORDERS_CREATE') or hasRole('WAITER') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<Order> createOrder(@RequestBody Map<String, Object> body) {
        UUID tableId = UUID.fromString(body.get("tableId").toString());
        List<Map<String, Object>> items = (List<Map<String, Object>>) body.get("items");
        return ResponseEntity.ok(orderService.createOrder(tableId, items));
    }

    // 💡 Cocineros y Administradores pueden marcar platos como listos
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAuthority('KITCHEN_MANAGE') or hasRole('COOK') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> updateOrderStatus(@PathVariable UUID id, @RequestBody Map<String, String> body) {
        String statusStr = body.get("status");
        if (statusStr == null || statusStr.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("message", "El campo 'status' es obligatorio"));
        }
        OrderStatus status = OrderStatus.valueOf(statusStr.trim().toUpperCase());
        orderService.updateOrderStatus(id, status);
        return ResponseEntity.ok(Map.of("message", "Comanda actualizada a " + status.name()));
    }

    // 💡 Cobrar comandas y liberar la mesa automáticamente
    @PostMapping("/{id}/checkout")
    @PreAuthorize("hasAuthority('ORDERS_CHECKOUT') or hasRole('CASHIER') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> checkoutOrder(@PathVariable UUID id, @RequestBody(required = false) Map<String, Object> checkoutData) {
        orderService.checkoutTableOrders(id, checkoutData != null ? checkoutData : Map.of());
        return ResponseEntity.ok(Map.of("message", "Cuenta cobrada y mesa liberada"));
    }
}