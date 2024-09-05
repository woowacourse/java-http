package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.exception.UnexpectQueryParamException;
import org.apache.coyote.http11.common.HttpMethod;

public class HttpRequest {

    private final HttpMethod method;
    private final String uri;
    private final String path;
    private final Map<String, String> params = new HashMap<>();
    private final String protocol;
    private final String headers;
    private final String body;

    public HttpRequest(
            String method, String uri, String path, String[] paramStrings, String protocol, String headers,
            String body
    ) {
        this.method = HttpMethod.valueOf(method);
        this.uri = uri;
        this.path = path;
        mapQueryParams(paramStrings);
        this.protocol = protocol;
        this.headers = headers;
        this.body = body;
    }

    private void mapQueryParams(String[] pairs) {
        for (String pair : pairs) {
            String[] keyValue = pair.split("=");

            String key = keyValue[0];
            String value = "";

            if (keyValue.length > 1) {
                value = keyValue[1];
            }
            params.put(key, value);
        }
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public String getParams(String paramKey) {
        String paramValue = params.get(paramKey);
        if (paramValue == null) {
            throw new UnexpectQueryParamException();
        }
        return paramValue;
    }

    @Override
    public String toString() {
        return "HttpRequest{" +
                "body='" + body + '\'' +
                ", method=" + method +
                ", uri='" + uri + '\'' +
                ", path='" + path + '\'' +
                ", params=" + params +
                ", protocol='" + protocol + '\'' +
                ", headers='" + headers + '\'' +
                '}';
    }
}
