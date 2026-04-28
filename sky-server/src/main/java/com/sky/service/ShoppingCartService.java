package com.sky.service;

import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;

import java.util.List;

public interface ShoppingCartService {

    /**
     * 添加購物車
     * @param shoppingCartDTO
     */
    void addShoppingCart(ShoppingCartDTO shoppingCartDTO);

    /**
     * 查看購物車
     *
     */
    List<ShoppingCart> showShoppingCart();

    /**
     * 清空購物車
     *
     */
    void cleanShoppingCart();

    /**
     * 刪除購物車中的一個商品
     * @param shoppingCartDTO
     *
     */
    void subShoppingCart(ShoppingCartDTO shoppingCartDTO);
}
