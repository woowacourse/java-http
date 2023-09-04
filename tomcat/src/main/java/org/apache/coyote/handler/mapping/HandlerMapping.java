package org.apache.coyote.handler.mapping;

import org.apache.coyote.http.HttpHeaders;
import org.apache.coyote.http.HttpMethod;

import java.io.IOException;

public interface HandlerMapping {

    boolean supports(final HttpMethod httpMethod, final String requestUri);

    String handle(final String requestUri, final HttpHeaders httpHeaders, final String requestBody) throws IOException;
}
