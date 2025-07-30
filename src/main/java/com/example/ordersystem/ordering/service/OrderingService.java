package com.example.ordersystem.ordering.service;

import com.example.ordersystem.member.domain.Member;
import com.example.ordersystem.member.repository.MemberRepository;
import com.example.ordersystem.ordering.domain.OrderDetail;
import com.example.ordersystem.ordering.domain.Ordering;
import com.example.ordersystem.ordering.dto.OrderCreateDto;
import com.example.ordersystem.ordering.dto.OrderDetailResDto;
import com.example.ordersystem.ordering.dto.OrderListResDto;
import com.example.ordersystem.ordering.repository.OrderDetailRepository;
import com.example.ordersystem.ordering.repository.OrderingRepository;
import com.example.ordersystem.product.domain.Product;
import com.example.ordersystem.product.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderingService {
    private final OrderingRepository orderingRepository;
    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;
    private final OrderDetailRepository orderDetailRepository;

    public Long save(List<OrderCreateDto> orderCreateDtoList) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Member member = memberRepository.findByEmail(email).orElseThrow(()->new EntityNotFoundException("없는 id입니다."));

        //ORDERING 조립하기 (부모먼저)
        Ordering ordering = Ordering.builder()
                .member(member)
                .build();

        //ORDERINGDETAIL 조립하기
        for(OrderCreateDto dto : orderCreateDtoList) {
            Product product = productRepository.findById(dto.getProductId()).orElseThrow(() -> new EntityNotFoundException("없는 id입니다."));
//            재고처리(재고(product)가 주문개수(dto)보다 적은경우)
            if(product.getStockQuantity() < dto.getProductCount()) {
//                예외를 강제발생시킴으로써, 모든 임시저장사항들을 rollback처리
                throw new IllegalArgumentException("재고가 부족합니다.");
            }
            product.updateStockQuantity(dto.getProductCount());
            OrderDetail orderDetail = OrderDetail.builder()
                    .product(product)
                    .quantity(dto.getProductCount())
                    .ordering(ordering)     //왜함??
                    .build();
//            orderDetailRepository.save(orderDetail);           // cascade 무(repo에 저장)
            ordering.getOrderDetailList().add(orderDetail);      // cascade 유(ordering 객체에 OrderDetailList 컬럼에 저장. repo 불필요)
        }
        orderingRepository.save(ordering);
        return ordering.getId();
    }

    public List<OrderListResDto> findAll() {
        //원본
        List<Ordering> orderingList = orderingRepository.findAll();
        //원본을 dto로 조립
        List<OrderListResDto> orderListResDtoList = new ArrayList<>();

//        원본(orderingList)을 orderListResDtoList로 바꿔서 리턴
        for(Ordering ordering : orderingList) {
            //⭐원본(orderDetailList)을 orderDetailResDtoList로 바꿔서 리턴
            List<OrderDetail> orderDetailList = ordering.getOrderDetailList();
            List<OrderDetailResDto> orderDetailResDtoList = new ArrayList<>();
            for(OrderDetail orderDetail : orderDetailList) {
                OrderDetailResDto orderDetailResDto = OrderDetailResDto.builder()
                        .detailId(orderDetail.getId())
                        .productName(orderDetail.getProduct().getName())
                        .productCount(orderDetail.getQuantity())
                        .build();
                orderDetailResDtoList.add(orderDetailResDto);
            }

            OrderListResDto dto = OrderListResDto.builder()
                    .id(ordering.getId())
                    .memberEmail(ordering.getMember().getEmail())
                    .orderStatus(ordering.getOrderStatus())
                    .orderDetails(orderDetailResDtoList)     //⭐
                    .build();
            orderListResDtoList.add(dto);
        }

        return orderListResDtoList;
    }
}
