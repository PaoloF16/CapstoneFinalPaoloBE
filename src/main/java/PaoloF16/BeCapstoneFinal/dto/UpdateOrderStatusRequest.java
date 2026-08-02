package PaoloF16.BeCapstoneFinal.dto;

import PaoloF16.BeCapstoneFinal.enums.OrderStatus;
import lombok.Data;

@Data
public class UpdateOrderStatusRequest {
    private OrderStatus status;
}