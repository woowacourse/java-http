package org.apache.coyote.http11.handler.applicationResponse;

import org.apache.coyote.http11.general.HttpHeaders;
import org.apache.coyote.http11.httpResponse.HttpStatus;

public interface ApplicationResponse {

    HttpStatus status();
    HttpHeaders headers();
    String content();

    void addHeader(String key, String value);
}
