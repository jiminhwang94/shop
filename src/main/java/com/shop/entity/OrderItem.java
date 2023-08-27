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

    //주문 상품 객체를 만든다.
    public  static OrderItem createOrderItem(Item item, int count){
        OrderItem orderItem = new OrderItem();
        orderItem.setItem(item);                //주문할 상품
        orderItem.setCount(count);              //주문 수량
    orderItem.setOrderPrice(item.getPrice());   //현재 시간 기준으로 상품 가격을 주문 가격으로 세팅.상품 가격은 시간에 따라서 달라질 수 있다. (쿠폰이나 할인 적용은 고려하지 않았다.)

        item.removeStock(count);                //주문 수량만큼 상품의 재고 수량을 감소.
        return orderItem;
    }


    public int getTotalPrice(){                 //주문 가격과 주문 수량을 곱해서 해당 상품을 주문한 총 가격을 계산하는 메소드.
        return orderPrice*count;
    }

    //주문 취소 시 주문 수량만큼 상품의 재고를 더해준다.
    public void  cancel(){
        this.getItem().addStock(count);
    }

}
