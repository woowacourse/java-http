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
    private final HttpCookies cookies;
    private final String contentType;
    private final String messageBody;

    private ResponseEntity(HttpStatus status,
                           String location,
                           String viewResource,
                           HttpCookies cookies,
                           String contentType,
                           String messageBody) {
        this.status = status;
        this.location = location;
        this.viewResource = viewResource;
        this.cookies = cookies;
        this.contentType = contentType;
        this.messageBody = messageBody;
    }

    public static ResponseEntityBuilder ok() {
        return new ResponseEntityBuilder(HttpStatus.OK);
    }

    public static ResponseEntityBuilder redirect(String location) {
        return new ResponseEntityBuilder(HttpStatus.FOUND)
                .setLocation(location);
    }

    public HttpResponse toHttpResponse() {
        return new HttpResponse(status, location, cookies, contentType, messageBody);
    }

    public String getViewResource() {
        return viewResource;
    }

    public static class ResponseEntityBuilder {

        final HttpStatus status;
        String location;
        String viewResource;
        final Map<String, HttpCookie> cookies = new HashMap<>();
        String contentType;
        String messageBody;

        private ResponseEntityBuilder(HttpStatus status) {
            this.status = status;
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
            return new ResponseEntity(status, location, viewResource,
                    new HttpCookies(cookies), contentType, messageBody);
        }
    }
}
