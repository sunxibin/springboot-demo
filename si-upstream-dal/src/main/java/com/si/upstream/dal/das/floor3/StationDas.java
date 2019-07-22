package com.si.upstream.dal.das.floor3;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.si.upstream.dal.entity.floor3.StationDO;
import com.si.upstream.dal.mapper.floor3.StationMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @author sunxibin
 */
@Service
public class StationDas {
    @Resource
    private StationMapper mapper;


    public int insert(StationDO stationDO) {
        return mapper.insert(stationDO);
    }

    public int delete(Long id) {
        return mapper.deleteById(id);
    }

    public int update(StationDO stationDO) {
        UpdateWrapper<StationDO> wrapper = new UpdateWrapper<>();
        wrapper.eq("station_code", stationDO.getStationCode());
        return mapper.update(stationDO, wrapper);
    }

    /**查询专区*/

    /**
     * 查询指定状态的所有任务
     */
    public List<StationDO> queryByCondition(Map<String, Object> columnMap) {
        return mapper.selectByMap(columnMap);
    }

    public StationDO queryByStationCode(String stationCode) {
        QueryWrapper<StationDO> wrapper = new QueryWrapper<>();
        wrapper.eq("station_code", stationCode);
        return mapper.selectOne(wrapper);
    }
}
