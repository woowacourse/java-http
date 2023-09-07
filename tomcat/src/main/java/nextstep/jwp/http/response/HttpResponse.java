package nextstep.jwp.http.response;

import nextstep.jwp.http.common.HeaderType;
import nextstep.jwp.http.common.HttpBody;
import nextstep.jwp.http.common.HttpHeaders;

public class HttpResponse {

    private static final String REQUEST_LINE_FORMAT = "%s %s %s ";

    private final HttpStatusLine httpStatusLine;
    private final HttpHeaders httpHeaders;
    private final HttpBody httpBody;

    public HttpResponse(
            HttpStatusLine httpStatusLine,
            HttpHeaders httpHeaders,
            HttpBody httpBody
    ) {
        this.httpStatusLine = httpStatusLine;
        this.httpHeaders = httpHeaders;
        this.httpBody = httpBody;
    }

    public void setCookie(String value) {
        httpHeaders.addHeader(HeaderType.SET_COOKIE.getValue(), value);
    }

    public byte[] getBytes() {
        String httpVersion = httpStatusLine.getHttpVersion().getValue();
        String httpStatusCode = httpStatusLine.getHttpStatus().getCode();
        String httpStatusMessage = httpStatusLine.getHttpStatus().getMessage();

        String response = String.join("\r\n",
                String.format(REQUEST_LINE_FORMAT, httpVersion, httpStatusCode, httpStatusMessage),
                httpHeaders.getHeaders(),
                "",
                httpBody.getMessage());

        return response.getBytes();
    }
}
