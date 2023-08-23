package com.shop.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Wildcard;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.shop.constant.ItemSellStatus;
import com.shop.dto.ItemSearchDto;
import com.shop.dto.MainItemDto;
import com.shop.dto.QMainItemDto;
import com.shop.entity.Item;
import com.shop.entity.QItem;
import com.shop.entity.QItemImg;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.thymeleaf.util.StringUtils;


import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;

public class ItemRepositoryCustomImpl implements ItemRepositoryCustom{

    private JPAQueryFactory queryFactory;       //동적으로 쿼리를 생성하기 위해서 JPAQueryFactory클래스 사용

    //JPAQueryFactory의 생성자로 EntityManager 객체를 넣어준다.
    public ItemRepositoryCustomImpl(EntityManager em){
        this.queryFactory = new JPAQueryFactory(em);
    }

    //상품 판매 상태 조건이 전체(Null)일 경우 null 리턴. 결과값이 null이면 where절에서 해당 조건은 무시.상태 판매 상태 조건이 null이 아니라 판매중 or 품절 상태면 해당 조건의 상품만 조회
    private BooleanExpression searchSellStatusEq(ItemSellStatus searchSellStatus){
        return searchSellStatus == null ? null : QItem.item.itemSellStatus.eq(searchSellStatus);    // searchDateType의 값에 따라서 dateTime의 값을 시간 정해서 그 시간 이후 지금까지의 등록된 상푸만 조회한다.
    }

    private BooleanExpression regDtsAfter(String searchDateType){

        LocalDateTime dateTime = LocalDateTime.now();

        if(StringUtils.equals("all", searchDateType) || searchDateType == null){
            return null;
        } else if(StringUtils.equals("1d", searchDateType)){
            dateTime = dateTime.minusDays(1);
        } else if(StringUtils.equals("1w", searchDateType)){
            dateTime = dateTime.minusWeeks(1);
        } else if(StringUtils.equals("1m", searchDateType)){
            dateTime = dateTime.minusMonths(1);
        } else if(StringUtils.equals("6m", searchDateType)){
            dateTime = dateTime.minusMonths(6);
        }

        return QItem.item.regTime.after(dateTime);
    }

    //searchBy의 값에 따라서 상품명에 검색어를 포함하고 있는 상품, 상품 생성자 아이디에 검색어를 포함하고 있는 상품을 조회하도록 조건값을 반환
    private BooleanExpression searchByLike(String searchBy, String searchQuery){

        if(StringUtils.equals("itemNm", searchBy)){
            return QItem.item.itemNm.like("%" + searchQuery + "%");
        } else if(StringUtils.equals("createdBy", searchBy)){
            return QItem.item.createBy.like("%" + searchQuery + "%");
        }

        return null;
    }

    @Override
    public Page<Item> getAdminItemPage(ItemSearchDto itemSearchDto, Pageable pageable) {

        List<Item> content = queryFactory   //queryFactory를 이용해서 쿼리 생성. 직접 작설할 때의 형태와 문법이 비슷하다.
                .selectFrom(QItem.item) //상품 데이터를 조회하기 위해서 QItem의 item 지정한다.
                .where(regDtsAfter(itemSearchDto.getSearchDateType()),      //,는 and로 인식한다.
                        searchSellStatusEq(itemSearchDto.getSearchSellStatus()),
                        searchByLike(itemSearchDto.getSearchBy(),
        itemSearchDto.getSearchQuery()))
                .orderBy(QItem.item.id.desc())
                .offset(pageable.getOffset())   //데이터를 가지고 올 시작 인덱스 지정
                .limit(pageable.getPageSize())  //한번에 가지고 올 최대 개수 지정
                .fetch();                       //조회한 리스트 및 전체 개수를 포함하는 QueryResults를 반환.2번의 쿼리문 실행

        long total = queryFactory.select(Wildcard.count).from(QItem.item)
                .where(regDtsAfter(itemSearchDto.getSearchDateType()),
                        searchSellStatusEq(itemSearchDto.getSearchSellStatus()),
                        searchByLike(itemSearchDto.getSearchBy(), itemSearchDto.getSearchQuery()))
                .fetchOne()
                ;

        //content = 현제 페이지에 포함될 데이터, pageable = 페이지 정보(현재 페이지 번호,페이지 크기 등), total = 위에서 얻은 총 레코드 수
        return new PageImpl<>(content, pageable, total); //조회한 데이터를 page 클래스의 구현체인 PageImpl객체로 반환한다.
    }

    //검색어가 null이 아니면 상품명에 해당 검색어가 포함되는 상품을 조회하는 조건을 반환한다.
    private BooleanExpression itemNmLike(String searchQuery){
        return StringUtils.isEmpty(searchQuery) ? null : QItem.item.itemNm.like("%" + searchQuery + "%");
    }

    @Override           //getMainItemPage() 메소드를 구현한다.
    public Page<MainItemDto> getMainItemPage(ItemSearchDto itemSearchDto, Pageable pageable) {
        QItem item = QItem.item;
        QItemImg itemImg = QItemImg.itemImg;

        List<MainItemDto> content = queryFactory
                .select(
                        new QMainItemDto(   //QMainItemDto에 반환할 값들을 넣어준다. @QueryProjection을 사용하면 DTO로 바로 조회 가능. 엔티티 조회 후 DTO로 변환하는 과정 줄인다.
                                item.id,
                                item.itemNm,
                                item.itemDetail,
                                itemImg.imgUrl,
                                item.price)
                )
                .from(itemImg)
                .join(itemImg.item, item)   //itemImg와 item을 내부 조인한다.
                .where(itemImg.repimgYn.eq("Y"))    //상품 이미지의 경우 대표 상품 이미지만 불러온다.
                .where(itemNmLike(itemSearchDto.getSearchQuery()))
                .orderBy(item.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = queryFactory
                .select(Wildcard.count)
                .from(itemImg)
                .join(itemImg.item, item)
                .where(itemImg.repimgYn.eq("Y"))
                .where(itemNmLike(itemSearchDto.getSearchQuery()))
                .fetchOne()
                ;

        return new PageImpl<>(content, pageable, total);
    }

}