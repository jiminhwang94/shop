package com.shop.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
// 가입정보를 담을 dto
public class MemberFormDto {

    private String name;

    private String email;

    private String password;

    private String address;
}
