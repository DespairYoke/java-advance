package com.imooc.diveinspringboot.service;

/**
 * 计算服务
 *
 * @author 小马哥
 * @since 2018/5/15
 */
public interface CalculateService {

    /**
     * 从多个整数 sum 求和
     * @param values 多个整数
     * @return sum 累加值
     */
    Integer sum(Integer... values);
}
