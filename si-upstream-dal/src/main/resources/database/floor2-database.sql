CREATE DATABASE IF NOT EXISTS si_upstream;

CREATE TABLE `Floor2Station` (
     `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '自增主键id',
     `station_code` varchar(100) COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '工作站编号',
     `station_type` int(11) NOT NULL DEFAULT 0 COMMENT '工作站类型',
     `created_date` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
     `updated_date` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '更新时间',
     PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='工作站表';

CREATE TABLE `Floor2Bucket` (
    `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '自增主键id',
    `bucket_code` varchar(100) COLLATE utf8mb4_bin NOT NULL COMMENT '货架码',
    `station_code` varchar(100) COLLATE utf8mb4_bin NOT NULL COMMENT '工作站编号',
    `station_point_code` varchar(45) COLLATE utf8mb4_bin NOT NULL COMMENT '工作站点位编码',
    `station_in_use` varchar(45) COLLATE utf8mb4_bin NOT NULL COMMENT '货架占用工作站编码',
    `point_in_use` varchar(45) COLLATE utf8mb4_bin NOT NULL COMMENT '货架占用点位编码',
    `created_date` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
    `updated_date` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '更新时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='货架表';

CREATE TABLE `Floor2PlcJobTask` (
    `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '自增主键id',
    `warehouse_code` varchar(100) COLLATE utf8mb4_bin  NOT NULL DEFAULT '' COMMENT '仓库ID',
    `zone_code` varchar(100) COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '库区ID',
    `bucket_code` varchar(100) COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '货架编号',
    `source_station_code` varchar(100) COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '出发工作站编号',
    `source_point_code` varchar(100) COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '出发点位编号',
    `target_station_code` varchar(100) COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '目的工作站编号',
    `target_point_code` varchar(100) COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '目的点位编号',
    `plc_job_type` varchar(45) COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT 'PLC任务类型',
    `wcs_job_id` varchar(100) COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT 'WCS移动任务ID',
    `cache_point_code` varchar(100) COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '缓冲区点位编号',
    `cache_wcs_job_id` varchar(100) COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '缓冲移动任务ID',
    `status` varchar(45) COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '状态',
    `created_date` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
    `updated_date` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '更新时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='PLC任务表';


CREATE TABLE `Floor2StationPoint` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '自增主键id',
  `point_code` varchar(100) COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '点位编码',
  `station` varchar(100) COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '工作站编号',
  `point_type` varchar(45) COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '点位类型',
  `status` varchar(45) COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '状态',
  `created_date` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  `updated_date` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='点位表';


CREATE TABLE `Floor2WcsMoveJob` (
    `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '自增主键id',
    `bucket_code` varchar(100) COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '货架编号',
    `point_code` varchar(100) COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '点位编号',
    `warehouse_code` varchar(100) COLLATE utf8mb4_bin NOT  NULL DEFAULT '' COMMENT '仓库编号',
    `zone_code` varchar(100) COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '库区编号',
    `plc_job_type` varchar(45) COLLATE utf8mb4_bin DEFAULT NULL DEFAULT '' COMMENT 'PLC任务类型',
    `wcs_job_id` varchar(100) COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT 'WCS任务ID',
    `plc_task_id` bigint(20) NOT NULL DEFAULT 0 COMMENT 'PLC任务ID',
    `status` varchar(45) COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '状态',
    `created_date` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
    `updated_date` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '更新时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='WCS移动任务表';


