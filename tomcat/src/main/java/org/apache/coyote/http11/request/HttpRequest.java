package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;

public class HttpRequest {

    private static final String FORM_DELIMITER = "&";

    private final RequestLine requestLine;
    private final HttpHeaders headers;
    private final HttpCookie cookie;
    private final Map<String, String> formData;

    private HttpRequest(RequestLine requestLine,
                       HttpHeaders headers,
                       HttpCookie cookie,
                       Map<String, String> formData) {
        this.requestLine = requestLine;
        this.headers = headers;
        this.cookie = cookie;
        this.formData = formData;
    }

    public static HttpRequest from(final BufferedReader reader) throws IOException {
        final String line = reader.readLine();
        if (line == null) {
            return null;
        }
        final RequestLine requestLine = RequestLine.from(line);
        final HttpHeaders headers = HttpHeaders.from(reader);
        final String body = readBody(reader, headers.getContentLength());

        return new HttpRequest(
                requestLine,
                headers,
                HttpCookie.from(headers.getCookieHeader()),
                ParameterParser.parse(body, FORM_DELIMITER));
    }

    private static String readBody(final BufferedReader reader, final int contentLength) throws IOException {
        if (contentLength == 0) {
            return "";
        }
        final char[] buffer = new char[contentLength];
        int totalRead = 0;
        while (totalRead < contentLength) {
            final int read = reader.read(buffer, totalRead, contentLength - totalRead);
            if (read == -1) {
                break;
            }
            totalRead += read;
        }
        return new String(buffer, 0, totalRead);
    }

    public HttpMethod getMethod() {
        return requestLine.getMethod();
    }

    public String getPath() {
        return requestLine.getPath();
    }

    public boolean isGet() {
        return requestLine.getMethod() == HttpMethod.GET;
    }

    public boolean isPost() {
        return requestLine.getMethod() == HttpMethod.POST;
    }

    public String getParameter(final String name) {
        final String queryParameter = requestLine.getQueryParameter(name);
        if (queryParameter != null) {
            return queryParameter;
        }
        return formData.get(name);
    }

    public HttpHeaders getHeaders() {
        return headers;
    }

    public HttpCookie getCookie() {
        return cookie;
    }
}
