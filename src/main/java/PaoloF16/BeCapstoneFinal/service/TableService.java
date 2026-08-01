// src/main/java/PaoloF16/BeCapstoneFinal/service/TableService.java
package PaoloF16.BeCapstoneFinal.service;

import PaoloF16.BeCapstoneFinal.entities.RestaurantTable;
import PaoloF16.BeCapstoneFinal.enums.TableStatus;
import PaoloF16.BeCapstoneFinal.repository.OrderRepository;
import PaoloF16.BeCapstoneFinal.repository.TableRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class TableService {

    @Autowired
    private TableRepository tableRepository;

    @Autowired
    private OrderRepository orderRepository;

    public List<RestaurantTable> getAllTables() {
        return tableRepository.findAll();
    }

    public RestaurantTable createTable(RestaurantTable table) {
        if (table.getStatus() == null) {
            table.setStatus(TableStatus.AVAILABLE);
        }
        return tableRepository.save(table);
    }

    public RestaurantTable updateTable(UUID id, RestaurantTable tableData) {
        RestaurantTable table = tableRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mesa no encontrada: " + id));

        table.setTableNumber(tableData.getTableNumber());
        table.setCapacity(tableData.getCapacity());

        return tableRepository.save(table);
    }

    public RestaurantTable updateTableStatus(UUID id, TableStatus status) {
        RestaurantTable table = tableRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mesa no encontrada: " + id));

        table.setStatus(status);
        return tableRepository.save(table);
    }

    // --- ELIMINACIÓN SEGURA DE MESA Y SUS ÓRDENES ---
    @Transactional
    public void deleteTable(UUID id) {
        RestaurantTable table = tableRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mesa no encontrada: " + id));

        // 1. Eliminar o desvincular órdenes asociadas a la mesa
        orderRepository.deleteByTableId(id); // O desvincular las órdenes asociadas

        // 2. Eliminar la mesa
        tableRepository.delete(table);
    }
}