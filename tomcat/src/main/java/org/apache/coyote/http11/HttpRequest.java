package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;
import org.apache.coyote.http11.header.Cookies;
import org.apache.coyote.http11.header.Headers;
import org.apache.coyote.http11.header.HttpHeader;

public class HttpRequest {

    private static final String WHITE_SPACE = " ";

    private final HttpMethod method;
    private final HttpTarget target;
    private final String version;
    private final Headers headers;
    private final String body;

    private HttpRequest(final String method, final String target, final String version,
                        final Headers headers,
                        final String body) {
        this.method = new HttpMethod(method);
        this.target = new HttpTarget(target);
        this.version = version;
        this.headers = headers;
        this.body = body;
    }

    public static HttpRequest from(BufferedReader reader) {
        try {
            String[] requestLineElements = reader.readLine().split(WHITE_SPACE);

            Headers headers = new Headers();
            String next;
            while (reader.ready() && !(next = reader.readLine()).isEmpty()) {
                headers.add(next);
            }

            int contentLength = headers.getContentLength();
            char[] bodyBuffer = new char[contentLength];
            while (contentLength > 0) {
                int readCount = reader.read(bodyBuffer, 0, contentLength);
                contentLength -= readCount;
            }

            return new HttpRequest(
                    requestLineElements[0],
                    requestLineElements[1],
                    requestLineElements[2],
                    headers,
                    new String(bodyBuffer)
            );
        } catch (IOException e) {
            throw new IllegalArgumentException("HTTP 요청을 잘 읽지 못했습니다");
        }
    }


    public HttpMethod getMethod() {
        return method;
    }

    public HttpTarget getTarget() {
        return target;
    }

    public String getVersion() {
        return version;
    }

    public List<HttpHeader> getHeaders() {
        return headers.getHeaders();
    }

    public Cookies getCookies() {
        return Cookies.from(getHeader("Cookie"));
    }

    public HttpHeader getHeader(String name) {
        return headers.get(name);
    }

    public String getBody() {
        return body;
    }
}
