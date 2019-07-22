package com.si.upstream.application.controller;

import com.si.upstream.core.manager.Floor3WcsMessageManager;
import com.si.upstream.core.manager.floor2.Floor2WcsMessageManager;
import com.si.upstream.model.wcs.job.JobCompleteVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author sunxibin
 */
@Slf4j
@RestController
@RequestMapping("/wcs/message")
public class WcsMessageController {

    @Resource
    private Floor3WcsMessageManager floor3WcsMessageManager;

    @Resource
    private Floor2WcsMessageManager floor2WcsMessageManager;


    @RequestMapping("/complete/3F")
    public String innerJobCompleteProcess3F(@RequestBody JobCompleteVO jobCompleteVO) {
        log.info("receive wcs jobCompleteVO: {}", jobCompleteVO);
        return floor3WcsMessageManager.processJobComplete(jobCompleteVO) ? "success" : "failure";
    }

    @RequestMapping("/complete/2F")
    public String innerJobCompleteProcess2F(@RequestBody JobCompleteVO jobCompleteVO) {
        return floor2WcsMessageManager.processJobComplete(jobCompleteVO) ? "success" : "failure";
    }

}
