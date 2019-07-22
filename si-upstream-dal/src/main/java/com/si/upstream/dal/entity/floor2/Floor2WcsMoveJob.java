package com.si.upstream.dal.entity.floor2;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName(value = "Floor2WcsMoveJob")
public class Floor2WcsMoveJob extends Floor2BaseDO implements Serializable {
    
    private String bucketCode;

    private String pointCode;

    private String warehouseCode;

    private String zoneCode;

    private String wcsJobId;

    private String plcJobType;

    private Long plcTaskId;

    private String status;

}
