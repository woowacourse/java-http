package org.apache.coyote.http11.service;

import org.apache.coyote.http11.AcceptableRequest;
import org.apache.coyote.http11.parser.ContentParseResult;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HttpServices {

    static Map<String, HttpService> serviceMap = new HashMap<>();

    static {
        serviceMap.put("/", new HelloService());
        serviceMap.put("/login", new UserService());
        serviceMap.put("/register", new SignService());
    }

    public ContentParseResult processServiceRequest(
            String contentPath,
            Map<String, String> query,
            String method,
            Map<String, String> requestBody
    ) throws IOException {
        validateExistHttpService(contentPath);

        HttpService httpService = serviceMap.get(contentPath);

        if (AcceptableRequest.isGet(method)) {
            return httpService.doGet(query);
        }
        
        if (AcceptableRequest.isPost(method)) {
            return httpService.doPost(requestBody);
        }
        throw new IllegalArgumentException("유효하지 않은 요청입니다");
    }

    private void validateExistHttpService(String contentPath) {
        if (serviceMap.get(contentPath) == null) {
            throw new IllegalArgumentException("처리할 수 없는 요청입니다 " + contentPath);
        }
    }
}
