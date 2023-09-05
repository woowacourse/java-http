package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class RequestHandler {

    private String requestMethod;
    private String httpStatus;
    private String responseFilePath;
    private Map<String, String> headers = new HashMap<>();

    private RequestHandler(String requestMethod, String httpStatus, String responseFilePath) {
        this.requestMethod = requestMethod;
        this.httpStatus = httpStatus;
        this.responseFilePath = responseFilePath;
    }

    public static RequestHandler of(String requestMethod, String httpStatus, String fileName) {
        String filePath = RequestHandler.class.getClassLoader().getResource(fileName).getPath();
        return new RequestHandler(requestMethod, httpStatus, filePath);
    }

    public void addHeader(String key, String value) {
        this.headers.put(key, value);
    }

    public String getRequestMethod() {
        return requestMethod;
    }

    public String getHttpStatus() {
        return httpStatus;
    }

    public String getResponseFilePath() {
        return responseFilePath;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }
}
