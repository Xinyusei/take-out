package com.sky.controller.admin;


import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/admin/dish")
@Slf4j
@Api(tags = "菜品相关接口")
public class DishController {

    @Autowired
    private DishService dishService;


    /**
     * 新增菜品
     *
     * @param dishDTO
     * @return
     */
    @PostMapping()
    @ApiOperation(value = "新增菜品")
    public Result<String> save(@RequestBody DishDTO dishDTO) {
        log.info("新增菜品和对应的口味:{}", dishDTO);
        //TODO 涉及多个表 要注意函数命名

        String key = "dish_" + dishDTO.getCategoryId();
        cleanCache(key);
        return Result.success();
    }

    /**
     * 菜品分页查询
     *
     * @param dishPageQueryDTO
     * @return
     */
    @GetMapping("/page")
    @ApiOperation(value = "菜品分页查询")
    public Result<PageResult> page(DishPageQueryDTO dishPageQueryDTO) {
        log.info("菜品分页查询:{}", dishPageQueryDTO);
        //调用service层接口
        PageResult pageResult = dishService.pageQuery(dishPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 删除菜品
     */

    @DeleteMapping()
    @ApiOperation(value = "(批量)删除菜品")
    public Result<String> delete(@RequestParam List<Long> ids) {
        log.info("菜品批量删除:{}", ids);
        dishService.deleteBatch(ids);

        //TODO 将所有包含该餐品的缓存数据全部删掉
        // 将所有的菜品缓存数据清理掉，所有以dish_开头的key
        cleanCache("dish_*");
        return Result.success();
    }

    /*
    - 根据id查询菜品
    - 根据类型查询分类(已实现)
    - 文件上传(已实现)
    - 修改菜品
    */

    /**
     * 根据id查询菜品
     *
     * @param id 菜品的id
     * @return
     */
    @GetMapping("/{id}")
    @ApiOperation(value = "根据id查询菜品")
    public Result<DishVO> getById(@PathVariable Long id) {
        log.info("查询菜品,id: {}", id);
        DishVO dishVO = dishService.getByIdWithFlavor(id);
        return Result.success(dishVO);
    }


    @PutMapping()
    @ApiOperation(value = "修改菜品")
    public Result<String> update(@RequestBody DishDTO dishDTO) {
        log.info("修改菜品,{}", dishDTO);
        dishService.update(dishDTO);

        //TODO 将所有包含该餐品的缓存数据全部删掉
        // 获取所有以dish_开头的key
        cleanCache("dish_*");

        return Result.success();
    }


    /**
     * 根据category ID 查询
     *
     * @param categoryId
     * @return
     */
    @GetMapping("/list")
    @ApiOperation(value = "根据分类id查询菜品")
    public Result<List<Dish>> getByCategoryId(@RequestParam Long categoryId) {
        log.info("查询分类号为: {} 的菜品", categoryId);
        List<Dish> dishList = dishService.getByCategoryId(categoryId);
        return Result.success(dishList);
    }

    /**
     * 起售、停售菜品
     *
     * @param status 1-代表起售,0-代表停售
     * @param id     菜品的id
     * @return
     */
    @PostMapping("/status/{status}")
    @ApiOperation("菜品起售、停售接口")
    public Result<String> startOrStop(@PathVariable Integer status, Long id) {
        if (status == 1) {
            log.info("起售菜品,id为:{}", id);
        } else if (status == 0) {
            log.info("仅售菜品,id为:{}", id);
        }

        //TODO 将所有包含该餐品的缓存数据全部删掉
        // 获取所有以dish_开头的key
        cleanCache("dish_*");
        dishService.startOrStop(status, id);
        return Result.success();
    }

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 清理缓存数据
     *
     * @param pattern
     */
    private void cleanCache(String pattern) {

        Set keys = redisTemplate.keys(pattern);
        redisTemplate.delete(keys);
    }
}
