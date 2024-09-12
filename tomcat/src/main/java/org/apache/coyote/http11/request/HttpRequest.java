package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {

    private RequestLine requestLine;
    private RequestHeaders requestHeaders;
    private RequestBody requestBody;

    public static HttpRequest create(BufferedReader bufferedReader) {
        try {
            Map<String, String> headers = new HashMap<>();

            String requestLine = bufferedReader.readLine();
            String headerLine = bufferedReader.readLine();
            while (headerLine != null && !headerLine.isBlank()) {
                String[] requestHeaderEntry = headerLine.split(":");
                headers.put(requestHeaderEntry[0], requestHeaderEntry[1].trim());
                headerLine = bufferedReader.readLine();
            }

            String body = "";
            String rawContentLength = headers.get("Content-Length");
            if (rawContentLength != null) {
                int contentLength = Integer.parseInt(rawContentLength);
                char[] buffer = new char[contentLength];
                bufferedReader.read(buffer, 0, contentLength);
                body = new String(buffer);
            }

            return new HttpRequest(
                    RequestLine.create(requestLine),
                    new RequestHeaders(headers),
                    RequestBody.create(body)
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private HttpRequest(RequestLine requestLine, RequestHeaders requestHeaders, RequestBody requestBody) {
        this.requestLine = requestLine;
        this.requestHeaders = requestHeaders;
        this.requestBody = requestBody;
    }

    public boolean isGetRequest() {
        return requestLine.isGetRequest();
    }

    public boolean isPostRequest() {
        return requestLine.isPostRequest();
    }

    public String getHeaderValue(String targetHeader) {
        return requestHeaders.getHeaderValue(targetHeader);
    }

    public String parseBodyParameter(String key) {
        return requestBody.getParameter(key);
    }

    public String getUri() {
        return requestLine.getPath();
    }
}
