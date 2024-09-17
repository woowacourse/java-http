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
import java.util.UUID;

public class HttpRequest {

    private final RequestLine requestLine;
    private final RequestHeader header;
    private final RequestBody body;

    public HttpRequest(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

        String rawRequestLine = bufferedReader.readLine();
        this.requestLine = RequestLine.from(rawRequestLine);
        this.header = Formatter.toHeader(bufferedReader);
        this.body = Formatter.toBody(bufferedReader, requestLine.isGetMethod(), header.getContentLength());
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
        Session session = new Session(UUID.randomUUID().toString());
        HttpCookies cookies = HttpCookies.from(header.getCookies());

        if (existsSession() && manager.findSession(cookies.getJsessionId()) != null) {
            return manager.findSession(cookies.getJsessionId());
        }
        manager.add(session);
        return session;
    }

    public Map<String, String> getRequestBody() {
        return body.getPayloads();
    }
}
