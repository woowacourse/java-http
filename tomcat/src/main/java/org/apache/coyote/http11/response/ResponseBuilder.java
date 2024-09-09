package org.apache.coyote.http11.response;

import java.util.Optional;
import org.apache.coyote.http11.Cookie;
import org.apache.coyote.http11.HttpHeaders;
import org.apache.coyote.http11.HttpStatusCode;

public class ResponseBuilder {
    private static final String CHARACTER_ENCODE_POLICY = "charset=utf-8";

    private final ViewResolver viewResolver = new ViewResolver();
    private final HttpHeaderBuilder headerBuilder = new HttpHeaderBuilder();
    private int statusCode;
    private Optional<String> statusMessage = Optional.empty();
    private Optional<String> responseBody = Optional.empty();

    public ResponseBuilder statusCode(HttpStatusCode statusCode) {
        this.statusCode = statusCode.getCode();
        this.statusMessage = Optional.of(statusCode.getMessage());
        return this;
    }

    public ResponseBuilder location(String location) {
        headerBuilder.location(location);
        headerBuilder.contentType(null);
        return this;
    }

    public ResponseBuilder contentType(String url) {
        String[] extension = url.split("\\.");
        if (extension.length >= 2) {
            String parsedType = "text/" + extension[1] + ";" + CHARACTER_ENCODE_POLICY;
            headerBuilder.contentType(parsedType);
        }
        return this;
    }

    public ResponseBuilder setCookie(Cookie cookie){
        headerBuilder.setCookie(cookie);
        return this;
    }

    public ResponseBuilder viewUrl(String viewUrl) {
        String responseBody = viewResolver.findResponseFile(viewUrl);
        contentType(viewUrl);
        headerBuilder.contentLength(responseBody.getBytes().length);
        this.responseBody = Optional.of(responseBody);
        return this;
    }

    public HttpResponse build() {
        HttpHeaders header = headerBuilder.build();
        return new HttpResponse(header, statusCode, statusMessage.get(), responseBody);
    }
}
