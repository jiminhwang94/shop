package com.shop.entity;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Table(name = "cart")
@Getter @Setter
@ToString
public class Cart {

    @Id
    @Column(name = "cart_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)           //1:1 매핑 / 지연 로딩
    @JoinColumn(name = "member_id") //member_id라는 외래키 지정. 왜? 매핑을 해야해서.
    private Member member;
}
