package com.si.upstream.core.manager;

import com.alibaba.fastjson.JSON;
import com.si.upstream.common.enums.*;
import com.si.upstream.common.util.UrlUtils;
import com.si.upstream.core.task.AgvRequestEntryTask;
import com.si.upstream.dal.das.floor3.InnerJobDas;
import com.si.upstream.dal.das.floor3.StationDas;
import com.si.upstream.dal.das.floor3.UpstreamJobDas;
import com.si.upstream.dal.das.floor3.WayPointDas;
import com.si.upstream.dal.entity.floor3.InnerJobDO;
import com.si.upstream.dal.entity.floor3.UpstreamJobDO;
import com.si.upstream.model.plc.PlcFrameData;
import com.si.upstream.model.plc.PlcJobMessage;
import com.si.upstream.model.wcs.job.JobCompleteVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.io.LineNumberReader;
import java.rmi.server.RemoteServer;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * @author sunxibin
 */
@Slf4j
@Service
public class Floor3WcsMessageManager {
    @Resource
    private UpstreamJobDas upstreamJobDas;
    @Resource
    private InnerJobDas innerJobDas;
    @Resource
    private WayPointDas wayPointDas;
    @Resource
    private StationDas stationDas;
    @Resource
    private RestTemplate restTemplate;
    @Resource
    private Floor3TaskManager floor3TaskManager;
    @Resource(name = "agvRequestThreadPool")
    private ExecutorService executor;

    /**
     * wcs上报任务完成事件的处理
     *
     * @param jobCompleteVO
     * @return 消息处理的结果
     */
    public boolean processJobComplete(JobCompleteVO jobCompleteVO) {
        InnerJobDO innerJobDO = innerJobDas.queryByInnerJobId(jobCompleteVO.getRobotJobId());
        if (null != innerJobDO) {
            UpstreamJobDO upstreamJobDO = upstreamJobDas.queryById(innerJobDO.getUpstreamJobId());
            //标记innerJob的状态为完成
            InnerJobDO jobCarrier = new InnerJobDO();
            jobCarrier.setInnerJobId(innerJobDO.getInnerJobId());
            jobCarrier.setStatus(InnerJobStatus.DONE.name());
            innerJobDas.update(jobCarrier);
            //寻找下一个任务
            InnerJobDO nextJob = nextJob(innerJobDO);
            log.info("innerJobId=" + jobCarrier.getInnerJobId() + ". nextJob=" + nextJob);
            if (null != nextJob) {
                //发送下一个任务
                //TODO 发送失败怎么办
                log.info("send next innerJob, innerJobId=" + nextJob.getInnerJobId());
                boolean result = floor3TaskManager.deliveringJob(nextJob, jobCompleteVO.getBucketCode(), jobCompleteVO.getAgvCode());
            } else {
                //标记上游任务状态为完成，更新点位状态；上报任务完成
                updateState(upstreamJobDO);
                reportCompleted(innerJobDO, upstreamJobDO);
            }
        }
        return true;
    }

    /**
     * 获取该任务组的下一个任务
     */
    private InnerJobDO nextJob(InnerJobDO innerJobDO) {
        //如果当前任务需要发送进站请求
        if (innerJobDO.getFlag()) {
            boolean result = false;
            //如果是进站申请则重复尝试，其他的只发一次
            log.info("need to send agv entry_station request. innerJobId=" + innerJobDO.getInnerJobId());
            result = requestEntry(innerJobDO);
            log.info("agv entry_station request.innerJobId=" + innerJobDO.getInnerJobId() + ", request result=" + result);
            if (result) {
                List<InnerJobDO> list = innerJobDas.queryInitJobByUpstreamJobId(innerJobDO.getUpstreamJobId(), InnerJobStatus.INIT.name());
                if (!CollectionUtils.isEmpty(list)) {
                    InnerJobDO nextJob = list.get(0);
                    return nextJob;
                }
            }
        }
        return null;
    }

    /**
     * 向PLC设备发送进展请求,请求成功返回true
     */
    private boolean requestEntry(InnerJobDO innerJobDO) {
        Future<Boolean> future = executor.submit(new AgvRequestEntryTask(floor3TaskManager, restTemplate, innerJobDO));
        try {
            while (!future.isDone()) { }
            return future.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
            log.info(e.getMessage());
        } catch (ExecutionException e) {
            e.printStackTrace();
            log.info(e.getMessage());
        }
        return false;
    }

    /**
     * 任务完成上报
     */
    private void reportCompleted(InnerJobDO innerJobDO, UpstreamJobDO upstreamJobDO) {
        String url = UrlUtils.getReportCompletedUrl();
        PlcJobMessage message = new PlcJobMessage();
        message.setWarehouseCode(innerJobDO.getWarehouseCode());
        message.setZoneCode(innerJobDO.getZoneCode());
        message.setStationCode(innerJobDO.getPlcStationCode());
        message.setJobResult(JobResult.success.getCode());
        message.setSourcePointCode(upstreamJobDO.getStartPoint());
        message.setTargetPointCode(innerJobDO.getTargetPoint());
        try {
            HttpEntity<String> request = floor3TaskManager.convertHttpEntity(message);
            String result = restTemplate.postForObject(url, request, String.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 更新InnerJob的状态为完成，以及对应点位的占用状态
     */
    private void updateState(UpstreamJobDO upstreamJobDO) {
        UpstreamJobDO upstreamJobCarrier = new UpstreamJobDO();
        upstreamJobCarrier.setId(upstreamJobDO.getId());
        upstreamJobCarrier.setStatus(PlcJobStatus.DONE.name());
        upstreamJobDas.update(upstreamJobCarrier);

        wayPointDas.updateStateByPointCode(upstreamJobDO.getRealStartPoint(), PointState.Free.getCode());
        wayPointDas.updateStateByPointCode(upstreamJobDO.getRealEndPoint(), PointState.Occupied.getCode());
    }

}
