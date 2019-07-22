package com.si.upstream.dal.entity.floor2;

import com.baomidou.mybatisplus.annotation.TableName;
import com.si.upstream.dal.entity.BaseDO;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName(value = "Floor2StationPoint")
public class Floor2StationPoint extends Floor2BaseDO implements Serializable {
    
    private String station;

    private String pointCode;

    private String pointType;

    private String status;
}
