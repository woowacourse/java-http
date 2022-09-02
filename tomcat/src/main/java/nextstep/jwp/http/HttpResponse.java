package nextstep.jwp.http;

public class HttpResponse {

    private static final String EMPTY_BODY = "";

    private final HttpVersion httpVersion;
    private final HttpStatus httpStatus;
    private final ContentType contentType;
    private final String location;
    private final String responseBody;

    public HttpResponse(final HttpVersion httpVersion, final HttpStatus httpStatus, final ContentType contentType,
                        final String location, final String responseBody) {
        this.httpVersion = httpVersion;
        this.httpStatus = httpStatus;
        this.contentType = contentType;
        this.location = location;
        this.responseBody = responseBody;
    }

    public static HttpResponse found(final HttpVersion httpVersion, final String location) {
        return new HttpResponse(httpVersion, HttpStatus.FOUND, ContentType.APPLICATION_JSON, location, EMPTY_BODY);
    }

    public byte[] httpResponse() {
        System.out.println(createOutputResponse());
        return createOutputResponse().getBytes();
    }

    public String createOutputResponse() {
        return String.join("\r\n",
                httpVersion + " " + httpStatus.httpResponseHeaderStatus() + " ",
                "Content-Type: " + contentType.getType() + ";charset=utf-8 ",
                "Content-Length: " + contentLength() + " " + parseLocation(),
                "",
                responseBody);
    }

    private String parseLocation() {
        if (location.isEmpty()) {
            return EMPTY_BODY;
        }
        return "\nLocation: " + location + " ";
    }

    private int contentLength() {
        return responseBody.getBytes()
                .length;
    }
}
