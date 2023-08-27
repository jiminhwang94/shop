package com.shop.service;

import com.shop.dto.CartItemDto;
import com.shop.entity.Cart;
import com.shop.entity.CartItem;
import com.shop.entity.Item;
import com.shop.entity.Member;
import com.shop.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;

@Service
@Transactional
@RequiredArgsConstructor
public class CartService {

    //장바구니에 상품을 담는 로직
    private final ItemRepository itemRepository;
    private final MemberRepository memberRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;

    public Long addCart(CartItemDto cartItemDto, String email){

        Item item = itemRepository.findById(cartItemDto.getItemId())            //장바구니에 담을 상품 엔티티 조회
                .orElseThrow(EntityNotFoundException::new);
        Member member = memberRepository.findByEmail(email);                    //현재 로그인한 회원 엔티티를 조회

        Cart cart = cartRepository.findByMemberId(member.getId());              //현재 로그인한 회원의 장바구니 엔티티를 조회
        if (cart == null){                                                      //상품을 처음으로 장바구니에 이미 들어가 있는지 조회
            cart = Cart.createCart(member);
            cartRepository.save(cart);
        }

        CartItem savedCartItem = cartItemRepository.findByCartIdAndItemId(cart.getId(),item.getId());   //현재 상품이 장바구니에 이미 들어가 있는지 조회

        if (savedCartItem != null){
            savedCartItem.addCount(cartItemDto.getCount());                     //장바구니에 이미 있던 상품일 경주 기존 수량에 현재 장바구니에 담을 수량 만큼을 더해준다
            return savedCartItem.getId();
        } else {
            CartItem cartItem = CartItem.createCartItem(cart, item, cartItemDto.getCount());    //장바구니 엔티티,상품 엔티티, 장바구니에 담을 수량을 이용하여 cartItem 엔티티를 생성
            cartItemRepository.save(cartItem);                                  //장바구니에 들어갈 상품을 저장
            return cartItem.getId();
        }
    }
}
