package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.HttpCookie;
import org.apache.coyote.http11.HttpHeaders;
import org.apache.coyote.http11.session.Session;
import org.apache.coyote.http11.util.StringUtils;

public class HttpRequest {

    private static final String START_LINE_DELIMITER = " ";
    private static final String HEADER_DELIMITER = ": ";
    private static final String COOKIE = "Cookie";

    private HttpMethod httpMethod;
    private String path;
    private HttpHeaders headers = new HttpHeaders();
    private HttpCookie cookie = new HttpCookie();
    private Map<String, String> requestBody = new HashMap<>();

    public HttpRequest(BufferedReader bufferedReader) throws IOException {
        String startLine = bufferedReader.readLine();
        RequestLine requestLine = new RequestLine(startLine.split(START_LINE_DELIMITER));
        this.httpMethod = requestLine.getHttpMethod();
        this.path = requestLine.getPath();
        parseHeaders(bufferedReader);

        if (HttpMethod.POST.equals(httpMethod)) {
            parseRequestBody(bufferedReader);
        }
    }

    private void parseHeaders(BufferedReader bufferedReader) throws IOException {
        String line = bufferedReader.readLine();
        while (line != null && !line.isBlank()) {
            String[] header = line.split(HEADER_DELIMITER);
            String key = header[0];
            String value = header[1].trim();
            if (key.equals(COOKIE)) {
                cookie.addCookies(value);
            }
            headers.add(key, value);
            line = bufferedReader.readLine();
        }
    }

    private void parseRequestBody(BufferedReader bufferedReader) throws IOException {
        String contentLength = headers.getOrDefault("Content-Length", "0");
        int length = Integer.parseInt(contentLength);
        char[] buffer = new char[length];
        bufferedReader.read(buffer, 0, length);
        requestBody = StringUtils.parseKeyAndValues(new String(buffer));
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public String getPath() {
        return path;
    }

    public HttpHeaders getHeaders() {
        return headers;
    }

    public HttpCookie getCookie() {
        return cookie;
    }

    public Map<String, String> getRequestBody() {
        return requestBody;
    }

    public Session getSession() {
        return new Session(getCookie().getJSessionId());
    }
}
