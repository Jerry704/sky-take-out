package com.sky.service.impl;


import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;


import com.github.pagehelper.Page;
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

    /**
     * 分頁查詢
     * @param setmealPageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO) {
        // 前端想要的頁碼
        int pageNum = setmealPageQueryDTO.getPage();
        //每頁顯示幾筆
        int pageSize = setmealPageQueryDTO.getPageSize();

        PageHelper.startPage(pageNum, pageSize);
        Page<SetmealVO> page = setmealMapper.pageQuery(setmealPageQueryDTO);
                                //總筆數       ,   list資料
        return new PageResult(page.getTotal(), page.getResult());

    }

    /**
     * 批量刪除套餐
     * @param ids
     * @return
     */
    @Transactional
    @Override
    public void deleteBatch(List<Long> ids) {
        //1.對每個id查詢販售狀態
        ids.forEach(id -> {
            Setmeal setmeal = setmealMapper.getById(id);
            if (StatusConstant.ENABLE == setmeal.getStatus()){
                //販售中不能刪除
                throw  new DeletionNotAllowedException(MessageConstant.SETMEAL_ON_SALE);
            }
        });

        //2.不在販售中則刪除
        ids.forEach(setmealId -> {
            //刪除套餐表(1)中資料
            setmealMapper.deleteById(setmealId);
            //刪除套餐內的菜品(多)資料
            setmealDishMapper.deleteBySetmealId(setmealId);
        });
    }
}
