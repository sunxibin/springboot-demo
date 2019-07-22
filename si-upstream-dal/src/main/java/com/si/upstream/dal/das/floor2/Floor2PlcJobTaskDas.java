package com.si.upstream.dal.das.floor2;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.si.upstream.dal.entity.floor2.Floor2PlcJobTask;
import com.si.upstream.dal.mapper.floor2.Floor2PlcJobTaskMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;


@Service
public class Floor2PlcJobTaskDas {

    @Resource
    private Floor2PlcJobTaskMapper mapper;

    public Integer insert(Floor2PlcJobTask task) {
        return mapper.insert(task);
    }

    public Integer delete(Long id) {
        return mapper.deleteById(id);
    }

    public Integer update(Floor2PlcJobTask task) {
        UpdateWrapper wrapper = new UpdateWrapper();

        wrapper.eq("id", task.getId());
        return mapper.update(task, wrapper);
    }

    public Integer update(Floor2PlcJobTask task, UpdateWrapper cond) {
        return mapper.update(task, cond);
    }

    public Integer update(Floor2PlcJobTask task, String status) {
        UpdateWrapper wrapper = new UpdateWrapper();
        wrapper.eq("id", task.getId());
        wrapper.eq("status", status);
        return mapper.update(task, wrapper);
    }

    public Floor2PlcJobTask queryById(Long id) {
        return mapper.selectById(id);
    }

    public Floor2PlcJobTask getByCondition(Floor2PlcJobTask task) {
        QueryWrapper wrapper = new QueryWrapper(task);
        wrapper.orderByAsc("id");
        return mapper.selectOne(wrapper);
    }

    public List<Floor2PlcJobTask> queryByCondition(Floor2PlcJobTask task) {
        QueryWrapper wrapper = new QueryWrapper(task);
        wrapper.orderByAsc("id");
        return mapper.selectList(wrapper);
    }

    public List<Floor2PlcJobTask> queryByCondition(QueryWrapper queryWrapper) {
        return mapper.selectList(queryWrapper);
    }


    public List<Floor2PlcJobTask> queryByCondition(Page<Floor2PlcJobTask> page, Floor2PlcJobTask task) {
        QueryWrapper wrapper = new QueryWrapper(task);
        wrapper.orderByAsc("id");
        return mapper.selectPage(page, wrapper).getRecords();
    }

    public List<Floor2PlcJobTask> queryByCondition(Page<Floor2PlcJobTask> page, QueryWrapper wrapper) {
        return mapper.selectPage(page, wrapper).getRecords();
    }
}
