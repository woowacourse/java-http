package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static org.apache.coyote.http11.Method.POST;

public class HttpRequest {

    private static final int FILE_EXTENSION_INDEX = 1;
    private final RequestLine requestLine;
    private final HttpHeaders httpHeaders;
    private String requestBody = ""; // 추후 GET, POST 리팩토링

    public HttpRequest(RequestLine requestLine, HttpHeaders httpHeaders) {
        this.requestLine = requestLine;
        this.httpHeaders = httpHeaders;
    }

    public HttpRequest(RequestLine requestLine, HttpHeaders httpHeaders, String requestBody) {
        this.requestLine = requestLine;
        this.httpHeaders = httpHeaders;
        this.requestBody = requestBody;
    }

    public HttpRequest(InputStream inputStream) throws IOException {
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

        this.requestLine = new RequestLine(bufferedReader);
        this.httpHeaders = new HttpHeaders(bufferedReader);

        if (isMethod(POST)) {
            this.requestBody = readRequestBody(bufferedReader);
        }
    }

    private String readRequestBody(BufferedReader bufferedReader) throws IOException {
        int contentLength = Integer.parseInt(httpHeaders.findField("Content-Length"));
        char[] buffer = readRequestBodyByLength(bufferedReader, contentLength);
        return new String(buffer);
    }

    private char[] readRequestBodyByLength(BufferedReader bufferedReader, int length) throws IOException {
        char[] buffer = new char[length];
        bufferedReader.read(buffer, 0, length);

        return buffer;
    }

    private String[] parseWithTrim(String line, String delimiter) {
        String[] splited = line.split(delimiter);

        for (int i = 0; i < splited.length; i++) {
            splited[i] = splited[i].trim();
        }

        return splited;
    }

    public boolean isMethod(Method method) {
        return requestLine.isMethod(method);
    }

    public String getPath() {
        return requestLine.getPath();
    }

    public String getCookie(String key) {
        if (httpHeaders.findField("Cookie") == null) {
            return null;
        }

        String[] cookies = parseWithTrim(httpHeaders.findField("Cookie"), ";");

        for (String cookie : cookies) {
            if (cookie.startsWith(key)) {
                return cookie.split("=")[1];
            }
        }

        return null;
    }

    public String getContentType() {
        String[] splitedPath = getPath().split("\\.");

        if (splitedPath.length == 2) {
            return "text/" + splitedPath[FILE_EXTENSION_INDEX];
        }

        return "text/html";
    }

    public String getRequestBody() {
        return requestBody;
    }
}
