package nextstep.jwp.http.request;

import nextstep.jwp.http.HttpMethod;
import nextstep.jwp.http.authentication.HttpSession;
import nextstep.jwp.http.authentication.HttpSessions;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class HttpRequest {
    private final HttpRequestHeader header;
    private final HttpRequestBody body;

    public HttpRequest(final HttpRequestHeader header, final HttpRequestBody body) {
        this.header = header;
        this.body = body;
    }

    public static HttpRequest ofStaticFile(final String url) {
        return new HttpRequest(
                new HttpRequestHeader(List.of("GET " + url + " HTTP/1.1 ")),
                null
        );
    }

    public static HttpRequest parseRequest(final BufferedReader inputStreamReader) throws IOException {
        final List<String> requestHeaders = parseRequestHeaders(inputStreamReader);
        final HttpRequestHeader httpRequestHeader = new HttpRequestHeader(requestHeaders);

        final String requestBody = parseRequestBody(httpRequestHeader.getContentLength(), inputStreamReader);
        final HttpRequestBody httpRequestBody = new HttpRequestBody(requestBody);

        return new HttpRequest(httpRequestHeader, httpRequestBody);
    }

    private static List<String> parseRequestHeaders(final BufferedReader inputStreamReader) throws IOException {
        final List<String> requestHeaders = new ArrayList<>();
        String line;
        while (!"".equals(line = inputStreamReader.readLine())) {
            if (line == null) {
                break;
            }
            requestHeaders.add(line);
        }
        return requestHeaders;
    }

    private static String parseRequestBody(final int contentLength, final BufferedReader inputStreamReader) throws IOException {
        char[] buffer = new char[contentLength];
        inputStreamReader.read(buffer, 0, contentLength);
        return new String(buffer);
    }

    public boolean doesNotHaveJSession() {
        return header.doesNotHaveJSession();
    }

    public boolean doesNotHaveQueryParameters() {
        return header.doesNotHaveQueryParameters();
    }

    public boolean isGet() {
        return getHttpMethod().isGet();
    }

    public HttpSession getSession() {
        if (header.doesNotHaveJSession()) {
            final String id = UUID.randomUUID().toString();
            final HttpSession httpSession = new HttpSession(id);
            HttpSessions.addSession(id, httpSession);
            return httpSession;
        }

        final String id = header.getJSession();
        return HttpSessions.getSession(id);
    }

    public HttpMethod getHttpMethod() {
        return header.getHttpMethod();
    }

    public String getPath() {
        return header.getPath();
    }

    public Map<String, String> getQueryParameters() {
        return header.getQueryParameters();
    }

    public String getProtocol() {
        return header.getProtocol();
    }

    public Map<String, String> getPayload() {
        return body.getPayload();
    }
}
