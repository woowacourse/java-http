package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import static org.apache.coyote.http11.Method.POST;

public class HttpRequest {

    private final RequestLine requestLine;
    private final Map<String, String> requestHeaders;
    private String requestBody = ""; // 추후 GET, POST 리팩토링

    public HttpRequest(InputStream inputStream) throws IOException {
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

        this.requestLine = readRequestLine(bufferedReader);
        this.requestHeaders = readRequestHeaders(bufferedReader);

        if (isMethod(POST)) {
            this.requestBody = readRequestBody(bufferedReader);
        }
    }

    private RequestLine readRequestLine(BufferedReader bufferedReader) throws IOException {
        return new RequestLine(bufferedReader);
    }

    private Map<String, String> readRequestHeaders(BufferedReader bufferedReader) throws IOException {
        Map<String, String> requestHeaders = new HashMap<>();
        String headerLine = bufferedReader.readLine();

        while (!("".equals(headerLine)) && headerLine != null) {
            String[] headerLineValues = parseWithTrim(headerLine, ":");
            String headerName = headerLineValues[0];
            String headerValue = headerLineValues[1];

            requestHeaders.put(headerName, headerValue);

            headerLine = bufferedReader.readLine();
        }

        return requestHeaders;
    }

    private String readRequestBody(BufferedReader bufferedReader) throws IOException {
        int contentLength = Integer.parseInt(requestHeaders.get("Content-Length"));
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

    public String getCookie() {
        String[] cookies = parseWithTrim(requestHeaders.get("Cookie"), ";");

        for (String cookie : cookies) {
            if (cookie.startsWith("JSESSIONID")) {
                return cookie.split("=")[1];
            }
        }

        return null;
    }

    public String getContentType() {
        String fileExtension = getPath().split("\\.")[1];
        return "text/" + fileExtension;
    }

    public String getRequestBody() {
        return requestBody;
    }
}
