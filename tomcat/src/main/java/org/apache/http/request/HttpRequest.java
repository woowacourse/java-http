package org.apache.http.request;

import org.apache.http.HttpMethod;

public interface HttpRequest {
    HttpMethod getHttpMethod();

    String getUrl();

    String getProtocol();

    String getHeader(String target);

    String getBody();
}
