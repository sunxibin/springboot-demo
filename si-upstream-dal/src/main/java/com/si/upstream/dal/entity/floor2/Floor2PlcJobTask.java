package com.si.upstream.dal.entity.floor2;

import com.baomidou.mybatisplus.annotation.TableName;
import com.si.upstream.dal.entity.BaseDO;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName(value = "Floor2PlcJobTask")
public class Floor2PlcJobTask extends Floor2BaseDO implements Serializable {

    private String warehouseCode;

    private String zoneCode;

    private String bucketCode;

    private String sourceStationCode;

    private String sourcePointCode;

    private String targetStationCode;

    private String targetPointCode;

    private String plcJobType;

    private String wcsJobId;

    private String cachePointCode;

    private String cacheWcsJobId;

    private String status;

}
