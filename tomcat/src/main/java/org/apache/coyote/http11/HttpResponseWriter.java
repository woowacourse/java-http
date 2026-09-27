package org.apache.coyote.http11;

import org.apache.coyote.http11.enums.HttpStatus;

import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

public class HttpResponseWriter {
    public String write(HttpRequest httpRequest, HttpResponse httpResponse) {
        StringBuilder response = new StringBuilder();
        String responseLine = mergeResponseLine(httpRequest, httpResponse);
        String header = mergeHeader(httpRequest, httpResponse, httpResponse.body());
        String body = new String(httpResponse.body(), StandardCharsets.UTF_8);

        response.append(responseLine)
                .append(" \r\n")
                .append(header)
                .append("\r\n")
                .append(body);
        return  response.toString();
    }

    private String mergeResponseLine(HttpRequest httpRequest, HttpResponse httpResponse) {
        HttpStatus httpStatus = httpResponse.status();
        return String.join(" ",
                httpRequest.version(), httpStatus.getCode(), httpStatus.getMessage());
    }

    private String mergeHeader(HttpRequest httpRequest, HttpResponse httpResponse, byte[] responseBody) {
        StringBuilder sb = new StringBuilder();
        Map<String, String> headers = new LinkedHashMap<>(httpResponse.headers());

        headers.put("Content-Type", getContentType(httpRequest.path()));
        headers.put("Content-Length", String.valueOf(responseBody.length));

        for (Map.Entry<String, String> header : headers.entrySet()) {
            sb.append(header.getKey())
                    .append(": ")
                    .append(header.getValue())
                    .append(" \r\n");
        }

        return sb.toString();
    }

    private String getContentType(String path) {
        if (path.endsWith(".css")) {
            return "text/css;charset=utf-8";
        }
        if (path.endsWith(".js")) {
            return "application/javascript;charset=utf-8";
        }
        return "text/html;charset=utf-8";
    }
}
