package com.si.upstream.dal.entity.floor2;

import com.baomidou.mybatisplus.annotation.TableName;
import com.si.upstream.dal.entity.BaseDO;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName(value = "Floor2Bucket")
public class Floor2Bucket extends Floor2BaseDO implements Serializable {
    
    private String bucketCode;

    private String stationCode;

    private String stationPointCode;

    private String pointInUse;

    private String stationInUse;

}
