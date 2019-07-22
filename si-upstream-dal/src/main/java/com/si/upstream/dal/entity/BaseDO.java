package com.si.upstream.dal.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author sunxibin
 */
@Data
public abstract class BaseDO {
    /**
     * 自增id
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 仓库编号
     */
    private String warehouseCode;
    /**
     * 库区编号
     */
    private String zoneCode;
    /**
     * 创建日期
     */
    private Date createdDate;
    /**
     * 更新日期
     */
    private Date updatedDate;
}
