package com.sky.controller.admin;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.SetmealService;
import com.sky.vo.DishVO;
import com.sky.vo.SetmealVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 套餐管理
 */

@RestController
@Slf4j
@Api(tags = "套餐管理相关接口")
@RequestMapping("/admin/setmeal")
public class SetmealController {
    @Autowired
    SetmealService setmealService;

    /**
     * 业务规则：
     * - 套餐名称唯一
     * - 套餐必须属于某个分类
     * - 套餐必须包含菜品
     * - 名称、分类、价格、图片为必填项
     * - 添加菜品窗口需要根据分类类型来展示菜品
     * - 新增的套餐默认为停售状态
     * <p>
     * 接口设计：
     * - 根据类型查询分类（已完成）
     * - 根据分类id查询菜品
     * - 图片上传（已完成）
     * - 新增套餐
     *
     * @param setmealDTO
     * @return
     */
    @PostMapping()
    @ApiOperation(value = "新增套餐")
    @CacheEvict(cacheNames = "setmealCache", key = "#setmealDTO.categoryId") //将包含该套餐的分类都删了
    public Result<String> save(@RequestBody SetmealDTO setmealDTO) {
        log.info("新增套餐,{}", setmealDTO);
        setmealService.saveWithDish(setmealDTO);
        return Result.success();
    }

    /**
     * 套餐分页查询
     *
     * @param setmealPageQueryDTO
     * @return
     */
    @GetMapping("/page")
    @ApiOperation(value = "套餐分页查询")
    public Result<PageResult> page(SetmealPageQueryDTO setmealPageQueryDTO) {
        log.info("套餐分页查询:{},", setmealPageQueryDTO);
        PageResult pageResult = setmealService.pageQuery(setmealPageQueryDTO);
        return Result.success(pageResult);
    }


   /*
    - 可以一次删除一个套餐，也可以批量删除套餐
    - 起售中的套餐不能删除
    */

    /**
     * 批量删除套餐
     *
     * @param ids
     * @return
     */
    @DeleteMapping()
    @ApiOperation(value = "批量删除套餐")
    @CacheEvict(value = "setmealCache", allEntries = true)
    public Result<String> delete(@RequestParam List<Long> ids) {
        log.info("批量删除套餐,ids为:{}", ids);
        setmealService.deleteBatch(ids);
        return Result.success();
    }


    /**
     * 根据id查询套餐
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    @ApiOperation(value = "根据id查询套餐")
    public Result<SetmealVO> getById(@PathVariable Long id) {
        log.info("查询套餐:{}", id);
        SetmealVO setmealVO = setmealService.getByIdWithDish(id);
        return Result.success(setmealVO);
    }

    @PutMapping
    @ApiOperation(value = "修改套餐")
    @CacheEvict(value = "setmealCache", allEntries = true)
    public Result<String> update(@RequestBody SetmealDTO setmealDTO) {
        log.info("修改套餐,{}", setmealDTO);
        setmealService.update(setmealDTO);
        return Result.success();
    }

    /*
    - 可以对状态为起售的套餐进行停售操作，可以对状态为停售的套餐进行起售操作
    - 起售的套餐可以展示在用户端，停售的套餐不能展示在用户端
    - 起售套餐时，如果套餐内包含停售的菜品，则不能起售
    */

    /**
     * 起售、停售套餐
     *
     * @param status 套餐要修改的状态 1表示起售，0表示停售
     * @param id     套餐id
     * @return
     */
    @PostMapping("/status/{status}")
    @ApiOperation(value = "起售停售套餐")
    @CacheEvict(value = "setmealCache", allEntries = true)
    public Result<String> startOrStop(@PathVariable Integer status, Long id) {
        //套餐状态，1表示起售，0表示停售
        if (status == 1) {
            log.info("起售套餐,id:{}", id);
        } else {
            log.info("停售套餐,id:{}", id);
        }
        setmealService.starOrStop(status, id);
        return Result.success();
    }

}
