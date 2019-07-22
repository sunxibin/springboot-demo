package com.si.upstream.dal.das.floor2;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.si.upstream.dal.entity.floor2.Floor2Bucket;
import com.si.upstream.dal.entity.floor2.Floor2StationPoint;
import com.si.upstream.dal.mapper.floor2.Floor2BucketMapper;
import com.si.upstream.dal.mapper.floor2.Floor2StationPointMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;


@Service
public class Floor2BucketDas {

    @Resource
    private Floor2BucketMapper mapper;

    public Integer insert(Floor2Bucket bucket) {
        return mapper.insert(bucket);
    }

    public Integer delete(Long id) {
        return mapper.deleteById(id);
    }

    public Integer update(Floor2Bucket bucket) {
        UpdateWrapper wrapper = new UpdateWrapper();
        wrapper.eq("id", bucket.getId());
        return mapper.update(bucket, wrapper);
    }

    public Floor2Bucket queryById(Long id) {
        return mapper.selectById(id);
    }

    public Floor2Bucket getByCondition(Floor2Bucket bucket) {
        QueryWrapper wrapper = new QueryWrapper(bucket);
        wrapper.orderByAsc("id");
        return mapper.selectOne(wrapper);
    }

    public List<Floor2Bucket> queryByCondition(Floor2Bucket bucket) {
        QueryWrapper wrapper = new QueryWrapper(bucket);
        wrapper.orderByAsc("id");
        return mapper.selectList(wrapper);
    }
}
