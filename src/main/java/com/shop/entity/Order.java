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
public class Order {

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

    private LocalDateTime regTime;

    private LocalDateTime updateTime;

}
