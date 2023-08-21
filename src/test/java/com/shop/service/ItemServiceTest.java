package com.shop.service;

import com.shop.constant.ItemSellStatus;
import com.shop.dto.ItemFormDto;
import com.shop.entity.Item;
import com.shop.entity.ItemImg;
import com.shop.repository.ItemImgRepository;
import com.shop.repository.ItemRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
@TestPropertySource(locations = "classpath:application-test.properties")
class ItemServiceTest {

    @Autowired
    ItemService itemService;

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    ItemImgRepository itemImgRepository;

    List<MultipartFile> createMultipartFiles() throws Exception{        //가짜 MultipartFile 리스트 만들어서 반환해주는 메소드

        List<MultipartFile> multipartFileList = new ArrayList<>();

        for (int i=0;i<5;i++){
            String path = "C:/shop/item/";          //가상 이미지 파일 저장될 경로
            String imageName = "image" + i + ".jpg";    //가상 이미지 파일 이름 구성
            MockMultipartFile multipartFile = new MockMultipartFile(path,imageName,"image/jpg", new byte[]{1,2,3,4});
            multipartFileList.add(multipartFile);
        }
        return multipartFileList;
    }

    @Test
    @DisplayName("상품 등록 테스트")
    @WithMockUser(username = "admin", roles = "ADMIN")
    void saveItem()throws Exception{
        ItemFormDto itemFormDto = new ItemFormDto();        //상품 등록 화면에서 입력 받는 상품 데이터를 세팅해준다.
        itemFormDto.setItemNm("테스트 상품");
        itemFormDto.setItemSellStatus(ItemSellStatus.SELL);
        itemFormDto.setItemDetail("상품 테스트 입니다.");
        itemFormDto.setPrice(1000);
        itemFormDto.setStockNumber(100);

        //saveItem저장 경로가 없다는 에러 발생. 그래서 저 루트로 폴더 생성 후 테스트 진행. 오류 해결
        List<MultipartFile> multipartFileList = createMultipartFiles();
        Long itemId = itemService.saveItem(itemFormDto, multipartFileList);     //상품 데이터와 이미지 정보를 파라미터로 넘겨서 저장 후 저장된 상품의 아이디 값을 반환 값으로 리턴

        List<ItemImg> itemImgList = itemImgRepository.findByItemIdOrderByIdAsc(itemId);

        Item item = itemRepository.findById(itemId).orElseThrow(EntityNotFoundException::new);

        assertEquals(itemFormDto.getItemNm(), item.getItemNm());        //입력한 상품 데이터와 실제로 저장된 상품 데이터가 같은지 확인
        assertEquals(itemFormDto.getItemSellStatus(),item.getItemSellStatus());
        assertEquals(itemFormDto.getItemDetail(),item.getItemDetail());
        assertEquals(itemFormDto.getPrice(),item.getPrice());
        assertEquals(itemFormDto.getStockNumber(), item.getStockNumber());
        assertEquals(multipartFileList.get(0).getOriginalFilename(),itemImgList.get(0).getOriImgName());    //상품 이미지는 첫 번쨰 파일의 원본 이미지 파일 이름만 같은지 확인.
    }
}
