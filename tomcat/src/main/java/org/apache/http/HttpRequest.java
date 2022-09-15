package org.apache.http;

import java.util.Map;
import org.apache.http.info.HttpMethod;

public interface HttpRequest {

    HttpMethod getHttpMethod();

    String getRequestURI();

    String getRequestURIWithoutQueryParams();

    Map<String, String> getParameters();

    Object getParameter(String key);

    Map<String, String> getHeaders();

    String getHeader(String headerName);

    Map<String, String> getBody();
}
