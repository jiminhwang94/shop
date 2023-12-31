package com.shop.service;

import lombok.extern.java.Log;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.util.UUID;

@Service
@Log
public class FileService {

    public String uploadFile(String uploadPath, String originalFileName, byte[] fileData)throws Exception {
        UUID uuid = UUID.randomUUID();      //서로 다른 개체들을 구별하기 위해서 이름을 부여할 때 사용한다.파일의 이름으로 사용하면 파일명 중복 문제 해결.
        //originalFileName 문자열에서 파일 확장자 추출.
        //originalFileName.lastIndexOf(".")은 문자열에서 마지막으로 나타나는 점 위치를 찾는다.
        //originalFileName.substring(..) = originalFileName 문자열에서 이전 단계에서 찾은 점의 인덱스부터 문자열의 끝까지의 부분 문자열을 추출한다.
        String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        String savedFileName = uuid.toString() + extension;     //UUID로 받은 값과 원래 파일의 이름 확장자를 조합해서 저장될 파일 이름 만든다.
        String fileUploadFullUrl = uploadPath + "/" + savedFileName;
        FileOutputStream fos = new FileOutputStream(fileUploadFullUrl);     //FileOutputStream = 바이트 단위의 출력을 내보내는 클래스. 생성자로 파일이 저장될 위치, 출력 스트림 만든다.
        fos.write(fileData);        //fileData를 파일 출력 스트림에 입력한다.
        fos.close();
        return savedFileName;   // 업로드된 파일의 이름을 반환한다.
    }

    public void deleteFile(String filePath)throws Exception{
        File deleteFile = new File(filePath);             //파일이 저장된 경로를 이용하여 파일 객체를 생성.

        if (deleteFile.exists()){       //해당 파일이 존재하면 파일을 삭제한다.
            deleteFile.delete();
            log.info("파일을 삭제하였습니다.");
        } else {
            log.info("파일이 존재하지 않습니다.");
        }
    }

}
