package org.apache.coyote.http11.response;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;
import org.apache.coyote.http11.HttpHeaderName;
import org.apache.coyote.http11.HttpHeaders;
import org.apache.coyote.http11.HttpMessageBody;
import org.apache.coyote.http11.response.line.ResponseLine;

public class HttpServletResponse {

    private final ResponseLine responseLine;
    private final HttpHeaders httpHeaders;
    private final HttpMessageBody httpMessageBody;

    public HttpServletResponse(ResponseLine responseLine, HttpHeaders httpHeaders, HttpMessageBody httpMessageBody) {
        this.responseLine = responseLine;
        this.httpHeaders = httpHeaders;
        this.httpMessageBody = httpMessageBody;
    }

    public static HttpServletResponse ok(String responseBody, String contentType) {
        ResponseLine line = ResponseLine.createOkResponseLine();
        HttpMessageBody body = new HttpMessageBody(responseBody);
        HttpHeaders headers = new HttpHeaders();
        headers.putHeader(HttpHeaderName.CONTENT_TYPE, "text/" + contentType + ";charset=utf-8 ");
        headers.putHeader(HttpHeaderName.CONTENT_LENGTH, body.getBytes().length + " ");

        return new HttpServletResponse(line, headers, body);
    }

    public static HttpServletResponse unauthorized(String responseBody, String contentType) {
        ResponseLine line = ResponseLine.createUnauthorizedLine();
        HttpMessageBody body = new HttpMessageBody(responseBody);
        HttpHeaders headers = new HttpHeaders();
        headers.putHeader(HttpHeaderName.CONTENT_TYPE, "text/" + contentType + ";charset=utf-8 ");
        headers.putHeader(HttpHeaderName.CONTENT_LENGTH, body.getBytes().length + " ");

        return new HttpServletResponse(line, headers, body);
    }

    public static HttpServletResponse redirect(String uri) {
        ResponseLine line = ResponseLine.createFoundLine();
        HttpMessageBody body = HttpMessageBody.createEmptyBody();
        HttpHeaders headers = new HttpHeaders();
        headers.putHeader(HttpHeaderName.LOCATION, uri);
        return new HttpServletResponse(line, headers, body);
    }

    public void flush(OutputStream outputStream) throws IOException {
        outputStream.write(resolveHttpMessage().getBytes());
        outputStream.flush();
    }

    private String resolveHttpMessage() {
        String lineMessage = responseLine.resolveLineMessage();
        String headersMessage = httpHeaders.resolveHeadersMessage();
        String bodyMessage = httpMessageBody.resolveBodyMessage();

        return String.join("\r\n", lineMessage, headersMessage, bodyMessage);
    }

    public void setJsessionCookie(UUID uuid) {
        httpHeaders.putHeader(HttpHeaderName.SET_COOKIE, "JSESSIONID=" + uuid);
    }
}
