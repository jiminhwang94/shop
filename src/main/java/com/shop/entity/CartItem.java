package com.shop.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Setter @Getter
@Table(name = "cart_item")
public class CartItem {

    @Id
    @GeneratedValue
    @Column(name = "cart_item_id")
    private Long id;

    @ManyToOne  //하나의 장바구니에는 여러개 담긴다.
    @JoinColumn(name = "cart_id")
    private  Cart cart;

    @ManyToOne  //하나의 상품은 여러가지 장바구니에 담길 수 있기에 다대일
    @JoinColumn(name = "item_id")
    private Item item;

    private int count;      //장바구니에 몇개 담을지 저장
}
