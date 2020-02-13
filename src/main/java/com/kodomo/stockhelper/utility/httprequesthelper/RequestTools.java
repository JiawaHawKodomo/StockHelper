package com.kodomo.stockhelper.utility.httprequesthelper;

import org.springframework.http.*;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

/**
 * @author Shuaiyu Yao
 * @create 2020-02-13 10:38
 **/
public class RequestTools {
    /**
     * 向目的URL发送get请求
     *
     * @param url     目的url
     * @param headers 发送的http头，可在外部设置好参数后传入
     * @return String
     */
    public static String sendGetRequest(String url, HttpHeaders headers) {
        RestTemplate client = new RestTemplate();

        HttpMethod method = HttpMethod.GET;
        // 以表单的方式提交
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        //将请求头部和参数合成一个请求
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(headers);
        //执行HTTP请求，将返回的结构使用String 类格式化
        ResponseEntity<String> response = client.exchange(url, method, requestEntity, String.class);

        return response.getBody();
    }

    public static String sendGetRequest(String url) {
        HttpHeaders httpHeaders = new HttpHeaders();
        return sendGetRequest(url, httpHeaders);
    }
}
