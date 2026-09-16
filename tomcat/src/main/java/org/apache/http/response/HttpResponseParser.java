package org.apache.http.response;

import java.util.Map;

public class HttpResponseParser {

    public String parse(HttpResponse httpResponse) {
        StringBuilder responseBuilder = new StringBuilder();
        responseBuilder.append("HTTP/1.1 ").append(httpResponse.getStatus()).append("\r\n");
        for (Map.Entry<String, String> header : httpResponse.getHeaders().entrySet()) {
            responseBuilder.append(header.getKey()).append(": ").append(header.getValue()).append("\r\n");
        }
        responseBuilder.append("\r\n");
        responseBuilder.append(httpResponse.getBody());
        return responseBuilder.toString();
    }

}
