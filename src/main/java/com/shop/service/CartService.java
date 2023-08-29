package com.shop.service;


import com.shop.dto.CartDetailDto;
import com.shop.dto.CartItemDto;
import com.shop.dto.CartOrderDto;
import com.shop.dto.OrderDto;
import com.shop.entity.Cart;
import com.shop.entity.CartItem;
import com.shop.entity.Item;
import com.shop.entity.Member;
import com.shop.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.util.StringUtils;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class CartService {

    //장바구니에 상품을 담는 로직
    private final ItemRepository itemRepository;
    private final MemberRepository memberRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final OrderService orderService;

    public Long addCart(CartItemDto cartItemDto, String email) {

        Item item = itemRepository.findById(cartItemDto.getItemId())            //장바구니에 담을 상품 엔티티 조회
                .orElseThrow(EntityNotFoundException::new);
        Member member = memberRepository.findByEmail(email);                    //현재 로그인한 회원 엔티티를 조회

        Cart cart = cartRepository.findByMemberId(member.getId());              //현재 로그인한 회원의 장바구니 엔티티를 조회
        if (cart == null) {                                                      //상품을 처음으로 장바구니에 이미 들어가 있는지 조회
            cart = Cart.createCart(member);
            cartRepository.save(cart);
        }

        CartItem savedCartItem = cartItemRepository.findByCartIdAndItemId(cart.getId(), item.getId());   //현재 상품이 장바구니에 이미 들어가 있는지 조회

        if (savedCartItem != null) {
            savedCartItem.addCount(cartItemDto.getCount());                     //장바구니에 이미 있던 상품일 경주 기존 수량에 현재 장바구니에 담을 수량 만큼을 더해준다
            return savedCartItem.getId();
        } else {
            CartItem cartItem = CartItem.createCartItem(cart, item, cartItemDto.getCount());    //장바구니 엔티티,상품 엔티티, 장바구니에 담을 수량을 이용하여 cartItem 엔티티를 생성
            cartItemRepository.save(cartItem);                                  //장바구니에 들어갈 상품을 저장
            return cartItem.getId();
        }


    }


    //장바구니에 들어있는 상품을 조회하는 로직
    @Transactional(readOnly = true)
    public List<CartDetailDto> getCartList(String email) {

        List<CartDetailDto> cartDetailDtoList = new ArrayList<>();

        Member member = memberRepository.findByEmail(email);
        Cart cart = cartRepository.findByMemberId(member.getId());  //현재 로그인한 회원의 장바구니 엔티티를 조회한다.
        if (cart == null) {                                         //장바구니에 상품을 한 번도 안 담았을 경우 장바구니 엔티티가 없으므로 빈 리스트를 반환
            return cartDetailDtoList;
        }

        cartDetailDtoList = cartItemRepository.findCartDetailDtoList(cart.getId()); //장바구니에 담겨있는 상품 정보를 조회한다.
        return cartDetailDtoList;
    }

    //상품의 수량을 업데이트하는 로직.
    @Transactional(readOnly = true)
    public boolean validateCartItem(Long cartItemId, String email){
        Member curMember = memberRepository.findByEmail(email);     //현재 로그인한 회원을 조회한다.
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(EntityNotFoundException::new);
        Member savedMember = cartItem.getCart().getMember();        //장바구니 상품을 저장한 회원을 조회한다.

        if(!StringUtils.equals(curMember.getEmail(), savedMember.getEmail())){  // 현재 로그인한 회원과 장바구니 상품을 저장한 회원이 다를 경우 false를,같으면 true를 반환한다.
            return false;
        }

        return true;
    }

    //장바구니 상품의 수량을 업데이트하는 메소드
    public void updateCartItemCount(Long cartItemId, int count){
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(EntityNotFoundException::new);

        cartItem.updateCount(count);
    }

    //상품 번호를 파라미터로 받아서 삭제하는 로직
    public void deleteCartItem(Long cartItemId){
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(EntityNotFoundException::new);
        cartItemRepository.delete(cartItem);
    }

    //주문 로직으로 전달할 orderDto 리스트 생성 및 주문 로직 호출, 주문한 상품은 장바구니에서 제거하는 로직 구현.
    public Long orderCartItem(List<CartOrderDto> cartOrderDtoList, String email){
        List<OrderDto> orderDtoList = new ArrayList<>();

        for (CartOrderDto cartOrderDto : cartOrderDtoList) {    //장바구니 페이지에서 전달받은 주문 상품 번호를 이용하여 주문 로직으로 전달할 orderDto 객체를 만든다.
            CartItem cartItem = cartItemRepository
                    .findById(cartOrderDto.getCartItemId())
                    .orElseThrow(EntityNotFoundException::new);

            OrderDto orderDto = new OrderDto();
            orderDto.setItemId(cartItem.getItem().getId());
            orderDto.setCount(cartItem.getCount());
            orderDtoList.add(orderDto);
        }

        Long orderId = orderService.orders(orderDtoList, email);       // 장바구니에 담은 상품을 주문하도록 주문 로직을 호출

        for (CartOrderDto cartOrderDto : cartOrderDtoList) {            //주문한 상품들을 장바구니에서 제거한다.
            CartItem cartItem = cartItemRepository
                    .findById(cartOrderDto.getCartItemId())
                    .orElseThrow(EntityNotFoundException::new);
            cartItemRepository.delete(cartItem);
        }

        return orderId;
    }

}
