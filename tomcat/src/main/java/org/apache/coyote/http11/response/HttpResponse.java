package org.apache.coyote.http11.response;

import org.apache.coyote.http11.Cookie;
import org.apache.coyote.http11.StatusCode;
import org.apache.coyote.http11.serdes.ResponseSerializer;
import org.apache.coyote.http11.serdes.Serializer;

public class HttpResponse {

    public static final HttpVersion DEFAULT_VERSION = new HttpVersion(1.1);
    public static final String CONTENT_TYPE_DELIMITER = "\\.";
    private static final String CHARACTER_ENCODE_POLICY = "charset=utf-8";

    private final ViewResolver viewResolver;
    private final Serializer<HttpResponse> serialzer;
    private final HttpResponseHeader headers;
    private ResponseBody responseBody;

    public HttpResponse() {
        this.viewResolver = new ViewResolver();
        this.serialzer = new ResponseSerializer();
        this.headers = new HttpResponseHeader();
    }

    public HttpResponse(HttpResponseHeader headers, int statusCode, String statusMessage, ResponseBody responseBody) {
        this.viewResolver = new ViewResolver();
        this.serialzer = new ResponseSerializer();
        this.headers = headers;
        this.responseBody = responseBody;
    }

    public HttpResponse(HttpResponseHeader headers, int statusCode, String statusMessage) {
        this(headers, statusCode, statusMessage, null);
    }

    public static HttpResponse ok(String viewUrl) {
        return new HttpResponse()
                .statusCode(StatusCode.OK_200)
                .viewUrl(viewUrl);
    }

    public HttpResponse redirect(String redirectUrl) {
        this.statusCode(StatusCode.FOUND_302);
        this.location(redirectUrl);
        return this;
    }

    public String serialize() {
        return serialzer.serialize(this);
    }

    public HttpResponse statusCode(StatusCode code) {
        headers.statusCode(code);
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
        this.responseBody = new ResponseBody(responseBody);
        contentType(viewUrl);
        headers.contentLength(responseBody.getBytes().length);
        return this;
    }

    public HttpResponseHeader getHeaders() {
        return headers;
    }

    public StatusCode getStatusCode() {
        return headers.getStatusCode();
    }

    public ResponseBody getResponseBody() {
        return responseBody;
    }
}
