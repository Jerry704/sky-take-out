package com.sky.mapper;

import com.sky.entity.SetmealDish;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

//套餐內的菜品 	一個套餐對應多筆資料
@Mapper
public interface SetmealDishMapper {

    /**
     * 根據菜品id查詢對應的套餐id
     * @param dishIds
     * @return
     */
    List<Long> getSetmealIdsByDishIds(List<Long> dishIds);

    /**
     * 批量保存套餐和菜品的關聯
     * @param setmealDishes
     */
    void insertBatch(List<SetmealDish> setmealDishes);

    /**
     * 靠id刪除套餐和菜品的關聯
     * @param setmealId
     */
    void deleteBySetmealId(Long setmealId);
}
