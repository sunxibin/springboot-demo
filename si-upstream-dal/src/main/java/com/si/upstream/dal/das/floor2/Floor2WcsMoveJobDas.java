package com.si.upstream.dal.das.floor2;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.si.upstream.dal.entity.floor2.Floor2WcsMoveJob;
import com.si.upstream.dal.mapper.floor2.Floor2WcsMoveJobMapper;
import org.apache.ibatis.session.RowBounds;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;


@Service
public class Floor2WcsMoveJobDas {

    @Resource
    private Floor2WcsMoveJobMapper mapper;

    public Integer insert(Floor2WcsMoveJob job) {
        return mapper.insert(job);
    }

    public Integer delete(Long id) {
        return mapper.deleteById(id);
    }

    public Integer update(Floor2WcsMoveJob job) {
        UpdateWrapper wrapper = new UpdateWrapper();
        wrapper.eq("id", job.getId());
        return mapper.update(job, wrapper);
    }

    public Floor2WcsMoveJob queryById(Long id) {
        return mapper.selectById(id);
    }

    public Floor2WcsMoveJob getByCondition(Floor2WcsMoveJob job) {
        QueryWrapper wrapper = new QueryWrapper(job);
        return mapper.selectOne(wrapper);
    }

    public List<Floor2WcsMoveJob> queryByCondition(Floor2WcsMoveJob job) {
        QueryWrapper wrapper = new QueryWrapper(job);
        wrapper.orderByAsc("id");
        return mapper.selectList(wrapper);
    }

    public List<Floor2WcsMoveJob> pageStatusJobs(Page<Floor2WcsMoveJob> page, String ... statusList) {
        QueryWrapper wrapper = new QueryWrapper();
        wrapper.orderByAsc("id");
        wrapper.in("status", statusList);
        return mapper.selectPage(page, wrapper).getRecords();
    }

    public List<Floor2WcsMoveJob> queryByCondition(Page<Floor2WcsMoveJob> page, Floor2WcsMoveJob job) {
        QueryWrapper wrapper = new QueryWrapper(job);
        wrapper.orderByAsc("id");
        return mapper.selectPage(page, wrapper).getRecords();
    }

}
