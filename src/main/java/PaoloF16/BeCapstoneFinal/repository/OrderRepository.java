// src/main/java/PaoloF16/BeCapstoneFinal/repository/OrderRepository.java
package PaoloF16.BeCapstoneFinal.repository;

import PaoloF16.BeCapstoneFinal.entities.Order;
import PaoloF16.BeCapstoneFinal.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {

    List<Order> findByTableIdAndStatusNotOrderByCreatedAtAsc(UUID tableId, OrderStatus status);

    @Query("SELECT DISTINCT o FROM Order o " +
            "LEFT JOIN FETCH o.table " +
            "LEFT JOIN FETCH o.items i " +
            "LEFT JOIN FETCH i.product " +
            "WHERE o.status IN (:statuses) AND SIZE(o.items) > 0 " +
            "ORDER BY o.createdAt ASC")
    List<Order> findKitchenOrders(@Param("statuses") List<OrderStatus> statuses);

    // 💡 UPDATE DIRECTO: 100% a prueba de fallos de proxies o Lazy Loading
    @Modifying
    @Transactional
    @Query("UPDATE Order o SET o.status = :status, o.closedAt = :closedAt WHERE o.id = :id")
    int updateOrderStatusDirect(@Param("id") UUID id,
                                @Param("status") OrderStatus status,
                                @Param("closedAt") LocalDateTime closedAt);

    void deleteByTableId(UUID tableId);
}