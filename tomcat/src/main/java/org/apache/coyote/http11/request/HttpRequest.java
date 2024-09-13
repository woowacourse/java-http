package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.apache.coyote.http11.utils.Separator;

public class HttpRequest {

    private RequestLine requestLine;
    private RequestHeaders requestHeaders;
    private RequestBody requestBody;

    public static HttpRequest create(BufferedReader bufferedReader) throws IOException {
        String requestLine = bufferedReader.readLine();
        Map<String, String> headers = readHeaders(bufferedReader);

        String contentLength = headers.get("Content-Length");
        String body = readBody(bufferedReader, contentLength);

        return new HttpRequest(
                RequestLine.create(requestLine),
                new RequestHeaders(headers),
                RequestBody.create(body)
        );
    }

    private HttpRequest(RequestLine requestLine, RequestHeaders requestHeaders, RequestBody requestBody) {
        this.requestLine = requestLine;
        this.requestHeaders = requestHeaders;
        this.requestBody = requestBody;
    }

    private static Map<String, String> readHeaders(BufferedReader bufferedReader) throws IOException {
        List<String> headerLines = new ArrayList<>();
        String headerLine = bufferedReader.readLine();
        while (!StringUtils.isBlank(headerLine)) {
            headerLines.add(headerLine);
            headerLine = bufferedReader.readLine();
        }

        return Separator.separateKeyValueBy(headerLines, ":");
    }

    private static String readBody(BufferedReader bufferedReader, String rawContentLength) throws IOException {
        if (rawContentLength == null) {
            return "";
        }

        int contentLength = Integer.parseInt(rawContentLength);
        char[] buffer = new char[contentLength];
        bufferedReader.read(buffer, 0, contentLength);

        return new String(buffer);
    }

    public boolean isGetRequest() {
        return requestLine.isGetRequest();
    }

    public boolean isPostRequest() {
        return requestLine.isPostRequest();
    }

    public String getUri() {
        return requestLine.getPath();
    }

    public String getCookieValue(String cookieName) {
        return requestHeaders.getCookieValue(cookieName);
    }

    public String getBodyParameter(String key) {
        return requestBody.getParameter(key);
    }
}
