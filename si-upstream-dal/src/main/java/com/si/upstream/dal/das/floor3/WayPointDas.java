package com.si.upstream.dal.das.floor3;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.si.upstream.dal.entity.floor3.WayPointDO;
import com.si.upstream.dal.mapper.floor3.WayPointMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author sunxibin
 */
@Service
public class WayPointDas {
    @Resource
    private WayPointMapper mapper;


    public int insert(WayPointDO point) {
        return mapper.insert(point);
    }

    public int delete(Long id) {
        return mapper.deleteById(id);
    }

    public int update(WayPointDO point) {
        UpdateWrapper<WayPointDO> wrapper = new UpdateWrapper<>();
        wrapper.eq("point_code", point.getPointCode());
        return mapper.update(point, wrapper);
    }

    public int updateStateByPointCode(String pointCode, int occupiedState) {
        WayPointDO pointCarrier = new WayPointDO();
        pointCarrier.setPointCode(pointCode);
        pointCarrier.setOccupiedState(occupiedState);
        UpdateWrapper<WayPointDO> wrapper = new UpdateWrapper<>();
        wrapper.eq("point_code", pointCode);
        return mapper.update(pointCarrier, wrapper);
    }

    /**
     * 查询专区
     */
    public WayPointDO queryByPointCode(String pointCode) {
        QueryWrapper<WayPointDO> wrapper = new QueryWrapper<>();
        wrapper.eq("point_code", pointCode);
        return mapper.selectOne(wrapper);
    }

    public WayPointDO queryByUpstreamCode(String upstreamCode) {
        QueryWrapper<WayPointDO> wrapper = new QueryWrapper<>();
        wrapper.eq("upstream_code", upstreamCode);
        return mapper.selectOne(wrapper);
    }

    public WayPointDO queryById(Long id) {
        return mapper.selectById(id);
    }

    public List<WayPointDO> queryByCondition(Map<String, Object> columnMap) {
        return mapper.selectByMap(columnMap);
    }

    public List<WayPointDO> queryByStations(List<String> list) {
        QueryWrapper<WayPointDO> wrapper = new QueryWrapper<>();
        wrapper.in("station", list);
        return mapper.selectList(wrapper);
    }

}
