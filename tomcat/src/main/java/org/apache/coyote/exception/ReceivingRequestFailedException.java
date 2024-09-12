package org.apache.coyote.exception;

public class ReceivingRequestFailedException extends HttpConnectorException {

    public ReceivingRequestFailedException() {
        super("Receiving request failed");
    }
}
