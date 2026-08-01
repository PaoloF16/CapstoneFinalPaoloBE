// src/main/java/PaoloF16/BeCapstoneFinal/controller/TableController.java
package PaoloF16.BeCapstoneFinal.controller;

import PaoloF16.BeCapstoneFinal.entities.RestaurantTable;
import PaoloF16.BeCapstoneFinal.enums.TableStatus;
import PaoloF16.BeCapstoneFinal.service.TableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/tables")
@CrossOrigin(origins = "http://localhost:5173")
public class TableController {

    @Autowired
    private TableService tableService;

    @GetMapping
    public List<RestaurantTable> getAllTables() {
        return tableService.getAllTables();
    }

    @PostMapping
    public RestaurantTable createTable(@RequestBody RestaurantTable table) {
        return tableService.createTable(table);
    }

    // --- ENDPOINT PARA EDITAR MESA ---
    @PutMapping("/{id}")
    public RestaurantTable updateTable(@PathVariable UUID id, @RequestBody RestaurantTable tableData) {
        return tableService.updateTable(id, tableData);
    }

    @PutMapping("/{id}/status")
    public RestaurantTable updateStatus(@PathVariable UUID id, @RequestBody Map<String, String> body) {
        TableStatus status = TableStatus.valueOf(body.get("status"));
        return tableService.updateTableStatus(id, status);
    }

    // --- ENDPOINT PARA ELIMINAR MESA ---
    @DeleteMapping("/{id}")
    public void deleteTable(@PathVariable UUID id) {
        tableService.deleteTable(id);
    }
}