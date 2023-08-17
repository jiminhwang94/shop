package com.shop.entity;


import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

@EntityListeners(value = {AuditingEntityListener.class})        //Auditing 적용하기 위해서 설정
@MappedSuperclass       //공통 매핑 정보가 필요할때 사용. 부모 클래스를 상속 받는 자식 클래스에 매핑 정보만 제공
@Data
public abstract class BaseTimeEntity {

    @CreatedDate        //엔티티 생성되어 저장될 때 시간을 자동 저장
    @Column(updatable = false)
    private LocalDateTime regTime;

    @LastModifiedDate       //엔티티 값을 변경할 때 자동 저장
    private LocalDateTime updateTime;
}
