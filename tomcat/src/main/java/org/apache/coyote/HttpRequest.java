package org.apache.coyote;

import java.util.Map;

public interface HttpRequest {

    String getVersionOfProtocol();

    String getRequestURI();

    String getMethod();

    String getPath();

    String getHeaderValue(String header);

    boolean existsQueryParam();

    boolean existsBody();

    Map<String, String> getQueryParam();

    String getBody();
}
