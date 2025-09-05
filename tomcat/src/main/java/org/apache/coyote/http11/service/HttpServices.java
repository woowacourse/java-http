package org.apache.coyote.http11.service;

import org.apache.coyote.http11.parser.ContentParseResult;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HttpServices {

    static Map<String, HttpService> serviceMap = new HashMap<>();

    static {
        serviceMap.put("/", new HelloService());
        serviceMap.put("/login", new UserService());
    }

    public ContentParseResult processServiceRequest(String contentPath, Map<String, String> query) throws IOException {
        validateExistHttpService(contentPath);

        HttpService httpService = serviceMap.get(contentPath);
        return httpService.doRequest(query);
    }

    private void validateExistHttpService(String contentPath) {
        if (serviceMap.get(contentPath) == null) {
            throw new IllegalArgumentException("처리할 수 없는 요청입니다 " + contentPath);
        }
    }
}
