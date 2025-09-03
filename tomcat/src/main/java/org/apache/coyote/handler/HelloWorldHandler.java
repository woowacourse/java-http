package org.apache.coyote.handler;

public class HelloWorldHandler extends AbstractHandler {

    @Override
    public boolean canHandler(final String requestTarget) {
        return requestTarget.equals("/");
    }

    @Override
    public String handle(final String requestTarget) {
        final var responseBody = "Hello World!";

        return createResponse(responseBody, "text/html;charset=utf-8");    }
}
