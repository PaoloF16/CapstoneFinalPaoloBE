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

    // 1. CREAR ORDEN Y CAMBIAR MESA A OCUPADA
    @Transactional
    public Order createOrder(UUID tableId, List<Map<String, Object>> items) {
        RestaurantTable table = tableRepository.findById(tableId)
                .orElseThrow(() -> new RuntimeException("Mesa no encontrada: " + tableId));

        // Marcar mesa como OCUPADA en la base de datos
        table.setStatus(TableStatus.OCCUPIED);
        tableRepository.save(table);

        Order order = Order.builder()
                .table(table)
                .status(OrderStatus.PENDING)
                .subtotal(0.0)
                .discount(0.0)
                .total(0.0)
                .items(new ArrayList<>())
                .createdAt(LocalDateTime.now()) // 👈 CORREGIDO: LocalDateTime.now()
                .build();

        Order savedOrder = orderRepository.save(order);

        if (items != null && !items.isEmpty()) {
            return updateOrderItems(savedOrder.getId(), items);
        }

        return savedOrder;
    }

    // 2. ACTUALIZAR ITEMS DE LA COMANDA
    @Transactional
    public Order updateOrderItems(UUID orderId, List<Map<String, Object>> items) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Orden no encontrada: " + orderId));

        order.getItems().clear();
        double subtotal = 0.0;

        if (items != null) {
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
        double discount = order.getDiscount() != null ? order.getDiscount() : 0.0;
        order.setTotal(Math.max(0.0, subtotal - discount));

        return orderRepository.save(order);
    }

    // 3. OBTENER ORDEN ACTIVA DE LA MESA
    public Order getActiveOrderByTableId(UUID tableId) {
        return orderRepository.findFirstByTableIdAndStatusNotOrderByCreatedAtDesc(tableId, OrderStatus.PAID)
                .orElse(null);
    }

    // 4. CHECKOUT (COBRO) Y LIBERACIÓN AUTOMÁTICA DE MESA EN POSTGRESQL
    @Transactional
    public Order checkoutOrder(UUID orderId, Map<String, Object> checkoutData) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Orden no encontrada: " + orderId));

        Double discountPercent = 0.0;
        if (checkoutData != null && checkoutData.containsKey("discountValue")) {
            try {
                discountPercent = Double.parseDouble(checkoutData.get("discountValue").toString());
            } catch (Exception e) {
                discountPercent = 0.0;
            }
        }

        double discountAmount = (order.getSubtotal() * discountPercent) / 100.0;
        order.setDiscount(discountAmount);
        order.setTotal(Math.max(0.0, order.getSubtotal() - discountAmount));
        order.setStatus(OrderStatus.PAID);

        // 💡 CAMBIAR MESA A ESTADO LIBRE EN BASE DE DATOS
        RestaurantTable table = order.getTable();
        if (table != null) {
            table.setStatus(TableStatus.AVAILABLE);
            tableRepository.save(table);
        }

        return orderRepository.save(order);
    }
}