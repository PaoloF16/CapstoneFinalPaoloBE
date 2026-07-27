package PaoloF16.BeCapstoneFinal.service;

import PaoloF16.BeCapstoneFinal.dto.CheckoutRequestDTO;
import PaoloF16.BeCapstoneFinal.dto.CreateOrderDTO;
import PaoloF16.BeCapstoneFinal.dto.OrderItemRequestDTO;
import PaoloF16.BeCapstoneFinal.entities.*;
import PaoloF16.BeCapstoneFinal.enums.OrderStatus;
import PaoloF16.BeCapstoneFinal.enums.TableStatus;
import PaoloF16.BeCapstoneFinal.repository.OrderRepository;
import PaoloF16.BeCapstoneFinal.repository.ProductRepository;
import PaoloF16.BeCapstoneFinal.repository.TableRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private TableRepository tableRepository;

    @Autowired
    private ProductRepository productRepository;

    @Transactional
    public Order createOrder(CreateOrderDTO dto) {
        RestaurantTable table = tableRepository.findById(dto.getTableId())
                .orElseThrow(() -> new RuntimeException("Mesa no encontrada con id: " + dto.getTableId()));

        Order order = Order.builder()
                .table(table)
                .status(OrderStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .items(new ArrayList<>())
                .build();

        double total = 0.0;

        if (dto.getItems() != null && !dto.getItems().isEmpty()) {
            for (OrderItemRequestDTO itemDto : dto.getItems()) {
                Product product = productRepository.findById(itemDto.getProductId())
                        .orElseThrow(() -> new RuntimeException("Producto no encontrado con id: " + itemDto.getProductId()));

                OrderItem item = OrderItem.builder()
                        .order(order)
                        .product(product)
                        .quantity(itemDto.getQuantity())
                        .unitPrice(product.getPrice())
                        .build();

                order.getItems().add(item);
                total += product.getPrice() * itemDto.getQuantity();
            }
        }

        order.setTotal(total);

        // Actualizar el estado de la mesa a OCUPADA
        table.setStatus(TableStatus.OCCUPIED);
        tableRepository.save(table);

        return orderRepository.save(order);
    }

    @Transactional
    public Order checkoutOrder(UUID orderId, CheckoutRequestDTO checkoutDto) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Orden no encontrada con id: " + orderId));

        if (order.getStatus() == OrderStatus.PAID) {
            throw new RuntimeException("La orden ya ha sido pagada previamente.");
        }

        // 1. Calcular subtotal basado en los items de la orden
        double subtotal = order.getItems().stream()
                .mapToDouble(item -> item.getUnitPrice() * item.getQuantity())
                .sum();

        order.setSubtotal(subtotal);

        // 2. Procesar descuento opcional
        double calculatedDiscount = 0.0;
        if (checkoutDto != null && checkoutDto.getDiscountValue() != null && checkoutDto.getDiscountValue() > 0) {
            String type = checkoutDto.getDiscountType() != null ? checkoutDto.getDiscountType().toUpperCase() : "FIXED";
            order.setDiscountType(type);
            order.setDiscount(checkoutDto.getDiscountValue());

            if ("PERCENTAGE".equals(type)) {
                calculatedDiscount = subtotal * (checkoutDto.getDiscountValue() / 100.0);
            } else { // "FIXED"
                calculatedDiscount = checkoutDto.getDiscountValue();
            }
        } else {
            order.setDiscount(0.0);
            order.setDiscountType("NONE");
        }

        // 3. Calcular total final (asegurando que no sea negativo)
        double total = Math.max(0.0, subtotal - calculatedDiscount);
        order.setTotal(total);

        // 4. Cambiar estado de la orden a PAGADO y registrar fecha de cierre
        order.setStatus(OrderStatus.PAID);
        order.setClosedAt(LocalDateTime.now());

        // 5. Liberar automáticamente la mesa asignada (Estado LIBRE / AVAILABLE)
        RestaurantTable table = order.getTable();
        table.setStatus(TableStatus.AVAILABLE);
        tableRepository.save(table);

        return orderRepository.save(order);
    }

    public Order getActiveOrderByTableId(UUID tableId) {
        return orderRepository.findByTableIdAndStatusNot(tableId, OrderStatus.PAID)
                .orElseThrow(() -> new RuntimeException("No hay una orden activa para la mesa con id: " + tableId));
    }

    @Transactional
    public Order updateOrderItems(UUID orderId, List<OrderItemRequestDTO> itemsDto) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Orden no encontrada con id: " + orderId));

        order.getItems().clear();
        double total = 0.0;

        for (OrderItemRequestDTO itemDto : itemsDto) {
            Product product = productRepository.findById(itemDto.getProductId())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado con id: " + itemDto.getProductId()));

            OrderItem item = OrderItem.builder()
                    .order(order)
                    .product(product)
                    .quantity(itemDto.getQuantity())
                    .unitPrice(product.getPrice())
                    .build();

            order.getItems().add(item);
            total += product.getPrice() * itemDto.getQuantity();
        }

        order.setTotal(total);
        return orderRepository.save(order);
    }
}