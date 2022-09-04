package org.apache.coyote.servlet.response;

import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.servlet.cookie.HttpCookie;
import org.apache.coyote.servlet.cookie.HttpCookies;
import org.apache.coyote.support.HttpStatus;

public class ResponseEntity {

    private final HttpStatus status;
    private final String location;
    private final String viewResource;
    private final Map<String, HttpCookie> cookies;
    private final String contentType;
    private final String messageBody;

    private ResponseEntity(HttpStatus status,
                           String location,
                           String viewResource,
                           Map<String, HttpCookie> cookies,
                           String contentType,
                           String messageBody) {
        this.status = status;
        this.location = location;
        this.viewResource = viewResource;
        this.cookies = cookies;
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

    public String getViewResource() {
        return viewResource;
    }

    public ResponseEntityBuilder toBuilder() {
        return new ResponseEntityBuilder(status, location, cookies, contentType, messageBody);
    }

    public HttpResponse toHttpResponse() {
        return new HttpResponse(status, location, new HttpCookies(cookies), contentType, messageBody);
    }

    public static class ResponseEntityBuilder {

        final HttpStatus status;
        String location;
        String viewResource;
        final Map<String, HttpCookie> cookies;
        String contentType;
        String messageBody;

        private ResponseEntityBuilder(HttpStatus status,
                                      String location,
                                      Map<String, HttpCookie> cookies,
                                      String contentType,
                                      String messageBody) {
            this.status = status;
            this.location = location;
            this.cookies = cookies;
            this.contentType = contentType;
            this.messageBody = messageBody;
        }

        private ResponseEntityBuilder(HttpStatus status) {
            this(status, null, new HashMap<>(), null, null);
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

        public ResponseEntityBuilder setCookie(HttpCookie cookie) {
            this.cookies.put(cookie.getName(), cookie);
            return this;
        }

        public ResponseEntityBuilder setMessageBody(String messageBody) {
            this.messageBody = messageBody;
            return this;
        }

        public ResponseEntity build() {
            return new ResponseEntity(status, location, viewResource, cookies, contentType, messageBody);
        }
    }
}
