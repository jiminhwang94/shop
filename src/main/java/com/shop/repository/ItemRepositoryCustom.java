package com.shop.repository;


import com.shop.dto.ItemSearchDto;
import com.shop.entity.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ItemRepositoryCustom {
    //Querydsl을 Spring Data Jpa와 함께 사용하기 위해 정의

    //ItemSearchDto = 상품 조회 조건을 담고있다.
    //Pageable = 페이징 정보를 담고있다.
    // 이 두 객체를 파라미터로 받는 getAdminItemPage 메소드로 정의.
    Page<Item> getAdminItemPage(ItemSearchDto itemSearchDto, Pageable pageable);

//    Page<MainItemDto> getMainItemPage(ItemSearchDto itemSearchDto, Pageable pageable);
}
