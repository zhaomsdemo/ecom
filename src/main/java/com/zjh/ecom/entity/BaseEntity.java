package com.zjh.ecom.entity;

import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

@MappedSuperclass
@Data
@EntityListeners(AuditingEntityListener.class)
public class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    @CreatedDate
    @Column
    @Temporal(TemporalType.DATE)
    private Date createdDate;
    @Column
    @Temporal(TemporalType.DATE)
    private Date modifiedDate;
    @CreatedBy
    @Column
    private String createdUser;
    @LastModifiedBy
    @Column
    private String modifiedUser;
}
