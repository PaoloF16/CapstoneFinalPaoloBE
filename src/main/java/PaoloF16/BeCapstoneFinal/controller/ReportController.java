package PaoloF16.BeCapstoneFinal.controller;

import PaoloF16.BeCapstoneFinal.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/reports")
@CrossOrigin(origins = "http://localhost:5173")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @GetMapping("/analytics")
    public ResponseEntity<Map<String, Object>> getAnalytics() {
        return ResponseEntity.ok(reportService.getAnalyticsSummary());
    }

    @PostMapping("/close-register")
    public ResponseEntity<?> closeRegister(@RequestBody(required = false) Map<String, Object> body) {
        reportService.closeRegister(body);
        return ResponseEntity.ok(Map.of("message", "Caja cerrada exitosamente"));
    }

    @PostMapping("/open-new-day")
    public ResponseEntity<?> openNewDay(@RequestBody(required = false) Map<String, Object> body) {
        reportService.openNewDay(body);
        return ResponseEntity.ok(Map.of("message", "Nuevo día operativo iniciado"));
    }
}