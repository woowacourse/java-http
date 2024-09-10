package org.apache.coyote;

import org.apache.catalina.Session;
import org.apache.coyote.http11.HttpStatus;

public interface HttpResponse {

    String getResponseMessage();

    void setStatus(HttpStatus status);

    void setLocationHeader(String value);

    void setContentTypeHeader(String value);

    void setSessionHeader(Session session);

    void setBody(String body);
}
