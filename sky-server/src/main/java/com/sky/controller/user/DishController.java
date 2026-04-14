package com.sky.controller.user;

import com.sky.constant.StatusConstant;
import com.sky.entity.Dish;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController("userDishController")
@RequestMapping("/user/dish")
@Slf4j
@Api(tags = "C端-菜色瀏覽接口")
public class DishController {
    @Autowired
    private DishService dishService;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 根據分類id查詢菜餚
     *
     * @param categoryId
     * @return
     */
    @GetMapping("/list")
    @ApiOperation("根據分類id查詢菜餚")
    public Result<List<DishVO>> list(Long categoryId) {

        //構造redis key, dish_分類id
        String key = "dish_" + categoryId;

        //查詢redis是否有菜品數據 (放進去什麼類，取出就什麼類)
        List<DishVO> list = (List<DishVO>) redisTemplate.opsForValue().get(key);
        if (list != null && list.size() > 0) {
            //有則返回，不查DB
            return Result.success(list);
        }

        Dish dish = new Dish();
        dish.setCategoryId(categoryId);
        dish.setStatus(StatusConstant.ENABLE);//查詢起售中的菜品

        //沒有，查DB，將數據放入redis
        list = dishService.listWithFlavor(dish);

        redisTemplate.opsForValue().set(key,list);

        return Result.success(list);
    }

}
