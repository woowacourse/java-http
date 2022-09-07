package nextstep.jwp.http.response;

import nextstep.jwp.http.ContentType;
import nextstep.jwp.http.HttpCookie;
import nextstep.jwp.http.HttpStatus;
import nextstep.jwp.http.HttpVersion;
import nextstep.jwp.http.Location;

public class HttpResponse {

    private static final String EMPTY_BODY = "";

    private final HttpVersion httpVersion;
    private final HttpStatus httpStatus;
    private final HttpResponseHeaders httpResponseHeaders;
    private final String responseBody;

    public HttpResponse(final HttpVersion httpVersion, final HttpStatus httpStatus,
                        final HttpResponseHeaders httpResponseHeaders, final String responseBody) {
        this.httpVersion = httpVersion;
        this.httpStatus = httpStatus;
        this.httpResponseHeaders = httpResponseHeaders;
        this.responseBody = responseBody;
    }

    public static HttpResponse ok(final HttpVersion httpVersion, final HttpCookie httpCookie,
                                  final String responseBody) {
        return new HttpResponse(httpVersion, HttpStatus.OK,
                new HttpResponseHeaders(Location.empty(), ContentType.TEXT_HTML, httpCookie), responseBody);
    }

    public static HttpResponse found(final HttpVersion httpVersion, final HttpCookie httpCookie,
                                     final Location location) {
        return new HttpResponse(httpVersion, HttpStatus.FOUND,
                new HttpResponseHeaders(location, ContentType.APPLICATION_JSON, httpCookie), EMPTY_BODY);
    }

    public byte[] httpResponse() {
        return createOutputResponse().getBytes();
    }

    public String createOutputResponse() {
        String response = httpVersion.getValue() + " " + httpStatus.httpResponseHeaderStatus() + " ";
        httpResponseHeaders.addHeader("Content-Length", String.valueOf(contentLength()));
        response = joinOutputResponseFormat(response, httpResponseHeaders.toHeaderFormat());
        return joinOutputResponseFormat(response, "", responseBody);
    }

    private String joinOutputResponseFormat(final String... response) {
        return String.join("\r\n", response);
    }

    private int contentLength() {
        return responseBody.getBytes()
                .length;
    }
}
