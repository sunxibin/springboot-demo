package com.si.upstream.dal.das.floor2;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.si.upstream.dal.entity.floor2.Floor2Station;
import com.si.upstream.dal.entity.floor2.Floor2StationPoint;
import com.si.upstream.dal.mapper.floor2.Floor2StationMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;


@Service
public class Floor2StationDas {

    @Resource
    private Floor2StationMapper mapper;

    public Integer insert(Floor2Station station) {
        return mapper.insert(station);
    }

    public Integer delete(Long id) {
        return mapper.deleteById(id);
    }

    public Integer update(Floor2Station station) {
        UpdateWrapper wrapper = new UpdateWrapper();
        wrapper.eq("id", station.getId());
        return mapper.update(station, wrapper);
    }

    public Floor2Station queryById(Long id) {
        return mapper.selectById(id);
    }

    public Floor2Station getByCondition(Floor2Station station) {
        QueryWrapper wrapper = new QueryWrapper(station);
        wrapper.orderByAsc("id");
        return mapper.selectOne(wrapper);
    }

    public List<Floor2Station> queryByCondition(Floor2Station station) {
        QueryWrapper wrapper = new QueryWrapper(station);
        wrapper.orderByAsc("id");
        return mapper.selectList(wrapper);
    }
}
