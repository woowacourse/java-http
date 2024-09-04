package org.apache.coyote;

public interface HttpRequest {

    String getRequestURI();

    String getHeaderValue(String header);
}
