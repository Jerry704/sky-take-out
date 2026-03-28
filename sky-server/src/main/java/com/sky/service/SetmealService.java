package com.sky.service;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.vo.SetmealVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;


public interface SetmealService {



    /**
     * 新增套餐，同時需要保存套餐和蔡品關聯
     * @param setmealDTO
     */
    void saveWithDish(SetmealDTO setmealDTO);


    /**
     * 分頁查詢
     * @param setmealPageQueryDTO
     *
     */
    PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO);

    /**
     * 批量刪除套餐
     * @param ids
     *
     */
    void deleteBatch(List<Long> ids);

    /**
     * 靠id查套餐，回傳前端用於修改
     * @param id
     *
     */
    SetmealVO getByIdWithDish(Long id);

    /**
     * 修改套餐
     * @param setmealDTO
     *
     */
    void update(SetmealDTO setmealDTO);

    /**
     * 套餐起售停售
     * @param status
     * @param id
     *
     */
    void startOrStop(Integer status, Long id);
}
