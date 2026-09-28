package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {

    private static final String CONTENT_LENGTH = "Content-Length";
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String FORM_URLENCODED = "application/x-www-form-urlencoded";
    private static final String COOKIE = "Cookie";
    private static final String GET = "GET";
    private static final String POST = "POST";

    private final RequestLine requestLine;
    private final Map<String, String> headers;
    private final String body;
    private final Parameters formParameters;
    private final HttpCookie cookie;

    public HttpRequest(final BufferedReader bufferedReader) throws IOException {
        this.requestLine = new RequestLine(bufferedReader.readLine());
        this.headers = readHeaders(bufferedReader);
        this.body = readBody(bufferedReader);
        this.formParameters = parseFormParameters();
        this.cookie = new HttpCookie(headers.get(COOKIE));
    }

    private Map<String, String> readHeaders(final BufferedReader reader) throws IOException {
        final Map<String, String> headers = new HashMap<>();
        String line = reader.readLine();
        while (line != null && !line.isEmpty()) {
            final int index = line.indexOf(":");
            if (index != -1) {
                headers.put(line.substring(0, index).trim(), line.substring(index + 1).trim());
            }
            line = reader.readLine();
        }
        return headers;
    }

    private String readBody(final BufferedReader reader) throws IOException {
        if (!headers.containsKey(CONTENT_LENGTH)) {
            return "";
        }
        final int contentLength = Integer.parseInt(headers.get(CONTENT_LENGTH));
        final char[] buffer = new char[contentLength];
        reader.read(buffer, 0, buffer.length);
        return new String(buffer);
    }

    private Parameters parseFormParameters() {
        final String contentType = headers.get(CONTENT_TYPE);
        if (contentType == null || !contentType.startsWith(FORM_URLENCODED)) {
            return Parameters.empty();
        }
        return Parameters.from(body);
    }

    public boolean isGet() {
        return GET.equals(requestLine.getMethod());
    }

    public boolean isPost() {
        return POST.equals(requestLine.getMethod());
    }

    public String getPath() {
        return requestLine.getPath();
    }

    public String getHeader(final String name) {
        return headers.get(name);
    }

    public String getQueryParameter(final String name) {
        return requestLine.getQueryParameter(name);
    }

    public String getFormParameter(final String name) {
        return formParameters.get(name);
    }

    public String getBody() {
        return body;
    }

    public HttpCookie getCookie() {
        return cookie;
    }
}
