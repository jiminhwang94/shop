package com.shop.exception;

public class OutOfStockException extends RuntimeException {
//삼품의 주문 수량보다 재고의 수가 적을 때 발생
    public OutOfStockException(String message){
        super(message);
    }
}
