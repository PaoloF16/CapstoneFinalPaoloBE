// src/main/java/PaoloF16/BeCapstoneFinal/controller/OrderController.java
package PaoloF16.BeCapstoneFinal.controller;

import PaoloF16.BeCapstoneFinal.entities.Order;
import PaoloF16.BeCapstoneFinal.enums.OrderStatus;
import PaoloF16.BeCapstoneFinal.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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

    @GetMapping("/kitchen")
    public ResponseEntity<List<Order>> getKitchenOrders() {
        return ResponseEntity.ok(orderService.getKitchenOrders());
    }

    @GetMapping("/table/{tableId}")
    public ResponseEntity<Order> getActiveOrderByTable(@PathVariable UUID tableId) {
        return ResponseEntity.ok(orderService.getActiveOrderByTableId(tableId));
    }

    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody Map<String, Object> body) {
        UUID tableId = UUID.fromString(body.get("tableId").toString());
        List<Map<String, Object>> items = (List<Map<String, Object>>) body.get("items");
        return ResponseEntity.ok(orderService.createOrder(tableId, items));
    }

    // 💡 ENDPOINT OPTIMIZADO: Retorna respuesta ligera para evitar fallos de Lazy Loading en Jackson
    @PatchMapping("/{id}/status")
    public ResponseEntity<?> updateOrderStatus(@PathVariable UUID id, @RequestBody Map<String, String> body) {
        try {
            String statusStr = body.get("status");
            if (statusStr == null || statusStr.isBlank()) {
                return ResponseEntity.badRequest().body(Map.of("message", "El campo 'status' es obligatorio"));
            }

            OrderStatus status = OrderStatus.valueOf(statusStr.trim().toUpperCase());
            orderService.updateOrderStatus(id, status);

            return ResponseEntity.ok(Map.of("message", "Comanda actualizada a " + status.name()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", "Estado inválido: " + body.get("status")));
        } catch (Exception e) {
            e.printStackTrace(); // 👈 Imprime la traza completa en la consola de IntelliJ/Eclipse
            return ResponseEntity.internalServerError().body(Map.of("message", e.getMessage() != null ? e.getMessage() : "Error interno"));
        }
    }

    @PostMapping("/{id}/checkout")
    public ResponseEntity<?> checkoutOrder(@PathVariable UUID id, @RequestBody(required = false) Map<String, Object> checkoutData) {
        try {
            orderService.checkoutTableOrders(id, checkoutData != null ? checkoutData : Map.of());
            return ResponseEntity.ok(Map.of("message", "Cuenta cobrada y mesa liberada"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
}