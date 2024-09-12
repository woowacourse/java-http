package org.apache.coyote.http11.exception;

public class ReceivingRequestFailedException extends HttpConnectorException {

    public ReceivingRequestFailedException() {
        super("HttpRequest format not matched");
    }
}
