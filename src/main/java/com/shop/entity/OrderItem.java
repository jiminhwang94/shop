package com.shop.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Setter @Getter
public class OrderItem extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "order_item_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)      //하나의 상품은 여러 주문 상픔 가능 다대일 단반향 매핑
    @JoinColumn(name = "item_id")
    private  Item item;

    @ManyToOne(fetch = FetchType.LAZY)  // 한번의 주문에 여러 개의 상품 주문 가능 다대일 단반향 매핑
    @JoinColumn(name = "order_id")
    private Order order;

    private int orderPrice; //주문가격
    
    private int count;  //수량

//    BaseEntity를 상속 받기에 등록시간과 업데이트시간 삭제.왜? BaseEntity에 다 담겨져있다.
//    private LocalDateTime regTime;
//    private LocalDateTime updateTime;

}
