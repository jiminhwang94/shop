package com.shop.dto;

import com.shop.entity.ItemImg;
import lombok.Data;
import org.modelmapper.ModelMapper;

@Data
public class ItemImgDto {

    private Long id;

    private String imgName;

    private String oriImgName;

    private String imgUrl;

    private String repImgYn;

    //멤버 변수로 ModelMapper객체 추가
    private static ModelMapper modelMapper = new ModelMapper();

    //ItemImg 엔티티 객체를 파라미터로 받아서 ItemImg 객체의 자료형과 이름이 같을 때 ItemImgDto로 값을 복사해서 반환한다.
    //static 메소드로 선언해서 ItemImgDto객체를 생성하지 않아도 호출하도록 설정.
    public static ItemImgDto of(ItemImg itemImg){
        return modelMapper.map(itemImg, ItemImgDto.class);
    }
}
