package org.apache.coyote.http11;

import java.util.LinkedHashMap;
import java.util.Map;

public class HttpResponse {

    private static final String START_LINE = "HTTP/1.1 200 OK ";
    private static final String HEADER_FORMAT_TEMPLATE = "%s: %s \r\n";

    private final Map<String, String> headers = new LinkedHashMap<>();
    private final String responseBody;

    public HttpResponse(String responseBody) {
        this.responseBody = responseBody;
        headers.put("Content-Type", "text/html;charset=utf-8");
        headers.put("Content-Length", String.valueOf(responseBody.getBytes().length));
    }

    public byte[] getBytes() {
        String response = createResponse();
        return response.getBytes();
    }

    private String createResponse() {
        StringBuilder sb = new StringBuilder(START_LINE).append("\r\n");
        headers.forEach((name, value) -> sb.append(HEADER_FORMAT_TEMPLATE.formatted(name, value)));
        sb.append("\r\n").append(responseBody);
        return sb.toString();
    }
}
