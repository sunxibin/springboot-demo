package com.si.upstream.dal.das.floor3;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.si.upstream.dal.entity.floor3.InnerJobDO;
import com.si.upstream.dal.mapper.floor3.InnerJobMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @author sunxibin
 */
@Service
public class InnerJobDas {
    @Resource
    private InnerJobMapper mapper;


    public int insert(InnerJobDO jobDO) {
        return mapper.insert(jobDO);
    }

    public int delete(Long id) {
        return mapper.deleteById(id);
    }

    public int update(InnerJobDO jobDO) {
        UpdateWrapper<InnerJobDO> wrapper = new UpdateWrapper<>();
        wrapper.eq("inner_job_id", jobDO.getInnerJobId());
        return mapper.update(jobDO, wrapper);
    }

    /**
     * 查询指定条件的所有任务
     */
    public List<InnerJobDO> queryByCondition(Map<String, Object> columnMap) {
        return mapper.selectByMap(columnMap);
    }

    public InnerJobDO queryByInnerJobId(String innerJobId) {
        QueryWrapper<InnerJobDO> wrapper = new QueryWrapper<>();
        wrapper.eq("inner_job_id", innerJobId);
        return mapper.selectOne(wrapper);
    }

    public List<InnerJobDO> queryInitJobByUpstreamJobId(Long upstreamJobId, String status) {
        QueryWrapper<InnerJobDO> wrapper = new QueryWrapper<>();
        wrapper.eq("upstream_job_id", upstreamJobId);
        wrapper.eq("status", status);
        wrapper.orderByAsc("id");
        return mapper.selectList(wrapper);
    }
}
