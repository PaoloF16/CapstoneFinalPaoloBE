// src/main/java/PaoloF16/BeCapstoneFinal/service/OrderService.java
package PaoloF16.BeCapstoneFinal.service;

import PaoloF16.BeCapstoneFinal.entities.*;
import PaoloF16.BeCapstoneFinal.enums.OrderStatus;
import PaoloF16.BeCapstoneFinal.enums.TableStatus;
import PaoloF16.BeCapstoneFinal.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private TableRepository tableRepository;

    @Autowired
    private ProductRepository productRepository;

    // 1. CREAR NUEVO TICKET / COMANDA PARA COCINA
    @Transactional
    public Order createOrder(UUID tableId, List<Map<String, Object>> items) {
        RestaurantTable table = tableRepository.findById(tableId)
                .orElseThrow(() -> new RuntimeException("Mesa no encontrada: " + tableId));

        table.setStatus(TableStatus.OCCUPIED);
        tableRepository.save(table);

        Order order = Order.builder()
                .table(table)
                .status(OrderStatus.PENDING)
                .subtotal(0.0)
                .discount(0.0)
                .total(0.0)
                .items(new ArrayList<>())
                .createdAt(LocalDateTime.now())
                .build();

        double subtotal = 0.0;
        if (items != null && !items.isEmpty()) {
            for (Map<String, Object> itemReq : items) {
                UUID productId = UUID.fromString(itemReq.get("productId").toString());
                int quantity = Integer.parseInt(itemReq.get("quantity").toString());

                Product product = productRepository.findById(productId)
                        .orElseThrow(() -> new RuntimeException("Producto no encontrado: " + productId));

                OrderItem orderItem = OrderItem.builder()
                        .order(order)
                        .product(product)
                        .quantity(quantity)
                        .unitPrice(product.getPrice())
                        .build();

                order.getItems().add(orderItem);
                subtotal += product.getPrice() * quantity;
            }
        }

        order.setSubtotal(subtotal);
        order.setTotal(subtotal);

        return orderRepository.save(order);
    }

    // 2. OBTENER COMANDAS ACTIVAS PARA EL KDS DE COCINA (SOLO PENDIENTES O EN PREPARACIÓN)
    public List<Order> getKitchenOrders() {
        return orderRepository.findKitchenOrders(
                List.of(OrderStatus.PENDING, OrderStatus.IN_PREPARATION)
        );
    }

    // 3. MARCAR ESTADO (ej: READY / DESPACHADO)
    @Transactional
    public void updateOrderStatus(UUID orderId, OrderStatus newStatus) {
        LocalDateTime closedAt = (newStatus == OrderStatus.READY || newStatus == OrderStatus.DELIVERED)
                ? LocalDateTime.now()
                : null;

        int updatedRows = orderRepository.updateOrderStatusDirect(orderId, newStatus, closedAt);
        if (updatedRows == 0) {
            throw new RuntimeException("No se encontró la orden con ID: " + orderId);
        }
    }

    // 4. OBTENER LA CUENTA CONSOLIDADA DE LA MESA (Suma todas las comandas de la sesión)
    public Order getActiveOrderByTableId(UUID tableId) {
        List<Order> activeOrders = orderRepository.findByTableIdAndStatusNotOrderByCreatedAtAsc(tableId, OrderStatus.PAID);
        if (activeOrders.isEmpty()) {
            return null;
        }

        RestaurantTable table = activeOrders.get(0).getTable();
        List<OrderItem> allItems = new ArrayList<>();
        double totalSubtotal = 0.0;

        for (Order o : activeOrders) {
            if (o.getItems() != null) {
                allItems.addAll(o.getItems());
                totalSubtotal += o.getSubtotal();
            }
        }

        // Retorna un objeto consolidado para el cobro / precuenta
        return Order.builder()
                .id(activeOrders.get(0).getId()) // ID de referencia
                .table(table)
                .status(OrderStatus.PENDING)
                .items(allItems)
                .subtotal(totalSubtotal)
                .discount(0.0)
                .total(totalSubtotal)
                .createdAt(activeOrders.get(0).getCreatedAt())
                .build();
    }

    // 5. COBRO Y CIERRE DE TODAS LAS COMANDAS DE LA MESA
    @Transactional
    public Order checkoutTableOrders(UUID tableIdOrOrderId, Map<String, Object> checkoutData) {
        // Buscar mesa por ID de orden o directamente
        Order sampleOrder = orderRepository.findById(tableIdOrOrderId).orElse(null);
        UUID tableId = sampleOrder != null ? sampleOrder.getTable().getId() : tableIdOrOrderId;

        List<Order> activeOrders = orderRepository.findByTableIdAndStatusNotOrderByCreatedAtAsc(tableId, OrderStatus.PAID);
        if (activeOrders.isEmpty()) {
            throw new RuntimeException("No hay comandas activas para esta mesa.");
        }

        Double discountPercent = 0.0;
        if (checkoutData != null && checkoutData.containsKey("discountValue")) {
            try {
                discountPercent = Double.parseDouble(checkoutData.get("discountValue").toString());
            } catch (Exception ignored) {}
        }

        for (Order o : activeOrders) {
            double discountAmount = (o.getSubtotal() * discountPercent) / 100.0;
            o.setDiscount(discountAmount);
            o.setTotal(Math.max(0.0, o.getSubtotal() - discountAmount));
            o.setStatus(OrderStatus.PAID);
            o.setClosedAt(LocalDateTime.now());
            orderRepository.save(o);
        }

        // Liberar mesa
        RestaurantTable table = tableRepository.findById(tableId).orElse(null);
        if (table != null) {
            table.setStatus(TableStatus.AVAILABLE);
            tableRepository.save(table);
        }

        return activeOrders.get(0);
    }
}