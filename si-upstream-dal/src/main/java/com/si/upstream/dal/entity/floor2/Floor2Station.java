package com.si.upstream.dal.entity.floor2;

import com.baomidou.mybatisplus.annotation.TableName;
import com.si.upstream.dal.entity.BaseDO;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName(value = "Floor2Station")
public class Floor2Station extends Floor2BaseDO implements Serializable {
    
    private String stationCode;

    private String stationType;

}
