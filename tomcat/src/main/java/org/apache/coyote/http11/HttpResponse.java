package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class HttpResponse {
    private final StringBuilder status = new StringBuilder();
    private final Map<String, String> headers = new HashMap<>();
    private final StringBuilder body = new StringBuilder();

    public String createResponse(String contentType, String responseBody) {
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: " + contentType + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }

    public String createResponse() {
        StringBuilder responseBuilder = new StringBuilder();
        responseBuilder.append("HTTP/1.1 ").append(status).append("\r\n");
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            responseBuilder.append(entry.getKey()).append(": ").append(entry.getValue()).append("\r\n");
        }
        responseBuilder.append("\r\n"); // 빈 줄로 헤더와 바디 구분

        // HTTP 응답 바디 추가
        responseBuilder.append(body);

        return responseBuilder.toString();
    }

    public void setStatus(String value) {
        status.append(value);
    }

    public void setCookie(String value) {
        headers.put("Set-Cookie", value);
    }

    public void setContentType(String value) {
        headers.put("Content-Type", value);
    }

    public void setBody(String value) {
        headers.put("Content-Length", String.valueOf(value.getBytes().length));
        body.append(value);
    }

    public void setRedirectUrl(String value) {
        headers.put("Location", value);
    }
    
}
