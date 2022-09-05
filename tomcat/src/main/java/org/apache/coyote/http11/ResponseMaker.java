package org.apache.coyote.http11;

import org.apache.coyote.http11.request.HttpRequest;

public interface ResponseMaker {

    String createResponse(final HttpRequest httpRequest) throws Exception;
}
