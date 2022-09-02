package http;

import java.util.Map;

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
