package PaoloF16.BeCapstoneFinal.service;

import PaoloF16.BeCapstoneFinal.entities.Order;
import PaoloF16.BeCapstoneFinal.entities.OrderItem;
import PaoloF16.BeCapstoneFinal.enums.OrderStatus;
import PaoloF16.BeCapstoneFinal.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReportService {

    @Autowired
    private OrderRepository orderRepository;

    // Control de Sesión de Caja / Turno
    private LocalDateTime shiftStartAt = LocalDate.now().atStartOfDay();
    private LocalDateTime lastClosedAt = null;
    private boolean registerClosed = false;
    private double initialCash = 100.0;

    public void closeRegister(Map<String, Object> data) {
        this.registerClosed = true;
        this.lastClosedAt = LocalDateTime.now();
        if (data != null && data.containsKey("initialCash")) {
            try {
                this.initialCash = Double.parseDouble(data.get("initialCash").toString());
            } catch (Exception ignored) {}
        }
    }

    public void openNewDay(Map<String, Object> data) {
        this.registerClosed = false;
        // Establece el inicio del nuevo turno al momento exacto actual
        this.shiftStartAt = LocalDateTime.now();
        this.lastClosedAt = null;
        if (data != null && data.containsKey("initialCash")) {
            try {
                this.initialCash = Double.parseDouble(data.get("initialCash").toString());
            } catch (Exception ignored) {}
        }
    }

    public Map<String, Object> getAnalyticsSummary() {
        List<Order> paidOrders = orderRepository.findAll().stream()
                .filter(o -> o.getStatus() == OrderStatus.PAID)
                .toList();

        LocalDate today = LocalDate.now();
        LocalDateTime startOfWeek = today.with(TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY)).atStartOfDay();
        LocalDateTime startOfMonth = today.with(TemporalAdjusters.firstDayOfMonth()).atStartOfDay();
        LocalDateTime startOfYear = today.with(TemporalAdjusters.firstDayOfYear()).atStartOfDay();

        // 1. Ventas del Turno / Día Operativo Actual
        LocalDateTime effectiveStart = this.shiftStartAt != null ? this.shiftStartAt : today.atStartOfDay();

        List<Order> todayOrders = paidOrders.stream()
                .filter(o -> {
                    LocalDateTime d = getOrderDate(o);
                    if (this.registerClosed && this.lastClosedAt != null) {
                        return !d.isBefore(effectiveStart) && !d.isAfter(this.lastClosedAt);
                    }
                    return !d.isBefore(effectiveStart);
                }).toList();

        double todayTotal = todayOrders.stream()
                .mapToDouble(o -> o.getTotal() != null ? o.getTotal() : 0.0)
                .sum();

        double weekTotal = paidOrders.stream()
                .filter(o -> getOrderDate(o).isAfter(startOfWeek))
                .mapToDouble(o -> o.getTotal() != null ? o.getTotal() : 0.0)
                .sum();

        double monthTotal = paidOrders.stream()
                .filter(o -> getOrderDate(o).isAfter(startOfMonth))
                .mapToDouble(o -> o.getTotal() != null ? o.getTotal() : 0.0)
                .sum();

        double yearTotal = paidOrders.stream()
                .filter(o -> getOrderDate(o).isAfter(startOfYear))
                .mapToDouble(o -> o.getTotal() != null ? o.getTotal() : 0.0)
                .sum();

        // 2. Desglose de Comandas del Turno Actual
        DateTimeFormatter timeFmt = DateTimeFormatter.ofPattern("HH:mm");
        List<Map<String, Object>> todayOrdersList = todayOrders.stream().map(o -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", o.getId() != null ? o.getId().toString() : "");
            map.put("tableNumber", o.getTable() != null ? o.getTable().getTableNumber() : 0);
            map.put("total", o.getTotal() != null ? o.getTotal() : 0.0);
            map.put("time", getOrderDate(o).format(timeFmt));
            map.put("paymentMethod", "EFECTIVO");
            return map;
        }).collect(Collectors.toList());

        // 3. Métricas Generales
        int totalOrdersCount = paidOrders.size();
        double averageTicket = totalOrdersCount > 0
                ? paidOrders.stream().mapToDouble(o -> o.getTotal() != null ? o.getTotal() : 0.0).sum() / totalOrdersCount
                : 0.0;

        // 4. Top 5 Platos
        Map<String, Integer> productQuantities = new HashMap<>();
        Map<String, Double> productRevenues = new HashMap<>();

        for (Order order : paidOrders) {
            if (order.getItems() != null) {
                for (OrderItem item : order.getItems()) {
                    String prodName = item.getProduct() != null ? item.getProduct().getName() : "Plato";
                    int qty = item.getQuantity() != null ? item.getQuantity() : 1;
                    double unitPrice = item.getUnitPrice() != null ? item.getUnitPrice() : 0.0;

                    productQuantities.put(prodName, productQuantities.getOrDefault(prodName, 0) + qty);
                    productRevenues.put(prodName, productRevenues.getOrDefault(prodName, 0.0) + (qty * unitPrice));
                }
            }
        }

        List<Map<String, Object>> topProducts = productQuantities.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(5)
                .map(e -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("name", e.getKey());
                    map.put("quantity", e.getValue());
                    map.put("totalRevenue", productRevenues.getOrDefault(e.getKey(), 0.0));
                    return map;
                })
                .collect(Collectors.toList());

        // 5. Desglose Semanal (Últimos 7 días)
        List<Map<String, Object>> last7Days = new ArrayList<>();
        for (int i = 6; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            LocalDateTime dStart = date.atStartOfDay();
            LocalDateTime dEnd = date.atTime(LocalTime.MAX);

            double daySales = paidOrders.stream()
                    .filter(o -> {
                        LocalDateTime od = getOrderDate(o);
                        return !od.isBefore(dStart) && !od.isAfter(dEnd);
                    })
                    .mapToDouble(o -> o.getTotal() != null ? o.getTotal() : 0.0)
                    .sum();

            int count = (int) paidOrders.stream()
                    .filter(o -> {
                        LocalDateTime od = getOrderDate(o);
                        return !od.isBefore(dStart) && !od.isAfter(dEnd);
                    })
                    .count();

            Map<String, Object> dayMap = new HashMap<>();
            dayMap.put("date", date.toString());
            dayMap.put("dayName", date.getDayOfWeek().name());
            dayMap.put("total", daySales);
            dayMap.put("orderCount", count);
            last7Days.add(dayMap);
        }

        // 6. Desglose Mensual (Por semanas)
        List<Map<String, Object>> monthWeeks = new ArrayList<>();
        for (int w = 1; w <= 4; w++) {
            LocalDate wStart = today.withDayOfMonth(Math.min((w - 1) * 7 + 1, today.lengthOfMonth()));
            LocalDate wEnd = today.withDayOfMonth(Math.min(w * 7, today.lengthOfMonth()));

            double wSales = paidOrders.stream().filter(o -> {
                LocalDate od = getOrderDate(o).toLocalDate();
                return !od.isBefore(wStart) && !od.isAfter(wEnd) && od.getMonth() == today.getMonth();
            }).mapToDouble(o -> o.getTotal() != null ? o.getTotal() : 0.0).sum();

            int wCount = (int) paidOrders.stream().filter(o -> {
                LocalDate od = getOrderDate(o).toLocalDate();
                return !od.isBefore(wStart) && !od.isAfter(wEnd) && od.getMonth() == today.getMonth();
            }).count();

            Map<String, Object> weekMap = new HashMap<>();
            weekMap.put("label", "Semana " + w + " (" + wStart.getDayOfMonth() + " - " + wEnd.getDayOfMonth() + ")");
            weekMap.put("total", wSales);
            weekMap.put("orderCount", wCount);
            monthWeeks.add(weekMap);
        }

        // 7. Desglose Anual (12 Meses)
        String[] monthNames = {"Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"};
        List<Map<String, Object>> yearMonths = new ArrayList<>();
        for (int m = 1; m <= 12; m++) {
            final int targetMonth = m;
            double mSales = paidOrders.stream().filter(o -> {
                LocalDateTime od = getOrderDate(o);
                return od.getYear() == today.getYear() && od.getMonthValue() == targetMonth;
            }).mapToDouble(o -> o.getTotal() != null ? o.getTotal() : 0.0).sum();

            int mCount = (int) paidOrders.stream().filter(o -> {
                LocalDateTime od = getOrderDate(o);
                return od.getYear() == today.getYear() && od.getMonthValue() == targetMonth;
            }).count();

            Map<String, Object> mYearMap = new HashMap<>();
            mYearMap.put("monthName", monthNames[m - 1]);
            mYearMap.put("monthNumber", m);
            mYearMap.put("total", mSales);
            mYearMap.put("orderCount", mCount);
            yearMonths.add(mYearMap);
        }

        // 8. Construcción del resultado
        Map<String, Object> result = new HashMap<>();
        result.put("todayTotal", todayTotal);
        result.put("weekTotal", weekTotal);
        result.put("monthTotal", monthTotal);
        result.put("yearTotal", yearTotal);
        result.put("totalPaidOrders", totalOrdersCount);
        result.put("averageTicket", averageTicket);
        result.put("topProducts", topProducts);
        result.put("last7Days", last7Days);
        result.put("todayOrdersList", todayOrdersList);
        result.put("monthWeeks", monthWeeks);
        result.put("yearMonths", yearMonths);
        result.put("isRegisterClosed", this.registerClosed);
        result.put("initialCash", this.initialCash);

        return result;
    }

    private LocalDateTime getOrderDate(Order o) {
        if (o.getClosedAt() != null) return o.getClosedAt();
        if (o.getCreatedAt() != null) return o.getCreatedAt();
        return LocalDateTime.now();
    }
}