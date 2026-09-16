package org.apache.http.request;

import java.util.Map;
import org.apache.http.HttpMethod;

public class HttpTomcatRequest implements HttpRequest {

    private final HttpMethod httpMethod;
    private final String url;
    private final String protocol;
    private final Map<String, String> Header;
    private final Map<String, String> queryParams;
    private final String body;

    public HttpTomcatRequest(HttpMethod httpMethod, String url, String protocol, Map<String, String> header,
                             Map<String, String> queryParams,
                             String body) {
        this.httpMethod = httpMethod;
        this.url = url;
        this.protocol = protocol;
        this.Header = Map.copyOf(header);
        this.queryParams = queryParams;
        this.body = body;
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
    public String getHeader(String target) {
        return Header.get(target);
    }

    @Override
    public Map<String, String> getQueryParams() {
        return Map.copyOf(queryParams);
    }

    @Override
    public String getBody() {
        return body;
    }
}
