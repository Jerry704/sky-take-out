package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class DishServiceImpl implements DishService {

    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private DishFlavorMapper dishFlavorMapper;

    @Autowired
    private SetmealDishMapper setmealDishMapper;

    /**
     * 新增菜品和對應口味
     * @param dishDTO
     */
    @Override
    @Transactional //開啟DB事務
    public void saveWithFlavor(DishDTO dishDTO) {

        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        //向菜品表插入1條數據
        dishMapper.insert(dish);

        //獲取insert語句生成的主鍵值
        Long dishId = dish.getId();

        //向口味表插入n條數據
        //從dishDTO中獲取flavors表
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if(flavors != null && flavors.size()>0){
            //先將獲取來的主鍵值遍歷給每個物件
            flavors.forEach(dishFlavor -> {
                dishFlavor.setDishId(dishId);
            });
            //向口味表插入n條數據(批量插入)
            dishFlavorMapper.insertBatch(flavors);
        }
    }

    /**
     * 菜品分頁查詢
     * @param dishPageQueryDTO
     * @return
     */
    public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO){
        PageHelper.startPage(dishPageQueryDTO.getPage(),dishPageQueryDTO.getPageSize());
        Page<DishVO> page = dishMapper.pageQuery(dishPageQueryDTO);
        //封装分页查询结果
        return new PageResult(page.getTotal(),page.getResult());
    }

    /**
     * 菜品批量刪除
     * @param ids
     */
    @Override
    @Transactional
    public void deleteBatch(List<Long> ids) {
        //1.判斷菜品能否刪除(販售中無法刪除)
        for (Long id : ids) {
            Dish dish = dishMapper.getById(id);
            if (dish.getStatus() == StatusConstant.ENABLE){
                //販售中無法刪除
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
        }
        //2.判斷菜品能否刪除(在套餐中無法刪除)
        List<Long> setmealIds =setmealDishMapper.getSetmealIdsByDishIds(ids);
        if (setmealIds != null && setmealIds.size() >0){
            //在套餐中無法刪除
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }
        //3.刪除菜品表中數據
        //for循環會發送大量sql語句，性能較差
       /* for (Long id : ids) {
            dishMapper.deleteById(id);
            //4.刪除菜品關聯口味
            dishFlavorMapper.deleteByDishId(id);
        }*/

        //改良版
        //3.批量刪除菜品表中數據
        //sql: delete from dish where id in(?,?,?)
        dishMapper.deleteByIds(ids);

        //4.批量刪除菜品關聯口味
        //sql: delete from dish_flavor where dish_id in(?,?,?)
        dishFlavorMapper.deleteByDishIds(ids);

    }

    /**
     * 根據id查詢菜品和對應的口味
     * @param id
     * @return
     */
    @Override
    public DishVO getByIdWithFlavor(Long id) {
        //id查數據
        Dish dish = dishMapper.getById(id);

        //id查口味
        List<DishFlavor> dishFlavorList =dishFlavorMapper.getByDishId(id);

        //封裝成VO
        DishVO dishVO = new DishVO();
        BeanUtils.copyProperties(dish,dishVO);
        dishVO.setFlavors(dishFlavorList);

        return dishVO;
    }

    /**
     * 根據id修改菜品和口味
     * @param dishDTO
     * @return
     */
    @Override
    public void updateWithFlavor(DishDTO dishDTO) {

        //dishDTO包含口味訊息，直截傳入update不合適，所以傳入dish
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO,dish);

        //修改菜品基本訊息
        dishMapper.update(dish);
        
        //先刪除原本口味
        dishFlavorMapper.deleteByDishId(dishDTO.getId());
        
        //再新增修改後的口味
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if(flavors != null && flavors.size()>0){
            //先將獲取來的主鍵值遍歷給每個物件
            flavors.forEach(dishFlavor -> {
                dishFlavor.setDishId(dishDTO.getId());
            });
            //向口味表插入n條數據(批量插入)
            dishFlavorMapper.insertBatch(flavors);
        }
    }


    /**
     * 根據分類id查菜品"
     * @param categoryId
     * @return
     */
    @Override
    public List<Dish> list(Long categoryId) {
        Dish dish = Dish.builder()
                .categoryId(categoryId)
                .status(StatusConstant.ENABLE)
                .build();
        return dishMapper.list(dish);
    }
}
