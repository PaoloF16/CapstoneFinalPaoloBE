package PaoloF16.BeCapstoneFinal.service;

import PaoloF16.BeCapstoneFinal.entities.RestaurantTable;
import PaoloF16.BeCapstoneFinal.enums.TableStatus;
import PaoloF16.BeCapstoneFinal.repository.TableRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class TableService {

    @Autowired
    private TableRepository tableRepository;

    public List<RestaurantTable> getAllTables() {
        return tableRepository.findAll();
    }

    public RestaurantTable createTable(RestaurantTable table) {
        return tableRepository.save(table);
    }

    public RestaurantTable updateTableStatus(UUID tableId, TableStatus status) {
        RestaurantTable table = tableRepository.findById(tableId)
                .orElseThrow(() -> new RuntimeException("Mesa no encontrada con id: " + tableId));
        table.setStatus(status);
        return tableRepository.save(table);
    }
}