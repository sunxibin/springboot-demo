package com.si.upstream.dal.das.floor2;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.si.upstream.dal.entity.floor2.Floor2StationPoint;
import com.si.upstream.dal.mapper.floor2.Floor2StationPointMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;


@Service
public class Floor2StationPointDas {

    @Resource
    private Floor2StationPointMapper mapper;

    public Integer insert(Floor2StationPoint point) {
        return mapper.insert(point);
    }

    public Integer delete(Long id) {
        return mapper.deleteById(id);
    }

    public Integer update(Floor2StationPoint point) {
        UpdateWrapper wrapper = new UpdateWrapper();
        wrapper.eq("id", point.getId());
        return mapper.update(point, wrapper);
    }

    public Integer updateOccupiedState(String pointCode, String occupiedState) {
        if(StringUtils.isBlank(pointCode)) return 0;
        UpdateWrapper wrapper = new UpdateWrapper();
        Floor2StationPoint where = new Floor2StationPoint();
        where.setPointCode(pointCode);
        wrapper.setEntity(where);

        Floor2StationPoint updItem = new Floor2StationPoint();
        updItem.setStatus(occupiedState);
        return mapper.update(updItem, wrapper);
    }

    public Floor2StationPoint queryById(Long id) {
        return mapper.selectById(id);
    }

    public Floor2StationPoint getByCondition(Floor2StationPoint task) {
        QueryWrapper wrapper = new QueryWrapper(task);
        wrapper.orderByAsc("id");
        return mapper.selectOne(wrapper);
    }

    public List<Floor2StationPoint> queryByCondition(Floor2StationPoint task) {
        QueryWrapper wrapper = new QueryWrapper(task);
        wrapper.orderByAsc("id");
        return mapper.selectList(wrapper);
    }

    public List<Floor2StationPoint> queryByCondition(Floor2StationPoint task, Boolean isStationPoint) {
        QueryWrapper wrapper = new QueryWrapper(task);
        wrapper.orderByAsc("id");
        return mapper.selectList(wrapper);
    }
}
