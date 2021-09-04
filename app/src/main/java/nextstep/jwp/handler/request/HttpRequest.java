package nextstep.jwp.handler.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import nextstep.jwp.exception.handler.HttpMessageException;
import nextstep.jwp.handler.*;
import nextstep.jwp.handler.constant.HttpMethod;

public class HttpRequest {

    private final RequestLine requestLine;
    private final HttpHeader header;
    private final HttpCookie cookie;
    private final HttpBody body;

    public HttpRequest(HttpMethod httpMethod, String httpUri, String httpVersion, HttpHeader header, HttpCookie cookie, HttpBody body) {
        this.requestLine = new RequestLine(httpMethod, new RequestUrl(httpUri), httpVersion);
        this.header = header;
        this.cookie = cookie;
        this.body = body;
    }

    public static HttpRequest from(BufferedReader reader) throws IOException {
        String startLine = reader.readLine();
        String[] startLineSplit = startLine.split(" ");
        if (startLineSplit.length < 3) {
            throw new HttpMessageException("HTTP Request Start-Line 형식이 올바르지 않습니다.");
        }

        HttpMethod httpMethod = HttpMethod.from(startLineSplit[0]);
        String httpUri = startLineSplit[1];
        String httpVersion = startLineSplit[2];
        HttpHeader header = extractHeader(reader);
        HttpCookie cookie = header.getCookie();
        HttpBody body = extractBody(reader, header.getContentLength());

        return new HttpRequest(httpMethod, httpUri, httpVersion, header, cookie, body);
    }

    private static HttpHeader extractHeader(BufferedReader reader) throws IOException {
        Map<String, String> headerMap = new HashMap<>();
        while (reader.ready()) {
            String headerLine = reader.readLine();
            if ("".equals(headerLine)) {
                break;
            }
            String[] headerLineSplit = headerLine.split(": ");
            headerMap.put(headerLineSplit[0], headerLineSplit[1].strip());
        }
        return new HttpHeader(headerMap);
    }

    private static HttpBody extractBody(BufferedReader bufferedReader, int contentLength) throws IOException {
        char[] buffer = new char[contentLength];
        bufferedReader.read(buffer, 0, contentLength);
        return new HttpBody(new String(buffer));
    }

    public boolean isRequestStaticFile() {
        return requestLine.hasFileSuffixUri() && isGet();
    }

    public boolean isUriContainsQuery() {
        return requestLine.hasQueryParams();
    }

    public boolean containsCookie(String name) {
        return cookie.contains(name);
    }

    public HttpSession getSession() {
        Cookie sessionCookie = cookie.getCookie("JSESSIONID");
        if (sessionCookie == null) {
            return null;
        }
        String sessionId = sessionCookie.getValue();
        return HttpSessions.getSession(sessionId);
    }

    public boolean isGet() {
        return requestLine.isSameMethodAs(HttpMethod.GET);
    }

    public boolean isPost() {
        return requestLine.isSameMethodAs(HttpMethod.POST);
    }

    public String getRequestUrl() {
        return requestLine.getRequestUrl().getUrl();
    }

    public String getHttpVersion() {
        return requestLine.getVersion();
    }

    public HttpBody getBody() {
        return body;
    }
}
