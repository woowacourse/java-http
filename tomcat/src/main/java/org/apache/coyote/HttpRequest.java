package org.apache.coyote;

import java.util.Map;

public interface HttpRequest {

    String getRequestURI();

    String getMethod();

    String getPath();

    boolean isNotExistsCookie(String key);

    String getHeader(String header);

    boolean isExistsQueryString();

    boolean isExistsBody();

    Map<String, String> getQueryParam();

    Map<String, String> getParsedBody();
}
