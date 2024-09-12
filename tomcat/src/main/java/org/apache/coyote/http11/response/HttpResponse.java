package org.apache.coyote.http11.response;

import static org.apache.coyote.http11.header.HeaderContent.CONTENT_LENGTH;
import static org.apache.coyote.http11.header.HeaderContent.CONTENT_TYPE;
import static org.apache.coyote.http11.header.HeaderContent.LOCATION;
import static org.apache.coyote.http11.header.HeaderContent.SET_COOKIE;

import org.apache.coyote.http11.Cookie;
import org.apache.coyote.http11.StatusCode;
import org.apache.coyote.http11.header.HttpResponseHeader;
import org.apache.coyote.http11.serdes.ResponseHeaderSerializer;
import org.apache.coyote.http11.serdes.ResponseSerializer;
import org.apache.coyote.http11.serdes.Serializer;

public class HttpResponse {

    public static final HttpVersion DEFAULT_VERSION = new HttpVersion(1.1);
    public static final String CONTENT_TYPE_DELIMITER = "\\.";
    private static final String CHARACTER_ENCODE_POLICY = ";charset=utf-8";
    public static final int PARSED_CONTENT_TYPE_LENGTH = 2;
    public static final int EXTENSION_INDEX = 1;
    public static final String TEXT_TYPE_PREFIX = "text/";

    private final ViewResolver viewResolver;
    private final Serializer<HttpResponse> serializer;
    private final HttpResponseHeader headers;
    private ResponseBody responseBody;

    public HttpResponse() {
        this.viewResolver = new ViewResolver();
        this.serializer = new ResponseSerializer(new ResponseHeaderSerializer());
        this.headers = new HttpResponseHeader();
    }

    public HttpResponse(HttpResponseHeader headers, int statusCode, String statusMessage, ResponseBody responseBody) {
        this.viewResolver = new ViewResolver();
        this.serializer = new ResponseSerializer(new ResponseHeaderSerializer());
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
        headers.put(LOCATION, redirectUrl);
        headers.put(CONTENT_TYPE, null);
        return this;
    }

    public boolean hasResponseBody() {
        return responseBody != null;
    }

    public String serialize() {
        return serializer.serialize(this);
    }

    public HttpResponse statusCode(StatusCode code) {
        headers.statusCode(code);
        return this;
    }

    public HttpResponse setCookie(Cookie cookie) {
        headers.put(SET_COOKIE, cookie.serialize());
        return this;
    }

    public HttpResponse viewUrl(String viewUrl) {
        String responseBody = viewResolver.findResponseFile(viewUrl);
        this.responseBody = new ResponseBody(responseBody);
        headers.put(CONTENT_TYPE, parseContentType(viewUrl));
        headers.put(CONTENT_LENGTH, String.valueOf(responseBody.getBytes().length));
        return this;
    }

    private String parseContentType(String url) {
        String[] extension = url.split(CONTENT_TYPE_DELIMITER);
        if (extension.length != PARSED_CONTENT_TYPE_LENGTH) {
            throw new IllegalArgumentException("잘못된 view url 입니다.");
        }

        return  TEXT_TYPE_PREFIX + extension[EXTENSION_INDEX] + CHARACTER_ENCODE_POLICY;
    }

    public HttpResponseHeader getHeaders() {
        return headers;
    }

    public ResponseBody getResponseBody() {
        return responseBody;
    }
}
