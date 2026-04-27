package com.sky.service.impl;

import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.ShoppingCart;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class ShoppingCartServiceImpl implements ShoppingCartService {

    @Autowired
    private ShoppingCartMapper shoppingCartMapper;

    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private SetmealMapper setmealMapper;

    /**
     * 添加購物車
     *
     * @param shoppingCartDTO
     */
    @Override
    public void addShoppingCart(ShoppingCartDTO shoppingCartDTO) {
        //1.當前商品添加購物車之前，判斷是否已存在
        ShoppingCart shoppingCart = new ShoppingCart();
        //將DTO屬性給購物車
        BeanUtils.copyProperties(shoppingCartDTO, shoppingCart);
        //將userId給購物車
        Long userId = BaseContext.getCurrentId();
        shoppingCart.setUserId(userId);

        List<ShoppingCart> list = shoppingCartMapper.list(shoppingCart);

        //已存在執行修改操作(修改數量)
        if (list != null && list.size() > 0) {
            ShoppingCart cart = list.get(0);
            //update語句
            cart.setNumber(cart.getNumber() + 1);
            shoppingCartMapper.updateNumberById(cart);
        } else {
            //不存在，執行insert 插入一條購物車數據

            //判斷添加到購物車的是單品還是套餐
            Long dishId = shoppingCartDTO.getDishId();
            if (dishId != null) {
                //添加到購物車的是單品
                Dish dish = dishMapper.getById(dishId);
                shoppingCart.setName(dish.getName());
                shoppingCart.setImage(dish.getImage());
                shoppingCart.setAmount(dish.getPrice());

            } else {
                //添加到購物車的是套餐
                Long setmealId = shoppingCartDTO.getSetmealId();
                Setmeal setmeal = setmealMapper.getById(setmealId);
                shoppingCart.setName(setmeal.getName());
                shoppingCart.setImage(setmeal.getImage());
                shoppingCart.setAmount(setmeal.getPrice());
                /**
                 * 注意到這裡沒有setDishFlavor，但是insert後DB會出現正確的dishFlavor，
                 原因在1.前面拷貝DTO屬性時有拷貝
                 2.insert語句中有插入dish_flavor內容，所以DB會顯示正確的dish_flavor
                 */
            }
            //設置數量與時間
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            //插入數據庫
            shoppingCartMapper.insert(shoppingCart);

        }

    }

    /**
     * 查看購物車
     *
     * @return
     */
    @Override
    public List<ShoppingCart> showShoppingCart() {
        //獲取當前用戶id
        Long userId = BaseContext.getCurrentId();
        ShoppingCart shoppingCart = ShoppingCart.builder()
                .userId(userId)
                .build();
        List<ShoppingCart> list = shoppingCartMapper.list(shoppingCart);
        return list;
    }
}
