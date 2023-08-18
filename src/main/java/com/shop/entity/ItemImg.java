package com.shop.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "item_img")
@Data
public class ItemImg {

    @Id
    @Column(name = "item_img_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String imgName;

    private String oriImgName;

    private String imgUrl;

    @ManyToOne(fetch = FetchType.LAZY)      //상품 엔티티와 다대일 매핑.지연 로딩 설정하여 상품 엔티티가 필요한 경우만 데이터 조회.
    @JoinColumn(name = "item_id")
    private Item item;

    public void updateItemImg(String oriImgName, String imgName, String imgUrl){            //원본이미지 파일명, 업데이트 이미지 파일명, 이미지 경로를 파라미터로 받아서 업데이트
        this.oriImgName = oriImgName;
        this.imgName = imgName;
        this.imgUrl = imgUrl;
    }
}
