package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;

public class HttpRequest {

    private final RequestLine requestLine;
    private final RequestHeaders requestHeaders;
    private final ResponseBody responseBody;

    public HttpRequest(RequestLine requestLine, RequestHeaders requestHeaders, ResponseBody responseBody) {
        this.requestLine = requestLine;
        this.requestHeaders = requestHeaders;
        this.responseBody = responseBody;
    }

    public static HttpRequest makeRequest(BufferedReader inputReader) {
        RequestLine requestLine = new RequestLine(readRequestLine(inputReader));
        RequestHeaders requestHeaders = new RequestHeaders(readHeaders(inputReader));
        ResponseBody responseBody = new ResponseBody(readBody(inputReader, requestHeaders));
        return new HttpRequest(requestLine,
            requestHeaders,
            responseBody);
    }

    private static String readRequestLine(BufferedReader inputReader) {
        try {
            return inputReader.readLine();
        } catch (IOException e) {
            return null;
        }
    }

    private static String readHeaders(BufferedReader inputReader) {
        try {
            StringBuilder stringBuilder = new StringBuilder();
            String line = inputReader.readLine();
            while (!"".equals(line)) {
                stringBuilder.append(line);
                stringBuilder.append(System.lineSeparator());
                line = inputReader.readLine();
            }
            return stringBuilder.toString();
        } catch (IOException e) {
            throw new IllegalArgumentException("RequestLine 을 읽을 수 없습니다.");
        }
    }

    private static String readBody(BufferedReader inputReader, RequestHeaders requestHeaders) {
        try {
            String s = requestHeaders.getHeaders().get("Content-Length");
            if (s == null) {
                return null;
            }
            int contentLength = Integer.parseInt(s);
            char[] buffer = new char[contentLength];
            inputReader.read(buffer, 0, contentLength);
            return new String(buffer);
        } catch (IOException e) {
            throw new IllegalArgumentException("RequestBody 를 읽을 수 없습니다.");
        }
    }


    public RequestLine getRequestLine() {
        return requestLine;
    }

    public RequestHeaders getRequestHeaders() {
        return requestHeaders;
    }

    public ResponseBody getResponseBody() {
        return responseBody;
    }
}
