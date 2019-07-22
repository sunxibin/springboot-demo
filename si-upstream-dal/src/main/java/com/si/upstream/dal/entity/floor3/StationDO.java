package com.si.upstream.dal.entity.floor3;

import com.baomidou.mybatisplus.annotation.TableName;
import com.si.upstream.dal.entity.BaseDO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author sunxibin
 */
@Data
@Builder
@TableName(value = "station")
@AllArgsConstructor
@NoArgsConstructor
public class StationDO extends BaseDO implements Serializable {
    /**
     * 区域编码
     */
    private String stationCode;
    /**
     * 区域类型
     */
    private String stationType;

}
