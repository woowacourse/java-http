package org.apache.coyote.http11;

public interface HttpRequest {

    HttpMethod getHttpMethod();

    String getRequestUri();
}
