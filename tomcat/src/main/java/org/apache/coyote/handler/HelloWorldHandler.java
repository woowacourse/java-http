package org.apache.coyote.handler;

public class HelloWorldHandler extends AbstractHandler {

    @Override
    public boolean canHandle(final String requestTarget) {
        return requestTarget.equals("/");
    }

    @Override
    public String handle(final String requestUri) {
        final var responseBody = "Hello world!";

        return createOkResponse(responseBody, "text/html;charset=utf-8");
    }
}
