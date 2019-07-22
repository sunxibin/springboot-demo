package demo;

import com.alibaba.fastjson.JSONObject;
import com.si.upstream.common.util.IdCreater;
import com.si.upstream.model.plc.PlcJobMessage;
import com.si.upstream.model.wcs.job.RobotJobVO;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.text.SimpleDateFormat;

/**
 * @author sunxibin
 */
public class SimpleTest {
    public static void main(String[] args) {

    }

    public static void function_001() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd-HHmmssSSS");
        String s = simpleDateFormat.format(System.currentTimeMillis());
        System.out.println(s);
    }

    public static void function_002() {
        RestTemplate restTemplate = new RestTemplate();
//        String url = "http://localhost:9001/hello";
        String url = "http://localhost:9001/greeting";
//        String result = restTemplate.postForObject(url,
//                RobotJobVO.builder().robotJobId(IdCreater.getInnerJobId()).build(),
//                String.class);

        RobotJobVO robotJob = RobotJobVO.builder()
                .robotJobId(IdCreater.getInnerJobId())
                .warehouseId(9001L)
                .build();
        String jsonString = JSONObject.toJSONString(robotJob);

        HttpHeaders header = new HttpHeaders();
        header.setContentType(MediaType.APPLICATION_JSON_UTF8);
//        HttpEntity<RobotJobVO> request = new HttpEntity<>(robotJob, header);
        HttpEntity<String> request = new HttpEntity<>(jsonString, header);
        String result = restTemplate.postForObject(url, request, String.class);

        System.out.println(result);
    }

    public static void function_003() {
        RestTemplate restTemplate = new RestTemplate();
        String url = "http://localhost:9001/a51040_accept?type=5";
        PlcJobMessage message = new PlcJobMessage();
        message.setWarehouseCode("60001");
        String result = restTemplate.postForObject(url, message, String.class);
        System.out.println(result);

        System.out.println(result);
    }
}
