package com.si.upstream.core.floor2;

import com.si.upstream.common.util.ObjectMappers;
import com.si.upstream.common.util.UrlUtils;
import com.si.upstream.common.util.WarehouseTypeConvert;
import com.si.upstream.model.floor2.wcs.Floor2WcsBucketMoveJob;
import com.si.upstream.model.plc.PlcJobMessage;
import com.si.upstream.model.wcs.job.AgvGoHomeRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class Floor2WcsStandardJob {

    private static final Logger LOGGER = LoggerFactory.getLogger(Floor2WcsStandardJob.class);

    @Resource
    private Floor2WcsStandardRestTemplate floor2WcsStandardRestTemplate;

    public WcsStandardResponse robotMove(String bucketCode, String endPoint, String robotJobId, String warehouseId, String zoneCode) {
        Floor2WcsBucketMoveJob job = Floor2WcsBucketMoveJob.jobCreate(bucketCode, endPoint, robotJobId, WarehouseTypeConvert.fromCode(warehouseId), zoneCode);
        String jobBody = ObjectMappers.mustWriteValue(job);
        String url = UrlUtils.getRobotJobUrl();

        WcsStandardResponse response = null;
        try {
            response = floor2WcsStandardRestTemplate.post(url, jobBody);
        } catch (Exception e) {
            LOGGER.info("floor2 send wcs robot-move request[" + url + "]: throw an exception: " + e.getLocalizedMessage(), e);
        }

        LOGGER.info("floor2 send wcs robot-move request[" + url + "]: " + jobBody + ", res: " + ObjectMappers.mustWriteValue(response));
        return response == null ? WcsStandardResponse.emptyResult() : response;
    }

    public WcsStandardResponse robotAllBack(PlcJobMessage message) {
        AgvGoHomeRequest job = new AgvGoHomeRequest();
        job.setWarehouseCode(message.getWarehouseCode());
        job.setZoneCode(message.getZoneCode());

        String jobBody = ObjectMappers.mustWriteValue(job);
        String url = UrlUtils.getAgvHomeUrl();

        WcsStandardResponse response = null;
        try {
            response = floor2WcsStandardRestTemplate.post(url, jobBody);
        } catch (Exception e) {
            LOGGER.info("floor2 send wcs robot-all-back request[" + url + "]: throw an exception: " + e.getLocalizedMessage(), e);
        }
        LOGGER.info("floor2 send wcs robot-all-back request[" + url + "]: " + jobBody + ", res: " + ObjectMappers.mustWriteValue(response));
        return response == null ? WcsStandardResponse.emptyResult() : response;
    }





}
