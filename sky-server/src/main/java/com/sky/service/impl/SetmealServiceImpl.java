package com.sky.service.impl;


import com.sky.dto.SetmealDTO;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class SetmealServiceImpl implements SetmealService {

    @Autowired
    private SetmealMapper setmealMapper;
    @Autowired
    private SetmealDishMapper setmealDishMapper;
    @Autowired
    private DishMapper dishMapper;

    /**
     * 新增套餐，同時需要保存套餐和蔡品關聯
     *
     * @param setmealDTO
     */
    @Transactional
    @Override
    public void saveWithDish(SetmealDTO setmealDTO) {
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);

        //向套餐表插入數據
        setmealMapper.insert(setmeal);

        //獲取套餐id
        Long setmealId = setmeal.getId();

        //從DTO獲取套餐中每一品項
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        //將套餐中的每一品項，套餐id設一樣
        setmealDishes.forEach(setmealDish -> {setmealDish.setSetmealId(setmealId);});

        setmealDishMapper.insertBatch(setmealDishes);
    }
}
