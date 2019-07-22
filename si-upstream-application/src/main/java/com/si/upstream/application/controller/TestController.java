package com.si.upstream.application.controller;

import com.alibaba.fastjson.JSON;
import com.si.upstream.model.plc.PlcFrameData;
import com.si.upstream.model.plc.PlcJobMessage;
import com.si.upstream.model.wcs.job.RobotJobVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author sunxibin
 * @date 2019/7/4 21:14
 */
@Slf4j
@RestController
public class TestController {

    @RequestMapping("/hello")
    public String hello() {
        return "hello world!";
    }

    @RequestMapping("/greeting")
    public String greeting(@RequestBody RobotJobVO robotJobVO) {
        String robotJobId = robotJobVO.getRobotJobId();
        return robotJobId + "    " + robotJobVO.getWarehouseId();
    }

    @RequestMapping("/a51040_accept")
    public String mockAgvRequestEntry(@RequestParam("type") Integer type, @RequestBody PlcJobMessage plcJobMessage) {
        log.info("receive a message. message=" + plcJobMessage);
        PlcFrameData<PlcJobMessage> data = new PlcFrameData<>();
        PlcJobMessage message = new PlcJobMessage();
        message.setResult(3);
        data.setBody(message);
        return JSON.toJSONString(data);
    }
}
