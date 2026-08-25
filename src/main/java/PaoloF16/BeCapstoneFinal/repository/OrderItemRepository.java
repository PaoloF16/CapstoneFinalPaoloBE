// src/main/java/PaoloF16/BeCapstoneFinal/repository/OrderItemRepository.java
package PaoloF16.BeCapstoneFinal.repository;

import PaoloF16.BeCapstoneFinal.entities.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, UUID> {
    void deleteByProductId(UUID productId);
}