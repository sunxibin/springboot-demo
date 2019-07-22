package com.si.upstream.application.controller;

import com.si.upstream.common.config.FloorConfiguration;
import com.si.upstream.core.manager.Floor3PlcJobManager;
import com.si.upstream.core.manager.floor2.Floor2PlcJobManager;
import com.si.upstream.model.plc.PlcFrameData;
import com.si.upstream.model.plc.PlcJobMessage;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/api")
public class PlcReceiveController {

    private static final Logger LOGGER = LoggerFactory.getLogger(PlcReceiveController.class);

    /**
     * private String warehouseCode;
     * private String zoneCode;
     * private Integer result;
     * 1 炒料区到灌料区任务下发接口   PLC --> 快仓系统 wz + source.station+point target.station(2) ro
     * 2 灌料区到炒料区任务下发接口   PLC --> 快仓系统 wz + source.station+point target.station
     * 3 任务上报接口              快仓系统 --> PLC ro + stationCode jobResult get ro
     * 4 货架出场接口   3
     * 5 货架出入站申请/确认接口 3
     * 6 AGV一键归巢接口 PLC-->快仓系统
     * 7 任务取消接口 PLC-->快仓系统  sourceStationCode sourcePointCode
     */

    @Resource private Floor2PlcJobManager floor2PlcJobManager;
    @Resource
    private Floor3PlcJobManager floor3PlcJobManager;


    @RequestMapping("/plc_gateway_access")
    public PlcFrameData<PlcJobMessage> plcGatewayAccess(@RequestBody PlcFrameData<PlcJobMessage> plcFrameData) {

        String zoneCode = plcFrameData.getBody().getZoneCode();
        PlcJobMessage res = null;
        if(StringUtils.equals(zoneCode, FloorConfiguration.floor3ZoneCode)) {
            res = floor3PlcJobManager.hand(plcFrameData);
        } else if(StringUtils.equals(zoneCode, FloorConfiguration.floor2ZoneCode)) {
            res = floor2PlcJobManager.hand(plcFrameData);
        } else {

        }
        return PlcFrameData.build(plcFrameData.getType(), res);
    }

}
