package nextstep.jwp.http;

public class HttpResponse {

    private static final String REQUEST_LINE_FORMAT = "%s %s ";

    private final HttpVersion httpVersion;
    private final HttpStatus httpStatus;
    private final HttpHeaders httpHeaders;
    private final HttpBody httpBody;

    public HttpResponse(
            HttpVersion httpVersion,
            HttpStatus httpStatus,
            HttpHeaders httpHeaders,
            HttpBody httpBody
    ) {
        this.httpVersion = httpVersion;
        this.httpStatus = httpStatus;
        this.httpHeaders = httpHeaders;
        this.httpBody = httpBody;
    }

    public void setCookie(String value) {
        httpHeaders.addHeader(HeaderType.SET_COOKIE.getValue(), value);
    }

    public byte[] getBytes() {
        String response = String.join("\r\n",
                String.format(REQUEST_LINE_FORMAT, httpVersion.getValue(), httpStatus.getValue()),
                httpHeaders.getHeaders(),
                "",
                httpBody.getMessage());

        return response.getBytes();
    }
}
