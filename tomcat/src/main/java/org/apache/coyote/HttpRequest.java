package org.apache.coyote;

import java.util.Map;

public interface HttpRequest {

    String getRequestURI();

    String getPath();

    String getHeaderValue(String header);

    boolean existsQueryParam();

    Map<String, String> getQueryParam();
}
