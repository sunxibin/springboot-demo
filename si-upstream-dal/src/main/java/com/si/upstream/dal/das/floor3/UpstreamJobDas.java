package com.si.upstream.dal.das.floor3;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.si.upstream.dal.entity.floor3.UpstreamJobDO;
import com.si.upstream.dal.mapper.floor3.UpstreamJobMapper;
import org.assertj.core.util.Lists;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @author sunxibin
 */
@Service
public class UpstreamJobDas {
    @Resource
    private UpstreamJobMapper mapper;


    public int insert(UpstreamJobDO jobDO) {
        return mapper.insert(jobDO);
    }

    public int delete(Long id) {
        return mapper.deleteById(id);
    }

    public int update(UpstreamJobDO jobDO) {
        UpdateWrapper<UpstreamJobDO> wrapper = new UpdateWrapper<>();
        wrapper.eq("id", jobDO.getId());
        return mapper.update(jobDO, wrapper);
    }

    public int cancelJob(String sourcePointCode, String sourceStationCode, String currentStatus, String targetStatus) {
        UpstreamJobDO jobCarrier = new UpstreamJobDO();
        jobCarrier.setStatus(targetStatus);

        UpdateWrapper<UpstreamJobDO> wrapper = new UpdateWrapper<>();
        wrapper.eq("start_point", sourcePointCode);
        wrapper.eq("start_station", sourceStationCode);
        wrapper.eq("status", currentStatus);
        return mapper.update(jobCarrier, wrapper);
    }

    /**查询专区*/

    /**
     * 查询指定状态的所有任务
     */
    public List<UpstreamJobDO> queryByStatus(String jobStatus, String zoneCode) {
        QueryWrapper<UpstreamJobDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("zone_code", zoneCode);
        queryWrapper.eq("status", jobStatus);
        queryWrapper.orderByAsc("id");
        List<UpstreamJobDO> ret = mapper.selectList(queryWrapper);
        return (null == ret) ? Lists.newArrayList() : ret;
    }

    public List<UpstreamJobDO> queryByCondition(Map<String, Object> columnMap) {
        return mapper.selectByMap(columnMap);
    }

    public UpstreamJobDO queryById(Long id) {
        return mapper.selectById(id);
    }

    public List<UpstreamJobDO> queryInitJobBySourcePoint(String pointCode, String jobStatus) {
        QueryWrapper<UpstreamJobDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("start_point", pointCode);
        queryWrapper.eq("status", jobStatus);
        List<UpstreamJobDO> ret = mapper.selectList(queryWrapper);
        return (null == ret) ? Lists.newArrayList() : ret;
    }

    public List<UpstreamJobDO> queryInitJobBySourceStation(String stationCode, String jobStatus) {
        QueryWrapper<UpstreamJobDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("start_station", stationCode);
        queryWrapper.eq("status", jobStatus);
        List<UpstreamJobDO> ret = mapper.selectList(queryWrapper);
        return (null == ret) ? Lists.newArrayList() : ret;
    }

}
