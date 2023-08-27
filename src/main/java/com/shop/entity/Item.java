package com.shop.entity;

import com.shop.constant.ItemSellStatus;
import com.shop.dto.ItemFormDto;
import com.shop.exception.OutOfStockException;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Entity //Item클래스를 엔티티로 선언
@Table(name="item") //item 테이블과 매핑되는 것을 지정하는것
public class Item extends BaseEntity{

    @Id //해당 엔티티에서 Primary Key라는것을 알려주는 어노테이션
    @Column(name="item_id") //column의 이름 지정
    @GeneratedValue(strategy = GenerationType.AUTO) // JPA에서 기본키 생성전략을 AUTO로 하는것
    private Long id;

    @Column(nullable = false, length = 50) //nullable: 널값 허용여부, length:String타입 문자길에 제약조건
    private String itemNm;

    @Column(name="price", nullable = false)
    private int price;

    @Column(nullable = false)
    private int stockNumber;

    @Lob
    @Column(nullable = false)
    private String itemDetail;

    @Enumerated(EnumType.STRING) //enum을 매핑하려고 할때 사용, 해당 열거형 상수들을 문자열로 받는것
    private ItemSellStatus itemSellStatus;


    //상품 업데이트 하는 로직
    public void updateItem(ItemFormDto itemFormDto){
        this.itemNm = itemFormDto.getItemNm();
        this.price = itemFormDto.getPrice();
        this.stockNumber = itemFormDto.getStockNumber();
        this.itemDetail = itemFormDto.getItemDetail();
        this.itemSellStatus = itemFormDto.getItemSellStatus();
    }

    public void removeStock(int stockNumber){
        //상품의 재고 수량에서 주문 후 남은 재고 수량을 수하는것
        int restStock = this.stockNumber - stockNumber;
        if(restStock<0){
            throw new OutOfStockException("상품의 재고가 부족합니다. (현 재고 수량 :" + this.stockNumber +")");
        }
        //주문 후 남은 재고 수량을 상품의 현재 재고 값으로 할당
        this.stockNumber = restStock;
    }


    //상품의 재고를 증가시키는 메소드
    public void addStock(int stockNumber){
        this.stockNumber += stockNumber;
    }
}