package nextstep.jwp.http;

public class HttpResponse {

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

    public byte[] getBytes() {
        String response = String.join("\r\n",
                httpVersion.getValue() + " " + httpStatus.getValue(),
                httpHeaders.getHeaders(),
                "",
                httpBody.getHttpBody());

        return response.getBytes();
    }
}
