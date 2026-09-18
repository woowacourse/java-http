package org.apache.http.request;

import java.util.Map;
import org.apache.http.HttpMethod;

public interface HttpRequest {
    HttpMethod getHttpMethod();

    String getUrl();

    String getProtocol();

    String getHeader(String target);

    Map<String, String> getQueryParams();

    String getBody();
}
