package PaoloF16.BeCapstoneFinal.service;

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