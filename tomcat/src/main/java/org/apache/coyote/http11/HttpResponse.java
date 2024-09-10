package org.apache.coyote.http11;

import java.util.Map;

public class HttpResponse {

    private static final String RESPONSE_HEADER_FORMAT = "%s: %s \r\n";

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

    public static HttpResponse createHttp11Response() {
        return new HttpResponse(
                HttpVersion.HTTP_1_1,
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
        String message = String.join("\r\n", getStartLine(), getHeaders(), getBody());
        return message.getBytes();
    }

    private String getStartLine() {
        return httpVersion.getVersionName() + " " + httpStatus.getDescription() + " ";
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

    public void setHttpStatus(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    public void setResponseBody(byte[] responseBody) {
        this.responseBody = responseBody;
    }

    public void setContentType(ContentType contentType) {
        this.header.append(HttpHeaderKey.CONTENT_TYPE, contentType.getName());
    }
}
