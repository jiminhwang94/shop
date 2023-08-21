package com.shop.controller;

import com.shop.dto.ItemFormDto;
import com.shop.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.util.List;

@Controller
@RequiredArgsConstructor

public class ItemController {

    private final ItemService itemService;

    @GetMapping(value = "/admin/item/new")
    public String itemForm(Model model) {
        model.addAttribute("itemFormDto", new ItemFormDto());
        return "/item/itemForm";

    }
    //상품 등록
    @PostMapping(value = "/admin/item/new")
    public String itemNew(@Valid ItemFormDto itemFormDto, BindingResult bindingResult,
                          Model model, @RequestParam("itemImgFile")List<MultipartFile>itemImgFileList){

        if (bindingResult.hasErrors()){     //상품 등록 시 필수 값이 없다면 다시 상품 등록 페이지로 전환
            return  "item/itemForm";
        }

        //상품 등록 시 첫 번째 이미지가 없다면 에러 메시지 보이며 상품 등록 페이지로 전환.
        //첫 번째 이미지는 메인 페이지에서 보여줄 상품 이미지로사용하기 위해서 필수 값으로 지정
        if (itemImgFileList.get(0).isEmpty() && itemFormDto.getId() == null){
            model.addAttribute("errorMessage", "첫번쨰 상품 이미지는 필수 입력 값 입니다.");
            return "item/itemForm";
        }

        try {
            itemService.saveItem(itemFormDto, itemImgFileList);     //상품 저장 로직 호출. 매개 변수로 상품 정보와 이미지 정보를 담고 있는 itemImgFileList를 넘겨준다.
        } catch (Exception e){      // 모든 종류의 예외를 처리하기 위한 일반적인 방법. e는 임의로 지정한 변수 이름
            model.addAttribute("errorMessage", "상품 등록 중 에러가 발생하였습니다." );
            return "item/itemForm";
        }

        //상품이 정상적으로 등록 되었다면 메인 페이지로 이동.
        return "redirect:/";

    }

    //상품 조회
    @GetMapping(value = "/admin/item/{itemId}")
    public String itemDtl(@PathVariable("itemId") Long ItemId, Model model){

        try {
            ItemFormDto itemFormDto = itemService.getItemDtl(ItemId);       //조회한 상품 데이터를 모델에 담아서 뷰로 전달
            model.addAttribute("itemFormDto", itemFormDto);
        } catch (EntityNotFoundException e){        // 상품 엔티티가 존재하지 않으면 에러메시지 담아서 상품 등록 페이지로 이동
            model.addAttribute("errorMessage", "존재하지 않는 상품 입니다.");
            model.addAttribute("itemFormDto", new ItemFormDto());
            return "item/itemForm";
        }
        return "item/itemForm";
    }

    //상품 수정
    @PostMapping(value = "/admin/item/{itemId}")
    public String itemUpdate(@Valid ItemFormDto itemFormDto, BindingResult bindingResult, @RequestParam("itemImgFile") List<MultipartFile> itemimgFileList, Model model){

        if (bindingResult.hasErrors()){
            return "item/itemForm";
        }

        if (itemimgFileList.get(0).isEmpty() && itemFormDto.getId() == null){
            model.addAttribute("errorMessage", "첫번째 상품 이미지는 필수 입력 값 입니다.");
        }

        try{
            itemService.updateItem(itemFormDto,itemimgFileList);
        } catch (Exception e){
            model.addAttribute("errorMessage", "상품 수정 중 에러가 발생하였습니다.");
            return "item/itemForm";
        }

        return "redirect:/";
    }

}
