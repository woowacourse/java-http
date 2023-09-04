package org.apache.coyote.http11.response;

import org.apache.coyote.http11.request.HttpRequest;

public interface ResponseMaker {
    String createResponse(final HttpRequest request) throws Exception;
}
