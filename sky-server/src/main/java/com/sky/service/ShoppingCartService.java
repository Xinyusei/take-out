package com.sky.service;

import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;

import java.util.List;

public interface ShoppingCartService {
    /**
     * 添加购物车
     * @param shoppingCartDTO
     * @return
     */
    void addShoppingCart(ShoppingCartDTO shoppingCartDTO);
    /**
     * 查看购物车
     * @return
     */
    List<ShoppingCart> list();

    /**
     * 清空购物车
     * @return
     */
    void cleanShoppingCart();

    /**
     * 删除购物车一个商品
     * @param shoppingCartDTO
     * @return
     */
    void delete(ShoppingCartDTO shoppingCartDTO);
}
