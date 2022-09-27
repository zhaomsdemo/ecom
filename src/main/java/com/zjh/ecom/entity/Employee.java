package com.zjh.ecom.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Version;

@Entity
@Table(name = "employee")
@Data
@EqualsAndHashCode(callSuper = false)
public class Employee extends BaseEntity{

    @Column
    private String empName;
    @Column
    private Integer salary;
    @Version
    @Column(columnDefinition = "default 0")
    private Integer version;
}
