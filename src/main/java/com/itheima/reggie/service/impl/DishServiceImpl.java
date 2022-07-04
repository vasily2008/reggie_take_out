package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.DishFlavor;
import com.itheima.reggie.mapper.DishMapper;
import com.itheima.reggie.service.DishFlavorService;
import com.itheima.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {
    /**
     * 新增菜品，同时保存对应的口味数据
     * @param dishDto
     */
    @Autowired
    private DishFlavorService dishFlavorService;
    @Override
    @Transactional
    public void saveWithFlavor(DishDto dishDto) {

        this.save(dishDto);

        Long dishId = dishDto.getId();

        List<DishFlavor> flavors = dishDto.getFlavors();

        List<DishFlavor> list = new ArrayList<>();

        for (DishFlavor flavor : flavors) {

            flavor.setDishId(dishId);

            list.add(flavor);
        }

//        flavors = flavors.stream().map((item)->{
//            item.setDishId(dishId);
//            return item;
//        }).collect(Collectors.toList());

        dishFlavorService.saveBatch(list);

    }

    @Override
    public DishDto getByIdWithFlavor(Long id) {

        Dish dish = this.getById(id);

        DishDto dishDto = new DishDto();

        BeanUtils.copyProperties(dish,dishDto);

        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.eq(DishFlavor::getDishId,id);

        List<DishFlavor> list = dishFlavorService.list(queryWrapper);

        dishDto.setFlavors(list);

        return dishDto;
    }

    @Override
    @Transactional
    public void updateWithFlavor(DishDto dishDto) {

        this.updateById(dishDto);

        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.eq(DishFlavor::getDishId,dishDto.getId());

        dishFlavorService.remove(queryWrapper);

        List<DishFlavor> flavors = dishDto.getFlavors();

        flavors = flavors.stream().map((item) -> {
            item.setDishId(dishDto.getId());
            return item;
        }).collect(Collectors.toList());

        dishFlavorService.saveBatch(flavors);
    }
}
