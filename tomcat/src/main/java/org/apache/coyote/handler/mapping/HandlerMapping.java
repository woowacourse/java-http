package org.apache.coyote.handler.mapping;

import java.io.IOException;
import java.util.Map;

public interface HandlerMapping {

    boolean supports(final String httpMethod, final String requestUri);

    String handle(final String requestUri, final Map<String, String> headers, final String requestBody) throws IOException;
}
