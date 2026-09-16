package org.apache.http.request;

import java.util.Map;
import org.apache.http.HttpMethod;

public class HttpTomcatRequest implements HttpRequest {

    private final HttpMethod httpMethod;
    private final String url;
    private final String protocol;
    private final Map<String, String> Header;
    private final String body;

    public HttpTomcatRequest(HttpMethod httpMethod, String url, String protocol, Map<String, String> header,
                             String body) {
        this.httpMethod = httpMethod;
        this.url = url;
        this.protocol = protocol;
        Header = header;
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
    public String getBody() {
        return body;
    }
}
