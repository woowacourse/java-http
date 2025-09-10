package org.apache.coyote.http11.handler.controllerResponse;

import org.apache.coyote.http11.general.HttpHeaders;
import org.apache.coyote.http11.general.HttpProtocolVersion;
import org.apache.coyote.http11.httpResponse.HttpResponse;
import org.apache.coyote.http11.httpResponse.HttpStatus;

public interface ApplicationResponse {

    HttpStatus status();
    HttpHeaders headers();
    String content();

    void addHeader(String key, String value);
    HttpResponse toHttpResponse(HttpProtocolVersion protocolVersion);
}
