package jakarta.http;

import java.util.Map;

public class HttpResponse {

    private static final String RESPONSE_HEADER_FORMAT = "%s: %s \r\n";
    private static final String CRLF = "\r\n";
    private static final String SP = " ";

    private HttpVersion httpVersion;
    private HttpStatus httpStatus;
    private Header header;
    private byte[] responseBody;

    public HttpResponse(
            HttpVersion httpVersion,
            HttpStatus httpStatus,
            Header header,
            byte[] responseBody
    ) {
        this.httpVersion = httpVersion;
        this.httpStatus = httpStatus;
        this.header = header;
        this.responseBody = responseBody;
    }

    public static HttpResponse createHttpResponse(HttpVersion version) {
        return new HttpResponse(
                version,
                HttpStatus.OK,
                Header.empty(),
                new byte[]{}
        );
    }

    public void sendRedirect(String redirectionPath) {
        this.httpStatus = HttpStatus.FOUND;
        this.header.append(HttpHeaderKey.LOCATION, redirectionPath);
    }

    public byte[] serialize() {
        String message = String.join(CRLF, getStartLine(), getHeaders(), getBody());
        return message.getBytes();
    }

    private String getStartLine() {
        return httpVersion.getVersionName() + SP + httpStatus.getDescription() + SP;
    }

    private CharSequence getHeaders() {
        StringBuilder stringBuilder = new StringBuilder();
        Map<String, String> headerMap = header.getHeader();
        headerMap.forEach((key, value) -> stringBuilder.append(String.format(RESPONSE_HEADER_FORMAT, key, value)));
        String format = String.format(RESPONSE_HEADER_FORMAT, HttpHeaderKey.CONTENT_LENGTH.getName(), responseBody.length);
        stringBuilder.append(format);

        return stringBuilder;
    }

    public String getBody() {
        return new String(responseBody);
    }

    public Header getHeader() {
        return header;
    }

    public HttpStatus getStatus() {
        return httpStatus;
    }

    public void setHttpStatus(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    public void setResponseBody(byte[] responseBody) {
        this.responseBody = responseBody;
    }

    public void setContentType(ContentType contentType) {
        this.header.append(HttpHeaderKey.CONTENT_TYPE, contentType.getName());
    }

    public void setHttpVersion(HttpVersion httpVersion) {
        this.httpVersion = httpVersion;
    }
}
