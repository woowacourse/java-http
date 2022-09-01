package org.apache;

import java.util.HashMap;
import java.util.Map;

public class HttpResponseMessage {

    private String httpVersion;
    private String httpStatusCode;
    private Map<String, String> headerMap = new HashMap<>();
    private String body;

    public HttpResponseMessage(String httpVersion,
                               String httpStatusCode,
                               Map<String, String> headerMap,
                               String body) {
        this.httpVersion = httpVersion;
        this.httpStatusCode = httpStatusCode;
        this.headerMap = headerMap;
        this.body = body;
    }

    public String generateMessage() {
        return String.join("\r\n",
                httpVersion + " " + httpStatusCode + " ",
                "Content-Type: " + headerMap.get("Content-Type") + " ",
                "Content-Length: " + headerMap.get("Content-Length") + " ",
                "",
                body);
    }
}
