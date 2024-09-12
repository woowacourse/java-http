package org.apache.coyote.http11.handler;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

public class RequestParameters {
    private final Map<String, String> requestParams;

    private RequestParameters(Map<String, String> requestParams) {
        this.requestParams = Map.copyOf(requestParams);
    }

    public static RequestParameters parseFrom(String paramString) {
        Map<String, String> requestParams = new HashMap<>();
        String[] requestParamTokens = paramString.split("&");
        for (String requestParam : requestParamTokens) {
            String[] split = requestParam.split("=");
            requestParams.put(split[0], split[1]);
        }
        return new RequestParameters(requestParams);
    }

    public String getParam(String key) {
        return Optional.ofNullable(requestParams.get(key))
                .orElseThrow(() -> new NoSuchElementException(key + " 에 해당하는 값이 존재하지 않습니다."));
    }
}
