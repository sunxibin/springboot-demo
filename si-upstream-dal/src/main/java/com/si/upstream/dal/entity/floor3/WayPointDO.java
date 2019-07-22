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
@TableName(value = "way_point")
@AllArgsConstructor
@NoArgsConstructor
public class WayPointDO extends BaseDO implements Serializable {
    /**
     * 点位编码 ： 内部用
     */
    private String pointCode;
    /**
     * 上游点位编码
     */
    private String upstreamCode;
    /**
     * 点位所属工作站
     */
    private String station;
    /**
     * 点位类型 [
     *              0,      //内侧点位
     *              1,      //外侧点位
     *              2,      //工作站前一个点位
     *              3,      //其他
     *          ]
     */
    private Integer pointType;
    /**
     * 点位是否已经被占用 [
     *                      0,   //未被占用
     *                      1,   //托盘驶离中
     *                      2,   //托盘驶入中
     *                      3,   //已占用
     *                    ]
     */
    private Integer occupiedState;
}
