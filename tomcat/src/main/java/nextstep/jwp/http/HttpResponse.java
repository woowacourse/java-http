package nextstep.jwp.http;

public class HttpResponse {

    private static final String EMPTY_BODY = "";

    private final HttpVersion httpVersion;
    private final HttpStatus httpStatus;
    private final ContentType contentType;
    private final Location location;
    private final HttpCookie httpCookie;
    private final String responseBody;

    public HttpResponse(final HttpVersion httpVersion, final HttpStatus httpStatus, final ContentType contentType,
                        final Location location, final HttpCookie httpCookie, final String responseBody) {
        this.httpVersion = httpVersion;
        this.httpStatus = httpStatus;
        this.contentType = contentType;
        this.location = location;
        this.httpCookie = httpCookie;
        this.responseBody = responseBody;
    }

    public static HttpResponse ok(final HttpVersion httpVersion, final HttpCookie httpCookie, final String responseBody) {
        return new HttpResponse(httpVersion, HttpStatus.OK, ContentType.TEXT_HTML, Location.empty(), httpCookie, responseBody);
    }

    public static HttpResponse found(final HttpVersion httpVersion, final HttpCookie httpCookie, final Location location) {
        return new HttpResponse(httpVersion, HttpStatus.FOUND, ContentType.APPLICATION_JSON, location, httpCookie, EMPTY_BODY);
    }

    public byte[] httpResponse() {
        return createOutputResponse().getBytes();
    }

    public String createOutputResponse() {
        String response = joinOutputResponseFormat(
                httpVersion.getValue() + " " + httpStatus.httpResponseHeaderStatus() + " ",
                "Content-Type: " + contentType.getType() + ";charset=utf-8 ",
                "Content-Length: " + contentLength() + " "
        );
        if (!location.isEmpty()) {
            response = joinOutputResponseFormat(response, location.toHeaderFormat());
        }
        return joinOutputResponseFormat(response, "", responseBody);
    }

    private String joinOutputResponseFormat(final String ... response) {
        return String.join("\r\n", response);
    }

    private int contentLength() {
        return responseBody.getBytes()
                .length;
    }
}
