package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {

    private RequestLine requestLine;
    private RequestHeaders requestHeaders;
    private RequestBody requestBody;

    public static HttpRequest create(InputStream inputStream) {
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
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
                    new RequestLine(requestLine),
                    new RequestHeaders(headers),
                    new RequestBody(headers.get("Content-Type"), body)
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
        return null;
    }
}
