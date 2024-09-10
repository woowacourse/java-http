package org.apache.coyote.http11.response;

import java.util.Optional;
import org.apache.coyote.http11.Cookie;
import org.apache.coyote.http11.HttpHeaders;
import org.apache.coyote.http11.HttpStatusCode;
import org.apache.coyote.http11.serdes.ResponseSerializer;
import org.apache.coyote.http11.serdes.Serializer;

public class HttpResponse {

    public static final HttpVersion DEFAULT_VERSION = new HttpVersion(1.1);
    public static final String CONTENT_TYPE_DELIMITER = "\\.";
    private static final String CHARACTER_ENCODE_POLICY = "charset=utf-8";

    private final ViewResolver viewResolver;
    private final Serializer<HttpResponse> serialzer;
    private final HttpHeaders headers;
    private int statusCode;
    private String statusMessage;
    private Optional<String> responseBody;

    public HttpResponse() {
        this.viewResolver = new ViewResolver();
        this.serialzer = new ResponseSerializer();
        this.headers = new HttpHeaders();
        this.responseBody = Optional.empty();

    }

    public HttpResponse(HttpHeaders headers, int statusCode, String statusMessage, Optional<String> responseBody) {
        this.viewResolver = new ViewResolver();
        this.serialzer = new ResponseSerializer();
        this.headers = headers;
        this.statusCode = statusCode;
        this.statusMessage = statusMessage;
        this.responseBody = responseBody;
    }

    public HttpResponse(HttpHeaders headers, int statusCode, String statusMessage) {
        this(headers, statusCode, statusMessage, null);
    }

    public String serialize() {
        return serialzer.serialize(this);
    }

    public HttpResponse statusCode(HttpStatusCode statusCode) {
        this.statusCode = statusCode.getCode();
        this.statusMessage = statusCode.getMessage();
        return this;
    }

    public HttpResponse location(String location) {
        headers.location(location);
        headers.contentType(null);
        return this;
    }

    public HttpResponse contentType(String url) {
        String[] extension = url.split(CONTENT_TYPE_DELIMITER);
        if (extension.length >= 2) {
            String parsedType = "text/" + extension[1] + ";" + CHARACTER_ENCODE_POLICY;
            headers.contentType(parsedType);
        }
        return this;
    }

    public HttpResponse setCookie(Cookie cookie) {
        headers.setCookie(cookie);
        return this;
    }

    public HttpResponse viewUrl(String viewUrl) {
        String responseBody = viewResolver.findResponseFile(viewUrl);
        contentType(viewUrl);
        headers.contentLength(responseBody.getBytes().length);
        this.responseBody = Optional.of(responseBody);
        return this;
    }

    public HttpHeaders getHeaders() {
        return headers;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public Optional<String> getResponseBody() {
        return responseBody;
    }
}
