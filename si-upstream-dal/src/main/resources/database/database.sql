CREATE DATABASE IF NOT EXISTS si_upstream;


DROP TABLE IF EXISTS `station`;
CREATE TABLE `station` (
  `id` BIGINT(19) UNSIGNED AUTO_INCREMENT COMMENT '自增主键id',
  `warehouse_code` VARCHAR(32) NOT NULL COMMENT '仓库ID',
  `zone_code` VARCHAR(32) NOT NULL COMMENT '库区ID对应二楼三楼',
  `station_code` VARCHAR(32) NOT NULL COMMENT '工作站编号',
  `station_type` VARCHAR(32) NOT NULL COMMENT '工作站类型',
  `created_date` DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建日期',
  `updated_date` DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '更新日期',
  PRIMARY KEY (`id`),
  UNIQUE INDEX `station_code` (`station_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='区域表';


DROP TABLE IF EXISTS `way_point`;
CREATE TABLE `way_point` (
  `id` BIGINT(19) UNSIGNED AUTO_INCREMENT COMMENT '自增主键id',
  `warehouse_code` VARCHAR(32) NOT NULL COMMENT '仓库ID',
  `zone_code` VARCHAR(32) NOT NULL COMMENT '库区ID对应二楼三楼',
  `point_code` VARCHAR(32) NOT NULL COMMENT '点位编码',
  `upstream_code` VARCHAR(32) DEFAULT NULL COMMENT '上游点位编码',
  `station` VARCHAR(32) NOT NULL COMMENT '点位所属区域',
  `point_type` int(4) NOT NULL COMMENT '点位类型',
  `occupied_state` tinyint(2) DEFAULT 0 COMMENT '点位占用状态',
  `created_date` DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建日期',
  `updated_date` DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '更新日期',
  PRIMARY KEY (`id`),
  UNIQUE INDEX `point_code` (`point_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='点位表';


DROP TABLE IF EXISTS `upstream_job`;
DROP TABLE IF EXISTS `upstream_job`;
CREATE TABLE `upstream_job` (
  `id` BIGINT(19) UNSIGNED AUTO_INCREMENT COMMENT '自增主键id',
  `warehouse_code` VARCHAR(32) NOT NULL COMMENT '仓库ID',
  `zone_code` VARCHAR(32) NOT NULL COMMENT '库区ID对应二楼三楼',
  `job_type` VARCHAR(32) NOT NULL COMMENT '任务类型：第二次用料，第二次补料，第一次用料，第一次补料',
  `order_time` int(4) DEFAULT 1 COMMENT '叫料任务的次序',
  `start_point` VARCHAR(32) DEFAULT NULL COMMENT '任务起始点',
  `start_station` VARCHAR(32) DEFAULT NULL COMMENT '任务起始区域',
  `end_point` VARCHAR(32) DEFAULT NULL COMMENT '任务目标点',
  `end_station` VARCHAR(32) DEFAULT NULL COMMENT '任务目标区域',
  `status` VARCHAR(32) NOT NULL COMMENT '任务状态',
  `real_start_point` VARCHAR(32) DEFAULT NULL COMMENT '任务的实际起始点',
  `real_end_point` VARCHAR(32) DEFAULT NULL COMMENT '任务的实际目标点',
  `next_job_id` BIGINT(16) DEFAULT NULL COMMENT '同组下一个任务的id',
  `created_date` DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建日期',
  `updated_date` DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '更新日期',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='PLC任务表';


DROP TABLE IF EXISTS `inner_job`;
CREATE TABLE `inner_job` (
  `id` BIGINT(19) UNSIGNED AUTO_INCREMENT COMMENT '自增主键id',
  `warehouse_code` VARCHAR(32) NOT NULL COMMENT '仓库ID',
  `zone_code` VARCHAR(32) NOT NULL COMMENT '库区ID对应二楼三楼',
  `upstream_job_id` BIGINT(16) NOT NULL COMMENT '上游任务编号',
  `inner_job_id` VARCHAR(32) NOT NULL COMMENT '任务编号',
  `type` VARCHAR(32) NOT NULL COMMENT '任务类型',
  `let_down_flag` VARCHAR(32) DEFAULT NULL COMMENT '任务到达后是否放下货架',
  `flag` TINYINT(1) DEFAULT 0 COMMENT '任务完成是否需要发送进站请求',
  `request_type` int(4) DEFAULT 0 COMMENT 'agv进站请求的类型',
  `plc_station_code` VARCHAR(32) DEFAULT NULL COMMENT '需要发起上报的目标作业台:进站申请，任务完成',
  `source_point` VARCHAR(32) DEFAULT NULL COMMENT '任务起始点',
  `target_point` VARCHAR(32) DEFAULT NULL COMMENT '任务目标点',
  `agv_end_point` VARCHAR(32) DEFAULT NULL COMMENT 'AGV停靠目标点',
  `status` VARCHAR(32) NOT NULL COMMENT '任务状态',
  `bucket_move_type` VARCHAR(32) DEFAULT '' COMMENT 'bucketMove的类型',
  `created_date` DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建日期',
  `updated_date` DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '更新日期',
  PRIMARY KEY (`id`),
  UNIQUE INDEX `inner_job_id` (`inner_job_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='内部任务表';