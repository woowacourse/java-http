package org.apache.http;

import java.util.Map;
import org.apache.http.info.HttpMethod;

public interface HttpRequest {

    HttpMethod getHttpMethod();

    String getProtocol();

    String getRequestURI();

    String getRequestURIWithoutQueryParams();

    String getHost();

    String getConnection();

    Map<String, String> getHeaders();

    String getBody();

    String getContentType();

    Object getParameter(String key);

    Map<String, Object> getParameters();
}
