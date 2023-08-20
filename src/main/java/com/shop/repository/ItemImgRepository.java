package com.shop.repository;

import com.shop.entity.ItemImg;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemImgRepository extends JpaRepository<ItemImg,Long> {
    //상품의 이미지 정보를 저장하기 위해서 만듬

    //이미지가 잘 저장됐는지 테스트 코드 작성하기 위해 추가
    List<ItemImg> findByItemIdOrderByIdAsc(Long itemId);
}
