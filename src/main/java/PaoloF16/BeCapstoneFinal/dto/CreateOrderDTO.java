package PaoloF16.BeCapstoneFinal.dto;

import lombok.Data;
import java.util.List;
import java.util.UUID;

@Data
public class CreateOrderDTO {
    private UUID tableId;
    private List<OrderItemRequestDTO> items;
}