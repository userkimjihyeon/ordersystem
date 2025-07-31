package com.example.ordersystem.common.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

//redis에서 재고등록 및 재고수량 확인 및 재고수량 감소처리 용도 service
@Component
public class StockInventoryService {
    private final RedisTemplate<String, String> redisTemplate;

//    redisConfig에서 @Bean으로 등록한 redisTemplate 주입
    public StockInventoryService(@Qualifier("stockInventory") RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

//    상품등록시 재고수량 세팅
    public void makeStockQuantity(Long productId, int quantity) {
        redisTemplate.opsForValue().set(String.valueOf(productId), String.valueOf(quantity));
    }
//    주문성공시 재고수량 감소
    public int decreaseStockQuantity(Long productId, int orderQuantity) {
        String remainObject = redisTemplate.opsForValue().get(String.valueOf(productId));
        int remains = Integer.parseInt(remainObject);
        if(remains < orderQuantity) {
            return -1;
        } else {
            Long finalRemains = redisTemplate.opsForValue().decrement(String.valueOf(productId), orderQuantity);
            return finalRemains.intValue();
        }
    }

//    주문취소시 재고수량 증가
    public void increaseStockQuantity() {

    }
}
