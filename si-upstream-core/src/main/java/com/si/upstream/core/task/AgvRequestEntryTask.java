package com.si.upstream.core.task;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.si.upstream.common.enums.AgvRequestOperationEnum;
import com.si.upstream.common.enums.JobResult;
import com.si.upstream.common.util.UrlUtils;
import com.si.upstream.core.manager.Floor3TaskManager;
import com.si.upstream.dal.entity.floor3.InnerJobDO;
import com.si.upstream.model.plc.PlcFrameData;
import com.si.upstream.model.plc.PlcJobMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.web.client.RestTemplate;

import javax.xml.transform.Result;
import java.util.concurrent.Callable;

/**
 * @author sunxibin
 */
public class AgvRequestEntryTask implements Callable<Boolean> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AgvRequestEntryTask.class);

    private Floor3TaskManager floor3TaskManager;
    private RestTemplate restTemplate;
    private InnerJobDO innerJobDO;

    public AgvRequestEntryTask(Floor3TaskManager floor3TaskManager, RestTemplate restTemplate, InnerJobDO innerJobDO) {
        this.floor3TaskManager = floor3TaskManager;
        this.restTemplate = restTemplate;
        this.innerJobDO = innerJobDO;
    }

    @Override
    public Boolean call() throws InterruptedException {
        boolean flag = false;
        String url = UrlUtils.getRequestEntryUrl();
        PlcJobMessage message = new PlcJobMessage();
        message.setWarehouseCode(innerJobDO.getWarehouseCode());
        message.setZoneCode(innerJobDO.getZoneCode());
        message.setStationCode(innerJobDO.getPlcStationCode());
        message.setOperation(innerJobDO.getRequestType());
        HttpEntity<String> request = floor3TaskManager.convertHttpEntity(message);

        if (AgvRequestOperationEnum.entry_application.getCode() == innerJobDO.getRequestType()) {
            // 没有收到进站许可则一直尝试
            while (!flag) {
                String result = doPost(url, request);
                if (null != result) {
                    PlcFrameData<PlcJobMessage> data = JSON.parseObject(result, new TypeReference<PlcFrameData<PlcJobMessage>>(){});
                    PlcJobMessage message1 = (PlcJobMessage) data.getBody();
                    LOGGER.info("agv entry_station request. upstream result=" + message1.getResult());
                    if (JobResult.allow_entry.getCode() == message1.getResult()) {
                        flag = true;
                    }
                    Thread.sleep(1000);
                }
            }
        } else {
            flag = true;
            String result = doPost(url, request);
        }
        return flag;
    }

    private String doPost(String url, HttpEntity<String> request) {
        String result = null;
        try {
            result = restTemplate.postForObject(url, request, String.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
