package nextstep.jwp.model;

import java.util.Map;

public class Request {

    private String requestMethod;
    private RequestPath requestPath;
    private Map<String, String> requestHeaders;
    private RequestBody requestBody;

    public Request(String requestMethod, Map<String, String> requestHeaders,
                   RequestPath requestPath, RequestBody requestBody) {
        this.requestMethod = requestMethod;
        this.requestHeaders = requestHeaders;
        this.requestPath = requestPath;
        this.requestBody = requestBody;
    }

    public String getRequestMethod() {
        return requestMethod;
    }

    public RequestPath getRequestPath() {
        return requestPath;
    }

    public RequestBody getRequestBody() {
        return requestBody;
    }
}
