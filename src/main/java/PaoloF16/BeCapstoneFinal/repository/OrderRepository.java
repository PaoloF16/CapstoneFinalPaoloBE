// src/main/java/PaoloF16/BeCapstoneFinal/repository/OrderRepository.java
package PaoloF16.BeCapstoneFinal.repository;

import PaoloF16.BeCapstoneFinal.entities.Order;
import PaoloF16.BeCapstoneFinal.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {

    // 💡 Obtiene SÓLO LA ÚLTIMA orden activa (evita que truenen los resultados múltiples)
    Optional<Order> findFirstByTableIdAndStatusNotOrderByCreatedAtDesc(UUID tableId, OrderStatus status);

    // Permite eliminar las órdenes asociadas al borrar una mesa
    void deleteByTableId(UUID tableId);
}