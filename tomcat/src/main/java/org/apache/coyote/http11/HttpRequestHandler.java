package org.apache.coyote.http11;

public interface HttpRequestHandler {

    boolean support(RequestStartLine requestStartLine);

    String response(RequestStartLine requestStartLine);
}
