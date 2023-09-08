package nextstep.jwp.http.response;

import nextstep.jwp.http.common.HeaderType;
import nextstep.jwp.http.common.HttpBody;
import nextstep.jwp.http.common.HttpHeaders;
import nextstep.jwp.http.common.HttpStatus;

public class HttpResponse {

    private static final String REQUEST_LINE_FORMAT = "%s %s %s ";

    private final HttpStatusLine httpStatusLine;
    private final HttpHeaders httpHeaders;
    private final HttpBody httpBody;

    private HttpResponse(
            HttpStatusLine httpStatusLine,
            HttpHeaders httpHeaders,
            HttpBody httpBody
    ) {
        this.httpStatusLine = httpStatusLine;
        this.httpHeaders = httpHeaders;
        this.httpBody = httpBody;
    }

    public static HttpResponse createEmptyResponse() {
        return new HttpResponse(
                HttpStatusLine.createDefaultStatusLine(),
                HttpHeaders.createEmptyHeaders(),
                HttpBody.createEmptyHttpBody()
        );
    }

    public void setCookie(String value) {
        httpHeaders.addHeader(HeaderType.SET_COOKIE.getValue(), value);
    }

    public void setStatus(HttpStatus httpStatus) {
        httpStatusLine.setHttpStatus(httpStatus);
    }

    public void setHeader(String key, String value) {
        httpHeaders.addHeader(key, value);
    }

    public void setContentType(String value) {
        httpHeaders.setContentType(value);
    }

    public void setBody(String body) {
        httpBody.setBody(body);

        updateContentLength();
    }

    private void updateContentLength() {
        httpHeaders.setContentLength(String.valueOf(httpBody.getBytesLength()));
    }

    public byte[] getBytes() {
        String httpVersion = httpStatusLine.getHttpVersion().getValue();
        String httpStatusCode = httpStatusLine.getHttpStatus().getCode();
        String httpStatusMessage = httpStatusLine.getHttpStatus().getMessage();

        String response = String.join("\r\n",
                String.format(REQUEST_LINE_FORMAT, httpVersion, httpStatusCode, httpStatusMessage),
                httpHeaders.getHeaders(),
                "",
                httpBody.getBody());

        return response.getBytes();
    }

}
