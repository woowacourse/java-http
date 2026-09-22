package org.apache.coyote.http11;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class HttpResponse {

    private final HttpStatus httpStatus;
    private final Map<String, String> headers;
    private final String responseBody;

    public HttpResponse(HttpStatus httpStatus, Map<String, String> headers, String responseBody) {
        this.httpStatus = httpStatus;
        this.headers = headers;
        this.responseBody = responseBody;
    }

    public static HttpResponse ok(String contentType, String responseBody) {
        Map<String, String> headers = new LinkedHashMap<>();
        headers.put("Content-Type", contentType + ";charset=utf-8");
        headers.put("Content-Length", responseBody.getBytes().length + "");
        return new HttpResponse(HttpStatus.OK, headers, responseBody);
    }

    public byte[] getBytes() {
        List<String> lines = new ArrayList<>();
        lines.add("HTTP/1.1 " + httpStatus.getCode() + " " + httpStatus.getStatusMessage() + " ");
        headers.forEach((name, value) -> lines.add(name + ": " + value + " "));   // 끝 공백!
        lines.add("");
        lines.add(responseBody);
        return String.join("\r\n", lines).getBytes();
    }
}
