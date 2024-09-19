package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static org.apache.coyote.http11.Method.POST;

public class HttpRequest {

    private static final int VALUE_INDEX = 1;
    private static final String HEADER_DELIMITER = ";";
    private static final String KEY_VALUE_DELIMITER = "=";
    private static final String REQUEST_BODY_DELIMITER = "&";

    private final RequestLine requestLine;
    private final HttpRequestHeaders httpRequestHeaders;
    private String requestBody = "";

    public HttpRequest(RequestLine requestLine, HttpRequestHeaders httpRequestHeaders) {
        this.requestLine = requestLine;
        this.httpRequestHeaders = httpRequestHeaders;
    }

    public HttpRequest(RequestLine requestLine, HttpRequestHeaders httpRequestHeaders, String requestBody) {
        this.requestLine = requestLine;
        this.httpRequestHeaders = httpRequestHeaders;
        this.requestBody = requestBody;
    }

    public HttpRequest(InputStream inputStream) throws IOException {
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

        this.requestLine = new RequestLine(bufferedReader);
        this.httpRequestHeaders = new HttpRequestHeaders(bufferedReader);

        if (isMethod(POST)) {
            this.requestBody = readRequestBody(bufferedReader);
        }
    }

    private String readRequestBody(BufferedReader bufferedReader) throws IOException {
        int contentLength = Integer.parseInt(httpRequestHeaders.findField("Content-Length"));
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
        if (httpRequestHeaders.findField("Cookie") == null) {
            return null;
        }

        String[] cookies = parseWithTrim(httpRequestHeaders.findField("Cookie"), HEADER_DELIMITER);

        for (String cookie : cookies) {
            if (cookie.startsWith(key)) {
                return cookie.split(KEY_VALUE_DELIMITER)[VALUE_INDEX];
            }
        }

        return null;
    }

    public String getContentType() {
        String[] splitedPath = getPath().split("\\.");

        if (splitedPath.length == 2) {
            return "text/" + splitedPath[VALUE_INDEX];
        }

        return "text/html";
    }

    public String getRequestBody() {
        return requestBody;
    }

    public String getRequestBodyValue(String key) {
        String[] values = requestBody.split(REQUEST_BODY_DELIMITER);

        for (String value : values) {
            if (value.startsWith(key)) {
                return value.split(KEY_VALUE_DELIMITER)[VALUE_INDEX];
            }
        }

        return null;
    }

    public void setPath(String path) {
        requestLine.setPath(path);
    }
}
