package com.si.upstream.core.floor2;

import org.springframework.http.*;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;

@Component
public class Floor2WcsStandardRestTemplate {

    @Resource
    private ClientHttpRequestFactory clientHttpRequestFactory;

    @Resource
    private RestTemplate restTemplate;

    public WcsStandardResponse post(String url, String body) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        return post(url, new HttpEntity<>(body, headers));
    }

    private WcsStandardResponse post(String url, HttpEntity requestEntity) {
        ResponseEntity<WcsStandardResponse> resultResponseEntity = this.restTemplate.exchange(url, HttpMethod.POST, requestEntity, WcsStandardResponse.class);
        if (resultResponseEntity.getStatusCode() == HttpStatus.OK) {
            return resultResponseEntity.getBody();
        }
        return null;
    }

}
