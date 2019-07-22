package com.si.upstream.dal.entity.floor2;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.util.Date;

/**
 * @author sunxibin
 */
@Data
public abstract class Floor2BaseDO {
    /**
     * 自增id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 创建日期
     */
    private Date createdDate;
    /**
     * 更新日期
     */
    private Date updatedDate;
}
