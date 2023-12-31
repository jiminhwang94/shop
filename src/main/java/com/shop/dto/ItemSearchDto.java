package com.shop.dto;

import com.shop.constant.ItemSellStatus;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ItemSearchDto {
    // 상품 데이터 조회 시 상품 조회 조건을 가지고 있는 클래스

    //현재 시간과 상품 등록일을 비교해서 상품 데이터를 조회.
    //기준) all:전체, 1d:최근 하루 동안 등록된 상품, 1w:일주일, 1m:한달, 6m:6개월
    private String searchDateType;

    //상품 판매상태 기준으로 상품 데이터 조회
    private ItemSellStatus searchSellStatus;

    //상품을 조회할 때 어떤 유형으로 조회할지 선택
    // itemNm : 상품명, createdBy:상품 등록자 아이디
    private String searchBy;

    //조회할 검색어 저장할 변수.
    //searchBy가 itemNm일 경우 상품명을 기준으로 검색, createdBy일 경우 상품 등록자 아이디 기준으로 검색
    private String searchQuery = "";
}
