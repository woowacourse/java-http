package org.apache.coyote.http11.request;

import org.apache.catalina.Manager;
import org.apache.catalina.session.Session;
import org.apache.coyote.http.cookie.HttpCookies;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class HttpRequest {

    private static final String JSESSIONID = "JSESSIONID";

    private final RequestLine requestLine;
    private final RequestHeader header;
    private final RequestBody body;

    public HttpRequest(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

        String rawRequestLine = bufferedReader.readLine();
        this.requestLine = RequestLine.from(rawRequestLine);
        this.header = RequestFormatter.toHeader(bufferedReader);
        this.body = RequestFormatter.toBody(bufferedReader, requestLine.isGetMethod(), header.getContentLength());
    }

    public boolean existsSession() {
        return header.existsSession();
    }

    public RequestLine getRequestLine() {
        return requestLine;
    }

    public HttpMethod getHttpMethod() {
        return requestLine.getHttpMethod();
    }

    public String getRequestURL() {
        return requestLine.getRequestURL();
    }

    public RequestHeader getRequestHeader() {
        return header;
    }

    public Session getSession(Manager manager) throws IOException {
        HttpCookies cookies = HttpCookies.from(header.getCookies());

        if (existsSession() && manager.findSession(cookies.getCookieValue(JSESSIONID)) != null) {
            return manager.findSession(cookies.getCookieValue(JSESSIONID));
        }

        return manager.createNewSession();
    }

    public Map<String, String> getRequestBody() {
        return body.getContents();
    }
}
