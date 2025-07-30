package com.example.ordersystem.ordering.dto;

import com.example.ordersystem.ordering.domain.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderListResDto {
    private Long id;
    private String memberEmail;
    private OrderStatus orderStatus = OrderStatus.ORDERED;
    private List<OrderDetailResDto> orderDetails;
}
