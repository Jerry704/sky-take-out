package com.sky.service;

import com.sky.dto.SetmealDTO;

public interface SetmealService {



    /**
     * 新增套餐，同時需要保存套餐和蔡品關聯
     * @param setmealDTO
     */
    void saveWithDish(SetmealDTO setmealDTO);
}
