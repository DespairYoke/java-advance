package com.imooc.web.domain;

/**
 *  用户模型
 *
 * @author 小马哥
 * @since 2018/5/27
 */
public class User {

    private Long id;

    private String name;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
