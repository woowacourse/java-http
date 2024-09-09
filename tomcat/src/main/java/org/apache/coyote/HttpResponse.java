package org.apache.coyote;

import org.apache.coyote.http11.HttpStatus;

public interface HttpResponse {

    String getResponseMessage();

    void setStatus(HttpStatus status);

    void setHeader(String key, String value);

    void setBody(String body);
}
