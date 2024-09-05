package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;

public class HttpRequest {

    private static final String HEADER_DELIMITER = ": ";
    private static final String CONTENT_LENGTH = "Content-Length";
    private static final String PARAMETER_DELIMITER = "&";
    private static final String KEY_VALUE_DELIMITER = "=";
    private static final String COOKIE_KEY = "Cookie";
    private static final String JSESSIONID_KEY = "JSESSIONID";

    private HttpRequestLine httpRequestLine;
    private HttpRequestHeader httpRequestHeader;
    private HttpRequestBody httpRequestBody;

    public HttpRequest(BufferedReader bufferedReader) throws IOException {
        this.httpRequestLine = parseRequestLine(bufferedReader);
        this.httpRequestHeader = parseHttpRequestHeader(bufferedReader);
        this.httpRequestBody = parseHttpRequestBody(bufferedReader);
    }

    private HttpRequestLine parseRequestLine(BufferedReader bufferedReader) throws IOException {
        String line = bufferedReader.readLine();
        validateNotNull(line);
        String[] tokens = splitByDelimiter(line, " ");
        return new HttpRequestLine(tokens[0], tokens[1], tokens[2]);
    }

    private void validateNotNull(String line) {
        if (line == null) {
            throw new IllegalArgumentException("Request Line은 null일 수 없습니다.");
        }
    }

    private String[] splitByDelimiter(String line, String delimiter) {
        return line.split(delimiter);
    }

    private HttpRequestHeader parseHttpRequestHeader(BufferedReader bufferedReader) throws IOException {
        String line;
        HttpRequestHeader httpRequestHeader = new HttpRequestHeader();
        while ((line = bufferedReader.readLine()) != null && !line.isEmpty()) {
            String[] tokens = splitByDelimiter(line, HEADER_DELIMITER);
            httpRequestHeader.add(tokens[0], tokens[1]);
        }
        return httpRequestHeader;
    }

    private HttpRequestBody parseHttpRequestBody(BufferedReader bufferedReader) throws IOException {
        HttpRequestBody httpRequestBody = new HttpRequestBody();

        String contentLengthValue = httpRequestHeader.findBy(CONTENT_LENGTH);
        if (contentLengthValue == null) {
            return httpRequestBody;
        }

        int contentLength = Integer.parseInt(contentLengthValue);
        char[] buffer = new char[contentLength];
        bufferedReader.read(buffer, 0, contentLength);
        String httpRequestBodyLine = new String(buffer);

        parseRequestBody(httpRequestBody, httpRequestBodyLine);
        return httpRequestBody;
    }

    private void parseRequestBody(HttpRequestBody httpRequestBody, String httpRequestBodyLine) {
        for (String line : splitByDelimiter(httpRequestBodyLine, PARAMETER_DELIMITER)) {
            String[] tokens = splitByDelimiter(line, KEY_VALUE_DELIMITER);
            httpRequestBody.add(tokens[0], tokens[1]);
        }
    }

    public boolean hasCookie() {
        return this.httpRequestHeader.containsKey("Cookie");
    }

    public boolean hasJSessionId() {
        String cookie = this.httpRequestHeader.findBy(COOKIE_KEY);
        return cookie != null && cookie.contains(JSESSIONID_KEY);
    }

    public boolean matchesMethod(String method) {
        return this.httpRequestLine.matchesMethod(method);
    }

    public boolean matchesFileExtension(String fileExtension) {
        return this.httpRequestLine.matchesFileExtension(fileExtension);
    }

    public String findRequestBodyBy(String key) {
        return this.httpRequestBody.findBy(key);
    }

    public String getUrlPath() {
        return this.httpRequestLine.getUrlPath();
    }
}
