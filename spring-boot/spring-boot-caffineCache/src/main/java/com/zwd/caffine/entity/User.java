package com.zwd.caffine.entity;

import lombok.Data;
import org.hibernate.annotations.Proxy;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author Administrator
 */
@Data
@Table(name = "user")
@Entity
@Proxy(lazy = false)
public class User  {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    private Integer age;

}
