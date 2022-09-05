package org.apache.coyote.servlet.response;

import org.apache.coyote.support.HttpStatus;

public class ResponseEntity {

    private final HttpStatus status;
    private final String location;
    private final String viewResource;
    private final String contentType;
    private final String messageBody;

    private ResponseEntity(HttpStatus status,
                           String location,
                           String viewResource,
                           String contentType,
                           String messageBody) {
        this.status = status;
        this.location = location;
        this.viewResource = viewResource;
        this.contentType = contentType;
        this.messageBody = messageBody;
    }

    public static ResponseEntityBuilder status(HttpStatus status) {
        return new ResponseEntityBuilder(status);
    }

    public static ResponseEntityBuilder ok() {
        return status(HttpStatus.OK);
    }

    public static ResponseEntityBuilder redirect(String location) {
        return status(HttpStatus.FOUND).setLocation(location);
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getViewResource() {
        return viewResource;
    }

    public String getLocation() {
        return location;
    }

    public String getContentType() {
        return contentType;
    }

    public String getMessageBody() {
        return messageBody;
    }

    public ResponseEntityBuilder toBuilder() {
        return new ResponseEntityBuilder(status, location, contentType, messageBody);
    }

    public static class ResponseEntityBuilder {

        final HttpStatus status;
        String location;
        String viewResource;
        String contentType;
        String messageBody;

        private ResponseEntityBuilder(HttpStatus status,
                                      String location,
                                      String contentType,
                                      String messageBody) {
            this.status = status;
            this.location = location;
            this.contentType = contentType;
            this.messageBody = messageBody;
        }

        private ResponseEntityBuilder(HttpStatus status) {
            this(status, null, null, null);
        }

        public ResponseEntityBuilder setContentType(String contentType) {
            this.contentType = contentType;
            return this;
        }

        public ResponseEntityBuilder setLocation(String uri) {
            this.location = uri;
            return this;
        }

        public ResponseEntityBuilder setViewResource(String viewResource) {
            this.viewResource = viewResource;
            return this;
        }

        public ResponseEntityBuilder setMessageBody(String messageBody) {
            this.messageBody = messageBody;
            return this;
        }

        public ResponseEntity build() {
            return new ResponseEntity(status, location, viewResource, contentType, messageBody);
        }
    }
}
