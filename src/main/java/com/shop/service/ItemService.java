package com.shop.service;

import com.shop.dto.ItemFormDto;
import com.shop.dto.ItemImgDto;
import com.shop.entity.Item;
import com.shop.entity.ItemImg;
import com.shop.repository.ItemImgRepository;
import com.shop.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
//상품 등록하는 곳
public class ItemService {

    private final ItemRepository itemRepository;

    private final ItemImgService itemImgService;

    private final ItemImgRepository itemImgRepository;

    public Long saveItem(ItemFormDto itemFormDto, List<MultipartFile> itemImgFileList) throws Exception{

        //상품 등록
        Item item = itemFormDto.createItem();
        itemRepository.save(item);

        //이미지 등록
        for (int i=0;i<itemImgFileList.size();i++){
            ItemImg itemImg = new ItemImg();
            itemImg.setItem(item);
            if (i==0)
                itemImg.setRepimgYn("Y");
            else
                itemImg.setRepimgYn("N");
            itemImgService.saveItemImg(itemImg,itemImgFileList.get(i));
        }

        return item.getId();
        
    }

    //상품을 불러오는 메소드
    @Transactional(readOnly = true)     //(readOnly = true)는 읽기 전용.
    public ItemFormDto getItemDtl(Long itemId){

        //상품 이미지를 조회한다. 등록순으로 가지고 오기 위해서 이미지 아이디를 오름차순으로 가지고 온다.
        List<ItemImg> itemImgList = itemImgRepository.findByItemIdOrderByIdAsc(itemId);
        List<ItemImgDto> itemImgDtoList = new ArrayList<>();
        
        //조회한 ItemImg 엔티티를 ItemImgDto 객체로 만들어서 리스트에 추가한다.
        for (ItemImg itemImg : itemImgList){
            ItemImgDto itemImgDto = ItemImgDto.of(itemImg);
            itemImgDtoList.add(itemImgDto);
        }

        // 아이디를 통해 상품 조회. 없으면 EntityExistsException 발생
        Item item = itemRepository.findById(itemId)
                .orElseThrow(EntityExistsException::new);
        ItemFormDto itemFormDto = ItemFormDto.of(item);
        itemFormDto.setItemImgDtoList(itemImgDtoList);
        return itemFormDto;

    }

    public Long updateItem(ItemFormDto itemFormDto, List<MultipartFile> itemImgFileList) throws Exception{

        //상품수정
        Item item = itemRepository.findById(itemFormDto.getId())   //상품 등록 하면으로부터 전달 받은 상품 아이디를 이용하여 상품 엔티티 조회
                .orElseThrow(EntityNotFoundException::new);
        item.updateItem(itemFormDto);   // 전달 받은 ItemFormdto를 통해 상품 엔티티 업데이트

        List<Long> itemImgIds = itemFormDto.getItemImgIds();    // 상품 이미지 아이디 리스트를 조회

        //이미지 등록
        for (int i=0;i<itemImgFileList.size();i++){

            //상품 이미지를 업데이트하기 위해서 updateItemImg() 메소드에 상품 이미지 아이디와, 상품 이미지 파일 정보를 파라미터로 전달
            itemImgService.updateItemImg(itemImgIds.get(i),itemImgFileList.get(i));
        }

        return item.getId();
    }
}
