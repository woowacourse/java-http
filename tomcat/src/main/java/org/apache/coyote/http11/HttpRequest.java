package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class HttpRequest {
    private final String method;
    private final String path;
    private final String version;
    private final Map<String, String> headers = new HashMap<String, String>();
    private final String requestBody;

    public HttpRequest(String method, String path, String version, Map<String, String> headers, String requestBody) {
        this.method = method;
        this.path = path;
        this.version = version;
        this.headers.putAll(headers);
        this.requestBody = requestBody;
    }

    public Map<String, String> getQueryParameter() {
        Map<String, String> parameters = new HashMap<>();
        if (requestBody == null || requestBody.isEmpty()) {
            return parameters;
        }
        String[] queryParameters = requestBody.split("&");
        for (String parameter : queryParameters) {
            String[] queryParameter = parameter.split("=", -1);
            parameters.put(queryParameter[0], queryParameter[1]);
        }
        return parameters;
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public String getVersion() {
        return version;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public String getRequestBody() {
        return requestBody;
    }
}
