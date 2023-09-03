package org.apache.coyote.http11;

public interface ResponseMaker {

    String createResponse(final String request) throws Exception;
}
