package org.apache.coyote.http11;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.uriprocessor.FileUriHandler;
import org.apache.coyote.http11.uriprocessor.LoginUriHandler;
import org.apache.coyote.http11.uriprocessor.RootUriHandler;
import org.apache.coyote.http11.uriprocessor.UriHandler;

public enum HandlerManager {
    ROOT(new RootUriHandler()),
    LOGIN(new LoginUriHandler()),
    FILE(new FileUriHandler());

    private final UriHandler uriHandler;

    HandlerManager(UriHandler uriHandler) {
        this.uriHandler = uriHandler;
    }

    public static UriResponse getUriResponse(String uri) throws IOException {
        UriHandler handler = getHandler(uri);

        String path = uri;
        Map<String, Object> parameters = new HashMap<>();

        int index = uri.indexOf("?");
        if (index != -1) {
            path = uri.substring(0, index);
            String queryString = uri.substring(index + 1);

            parameters = getParameters(queryString);
        }

        return handler.getResponse(path, parameters);
    }

    private static Map<String, Object> getParameters(String queryString) {
        String[] queries = queryString.split("&");

        Map<String, Object> parameters = new HashMap<>();
        for (String query : queries) {
            int index = query.indexOf("=");
            String key = query.substring(0, index);
            String value = query.substring(index + 1);

            parameters.put(key, value);
        }

        return parameters;
    }

    private static UriHandler getHandler(String uri) {
        return Arrays.stream(HandlerManager.values())
                .map(HandlerManager::getUriHandler)
                .filter(uriHandler -> uriHandler.canHandle(uri))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("처리할 수 없는 요청입니다."));
    }

    public UriHandler getUriHandler() {
        return uriHandler;
    }
}

