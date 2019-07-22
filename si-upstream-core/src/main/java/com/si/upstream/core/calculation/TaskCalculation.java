package com.si.upstream.core.calculation;

import com.si.upstream.common.enums.PlcJobType;
import com.si.upstream.dal.entity.floor3.UpstreamJobDO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 三楼任务计算辅助类
 *
 * @author sunxibin
 */
public class TaskCalculation {

    /**
     * 将任务按照类型非为四组
     */
    public static Map<String, List<UpstreamJobDO>> getPlcJobGroup(List<UpstreamJobDO> plcJobList) {
        Map<String, List<UpstreamJobDO>> map = new HashMap<>();

        for (UpstreamJobDO jobDO : plcJobList) {
            String jobType = jobDO.getJobType();
            if (PlcJobType.first_time_away_consumer.name().equals(jobType)
                    || PlcJobType.second_time_away_mechanical.name().equals(jobType)
                    || PlcJobType.first_time_to_consumer.name().equals(jobType)
                    || PlcJobType.second_time_to_mechanical.name().equals(jobType)) {
                refreshMap(map, jobDO, jobType);
            }
        }
        return map;
    }

    private static void refreshMap(Map<String, List<UpstreamJobDO>> map, UpstreamJobDO jobDO, String jobType) {
        List<UpstreamJobDO> list = map.get(jobType);
        if (null == list) {
            list = new ArrayList<>();
            map.put(jobType, list);
        }
        list.add(jobDO);
    }
}
