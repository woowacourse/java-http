package org.apache.coyote.exception;

public class CreatingSocketFailedException extends HttpConnectorException {

    public CreatingSocketFailedException() {
        super("Creating socket failed");
    }
}
