package com.shop.service;

import com.shop.entity.ItemImg;
import com.shop.repository.ItemImgRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.util.StringUtils;

@Service
@RequiredArgsConstructor
@Transactional
//상품 이미지를 업로드, 저장하기 위해서
public class ItemImgService {

    @Value("${itemImgLocation}")                //application.properites 파일에 등록한 itemImgLocation 값을 불러와서 itemImgLocation 변수에 넣어준다.
    private String itemImglacation;

    private final ItemImgRepository itemImgRepository;

    private final FileService fileService;

    public void saveItemImg(ItemImg itemImg, MultipartFile itemImgFile)throws Exception {
        String oriImgName = itemImgFile.getOriginalFilename();
        String imgName = "";
        String imgUrl = "";

        //파일 업로드
        if (!StringUtils.isEmpty(oriImgName)) {
            // 상품의 이미지를 등룍했다면, 저장할 경로, 파일의 이름, 파일의 바이트 배열 / 파일 업로드 파라미터로 uploadFile 메소드를 호출.
            // 호출 결과 로컬에 저장된 파일의 이름을 imgName 변수에 저장.
            imgName = fileService.uploadFile(itemImglacation, oriImgName, itemImgFile.getBytes());
            //저장한 상품이미지를 불러오는 경로 설정.
            //외부 리소스를 불러오는 urlPatterns로 WebMvcConfig 클래스에서 설정.
            //application.properites에서 설정한 uploadpath 프로퍼티 경로"C:/shop/"아래 item 폴더에 이미지 저장. 그래서 저 경로이다.
            imgUrl = "/images/item/" + imgName;
        }

        //상품 이미지 정보 저장
        //imgName = 실제 로컬에 저장된 상품 이미지의 파일 이름
        //oriImgName = 업로드했던 상품 이미지 파일의 원래 이름
        //imgUrl = 업로드 결과 로컬에 저장된 상품 이미지 파일을 불러오는 경로
        itemImg.updateItemImg(oriImgName, imgName, imgUrl);
        itemImgRepository.save(itemImg);


    }
}
