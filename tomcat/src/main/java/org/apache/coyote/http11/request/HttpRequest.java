package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.coyote.http11.HttpCookie;
import org.apache.coyote.http11.HttpMethod;

public class HttpRequest {
    private final RequestLine requestLine;
    private final RequestHeader requestHeader;
    private final RequestBody requestBody;
    private final HttpCookie httpCookie;

    private HttpRequest(RequestLine requestLine, RequestHeader requestHeader,
        RequestBody requestBody, HttpCookie httpCookie) {
        this.requestLine = requestLine;
        this.requestHeader = requestHeader;
        this.requestBody = requestBody;
        this.httpCookie = httpCookie;
    }

    public static HttpRequest from(BufferedReader reader) {
        try {
            RequestLine requestLine = RequestLine.from(reader.readLine());
            RequestHeader requestHeader = parseHeader(reader);
            RequestBody requestBody = parseBody(reader, requestHeader.getContentLength());
            HttpCookie httpCookie = HttpCookie.of(requestHeader.get("Cookie"));
            return new HttpRequest(requestLine, requestHeader, requestBody, httpCookie);
        } catch (IOException e) {
            throw new IllegalArgumentException("HTTP Request 생성 중 에러가 발생하였습니다.");
        }
    }

    private static RequestHeader parseHeader(BufferedReader reader) throws IOException {
        List<String> headerValues = new ArrayList<>();

        String line = reader.readLine();
        while (!line.isEmpty()) {
            headerValues.add(line);
            line = reader.readLine();
        }
        return RequestHeader.from(headerValues);
    }

    private static RequestBody parseBody(BufferedReader reader, int contentLength) throws IOException {
        if (contentLength == 0) {
            return RequestBody.getEmptyBody();
        }

        char[] buffer = new char[contentLength];
        reader.read(buffer, 0, contentLength);
        return RequestBody.from(new String(buffer));
    }

    public String getCookie(String key) {
        return httpCookie.getOrDefault(key);
    }

    public HttpMethod getHttpMethod() {
        return requestLine.getHttpMethod();
    }

    public String getPath() {
        return requestLine.getPath();
    }

    public RequestBody getRequestBody(){
        return requestBody;
    }

    public boolean isSamePath(String... paths) {
        String realPath = getPath();

        return Arrays.asList(paths).contains(realPath);
    }
}
