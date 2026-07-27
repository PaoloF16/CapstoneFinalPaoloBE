package PaoloF16.BeCapstoneFinal.dto;

import lombok.Data;
import java.util.UUID;

@Data
public class OrderItemRequestDTO {
    private UUID productId;
    private Integer quantity;
}