package com.shop.entity;

import com.shop.constant.OrderStatus;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Setter @Getter
@Table(name = "orders")
public class Order extends BaseEntity{

    @Id
    @GeneratedValue
    @Column(name = "order_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)      //한명의 회원은 여러 번 주문
    @JoinColumn(name = "member_id")
    private Member member;

    private LocalDateTime orderDate;    //주문일

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;    //주문상태

    //order_id라는 외래키가 현재는 order_item 테이블에 있으므로 주인은 OrderItem이다. order가 주인이 아니므로 속성으로 주인 설정
    //속성값이 order인 이유는. OrderItem에 있는 order에 의해 관리된다는 의미.
    //cascade = CascadeType.ALL : 부보 엔티티의 영속성 상태 변화를 자식 엔티티에 모두전이
    //orphanRemoval = true : 고아 객체 제거.
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    // 하나의 주문이 여러 개의 주문 상품을 갖으므로 List 자료형을 사용해서 매핑
    private List<OrderItem> orderItems = new ArrayList<>();

    //BaseEntity 를 상속받기에 필요없다.
//    private LocalDateTime regTime;
//
//    private LocalDateTime updateTime;

    public void addOrderItem(OrderItem orderItem){  //orderItems에는 주문 상품 정보들을 담아준다. orderItem 객체를 order 객체의 orderItems에 추가한다.
        orderItems.add(orderItem);
        orderItem.setOrder(this);   //Order 엔티티와 OrderItem 엔티티가 양방향 참조 관계, orderItem 객체에도 order 객체를 세팅한다.
    }

    public static Order createOrder(Member member,List<OrderItem> orderItemList){
        Order order = new Order();
        order.setMember(member);        //상품을 주문한 회원의 정보를 세팅한다.

        // 장바구니 페이지에서는 한 번에 여러 개의 상품을 주문할 수 있으므로 여러 개의 주문 상품을 담을 수 있도록 리스트형태로 파라미터 값을 받으며 주문 객체에 orderItem 객체를 추가한다.
        for (OrderItem orderItem : orderItemList){
            order.addOrderItem(orderItem);
        }

        order.setOrderStatus(OrderStatus.ORDER);            //주문 상태를 order로 표시한다.
        order.setOrderDate(LocalDateTime.now());            //현재 시간을 주문 시간으로 세팅한다.
        return order;
    }

    public int getTotalPrice(){             //총 주문 금액을 구하는 메소드.
        int totalPrice = 0;
        for (OrderItem orderItem : orderItems){
            totalPrice += orderItem.getTotalPrice();
        }
        return totalPrice;
    }

    //주문 상태를 취소 상태로 바꿔주는 메소드
    public void cancelOrder(){
        this.orderStatus = OrderStatus.CANCEL;

        for (OrderItem orderItem : orderItems){
            orderItem.cancel();
        }
    }

}
