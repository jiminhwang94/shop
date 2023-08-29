package com.shop.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Setter @Getter
@Table(name = "cart_item")
public class CartItem extends BaseEntity{

    @Id
    @GeneratedValue
    @Column(name = "cart_item_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)  //하나의 장바구니에는 여러개 담긴다.
    @JoinColumn(name = "cart_id")
    private  Cart cart;

    @ManyToOne(fetch = FetchType.LAZY)  //하나의 상품은 여러가지 장바구니에 담길 수 있기에 다대일
    @JoinColumn(name = "item_id")
    private Item item;

    private int count;      //장바구니에 몇개 담을지 저장

    //장바구니에 담을 상품 엔티티 생성
    public static CartItem createCartItem(Cart cart,Item item,int count){
        CartItem cartItem = new CartItem();
        cartItem.setCart(cart);
        cartItem.setItem(item);
        cartItem.setCount(count);
        return  cartItem;
    }

    //장바구니에 기존에 담겨있을 때 기존 수량에 현재 담을 수량을 더해줄 때 사용
    public void addCount(int count){
        this.count += count;
    }

    public void updateCount(int count){
        this.count = count;
    }
}