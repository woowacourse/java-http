package org.apache;

import java.util.Map;

public class HttpResponse {

    public static final String SPACE = " ";
    public static final String EMPTY_LINE = "";

    private String httpVersion;
    private String httpStatusCode;
    private Map<String, String> headerMap;
    private String body;

    public HttpResponse(String httpVersion,
                        String httpStatusCode,
                        Map<String, String> headerMap,
                        String body) {
        this.httpVersion = httpVersion;
        this.httpStatusCode = httpStatusCode;
        this.headerMap = headerMap;
        this.body = body;
    }

    public String createFullMessage() {
        return String.join("\r\n",
                httpVersion + SPACE + httpStatusCode + SPACE,
                "Content-Type: " + headerMap.get("Content-Type") + SPACE,
                "Content-Length: " + headerMap.get("Content-Length") + SPACE,
                EMPTY_LINE,
                body);
    }
}
