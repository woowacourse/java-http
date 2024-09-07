package org.apache.coyote.http11.response;

import java.util.Optional;
import org.apache.coyote.http11.request.HttpHeaders;

public class ResponseBuilder {

    private final HttpHeaderBuilder headerBuilder = new HttpHeaderBuilder();
    private String protocol = "HTTP";
    private double version = 1.1;
    private int statusCode;
    private Optional<String> statusMessage;
    private Optional<String> responseBody;
    private Optional<String> location;


    public ResponseBuilder statusMessge(String statusMessage) {
        this.statusMessage = Optional.of(statusMessage);
        return this;
    }

    public ResponseBuilder version(double version) {
        this.version = version;
        return this;
    }

    public ResponseBuilder protocol(String protocol) {
        this.protocol = protocol;
        return this;
    }

    public ResponseBuilder statusCode(int statusCode) {
        this.statusCode = statusCode;
        return this;
    }

    public ResponseBuilder location(String location) {
        headerBuilder.location(location);
        return this;
    }

    public ResponseBuilder contentType(String url) {
        String[] extension = url.split("\\.");
        if (extension.length >= 2) {
            headerBuilder.contentType("text/" + extension[1]);
        }
        return this;
    }

    public ResponseBuilder responseBody(String responseBody) {
        this.responseBody = Optional.of(responseBody);
        headerBuilder.contentLength(responseBody.getBytes().length);
        return this;
    }

    public HttpResponse build() {
        HttpHeaders header = headerBuilder.build();
        return new HttpResponse(header, statusCode, statusMessage.get(), responseBody);
    }
}
