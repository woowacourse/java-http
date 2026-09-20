package org.apache.http.request;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import org.apache.http.HttpMethod;
import org.qupring.session.Session;

public class HttpTomcatRequest implements HttpRequest {

    private final HttpMethod httpMethod;
    private final String url;
    private final String protocol;
    private Session session;
    private final Map<String, String> header;
    private final Map<String, String> queryParams;
    private final Map<String, String> cookies;
    private final Map<String, String> body;

    public HttpTomcatRequest(HttpMethod httpMethod, String url, String protocol, Session session, Map<String, String> header,
                             Map<String, String> queryParams, Map<String, String> cookies,
                             Map<String, String> body) {
        this.httpMethod = httpMethod;
        this.url = url;
        this.protocol = protocol;
        this.session = session;
        this.header = new LinkedHashMap<>(header);
        this.queryParams = Map.copyOf(queryParams);
        this.cookies = Map.copyOf(cookies);
        this.body = Map.copyOf(body);
    }

    @Override
    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    @Override
    public String getUrl() {
        return url;
    }

    @Override
    public String getProtocol() {
        return protocol;
    }

    @Override
    public Session getSession(boolean create) {
        if (session != null || !create) {
            return session;
        }

        session = new Session(
                UUID.randomUUID().toString()
        );

        return session;
    }

    @Override
    public String getHeader(String target) {
        return header.get(target);
    }

    @Override
    public Map<String, String> getQueryParams() {
        return Map.copyOf(queryParams);
    }

    @Override
    public Map<String, String> getBodys() {
        return Map.copyOf(body);
    }

    @Override
    public String getBody(String target) {return body.get(target);}

    @Override
    public Map<String, String> getCookies() {
        return Map.copyOf(cookies);
    }

    @Override
    public String getCookie(String target) {
        return cookies.get(target);
    }

}
