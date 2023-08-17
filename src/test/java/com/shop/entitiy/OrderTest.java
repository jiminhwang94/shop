package com.shop.entitiy;

import com.shop.constant.ItemSellStatus;
import com.shop.entity.Item;
import com.shop.entity.Member;
import com.shop.entity.Order;
import com.shop.entity.OrderItem;
import com.shop.repository.ItemRepository;
import com.shop.repository.MemberRepository;
import com.shop.repository.OrderItemRepository;
import com.shop.repository.OrderRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
@TestPropertySource(locations = "classpath:application-test.properties")
class OrderTest {

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    OrderItemRepository orderItemRepository;
    @PersistenceContext
    EntityManager em;



    public Order createOrder(){
        Order order = new Order();

        for (int i=0;i<3;i++){
            Item item = createItem();
            itemRepository.save(item);
            OrderItem orderItem = new OrderItem();
            orderItem.setItem(item);
            orderItem.setCount(10);
            orderItem.setOrderPrice(1000);
            orderItem.setOrder(order);
            order.getOrderItems().add(orderItem);
        }

        Member member = new Member();
        memberRepository.save(member);

        order.setMember(member);
        orderRepository.save(order);
        return order;
    }

    @Test
    @DisplayName("지연 로딩 테스트")
    public void lazyLoadingTest(){
        Order order = this.createOrder();
        Long orderItemId = order.getOrderItems().get(0).getId();
//        System.out.println("ID: " +orderItemId);
        em.flush();
        em.clear();

        //findById(orderItemId)에 값이 null로 넘어오는 에러 발생.
        //createorder가 문제가 있다고 확인
        //order를 return하는데 저장을 안함.  order.setMember(member);
        //                              orderRepository.save(order); 작성후 해결
        OrderItem orderItem = orderItemRepository.findById(orderItemId)
                .orElseThrow(EntityNotFoundException::new);
        System.out.println("Order class : " + orderItem.getOrder().getClass());
        System.out.println("===================================");
        orderItem.getOrder().getOrderDate();
        System.out.println("===================================");

    }

    @Test
    @DisplayName("고아객체 제거 테스트")
    public void orphanRemovalTest(){
        Order order = this.createOrder();
        order.getOrderItems().remove(0);    //order 엔티티에서 관리하는 orderitem 리스트의 0번째를 제거한다.
        em.flush();     //변경 내용을 데이터베이스에 적용한다.
    }

    public Item createItem(){
        Item item = new Item();
        item.setItemNm("테스트 상품");
        item.setPrice(10000);
        item.setItemDetail("상세설명");
        item.setItemSellStatus(ItemSellStatus.SELL);
        item.setStockNumber(100);
        item.setRegTime(LocalDateTime.now());

        return item;
    }

    @Test
    @DisplayName("영속성 전이 테스트")
    public void cascadeTest(){

        Order order = new Order();

        for(int i=0; i<3; i++){
            Item item = this.createItem();
            itemRepository.save(item);
            OrderItem orderItem = new OrderItem();
            orderItem.setItem(item);
            orderItem.setCount(10);
            orderItem.setOrderPrice(1000);
            orderItem.setOrder(order);
            order.getOrderItems().add(orderItem); //아직 영속성 컨텍스트에 저장되지 않은 orderItem 엔티티를 order엔티티에 담음

        }

        orderRepository.saveAndFlush(order);    //강제로 Flush를 호출해서 영속성 컨텍스트에 있는 객체들을 데이터베이스에 반영.
        //saveAndFlush오류로 쿼리문 작동 안됨. 오류 이유 ? EntityManage.save()함수를 호출하기 전에 id 값이 설정되어 있어야한다.
        //하지만 order에 id를 확인해보니 @GeneratedValue가 없었다.그래서 추가하니 해결.@GeneratedValue는 Primary Key를 생성 해준다.
        em.clear();         // 영속성 컨텍스트 상태 초기화
        
        Order saveOrder = orderRepository.findById(order.getId())       //초기화후 데이터베이스에서 주문 엔티티 조회.select쿼리문 작동
                .orElseThrow(EntityNotFoundException::new);
        assertEquals(3, saveOrder.getOrderItems().size());      //itemOrder 엔티티 3개가 데이터베이스에 저장되었는지 검사한다.
    }


}
