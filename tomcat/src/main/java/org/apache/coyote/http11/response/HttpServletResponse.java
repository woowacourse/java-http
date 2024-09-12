package org.apache.coyote.http11.response;

import java.util.UUID;
import org.apache.coyote.http11.HttpHeaderName;
import org.apache.coyote.http11.HttpHeaders;
import org.apache.coyote.http11.HttpMessageBody;
import org.apache.coyote.http11.response.line.HttpStatus;
import org.apache.coyote.http11.response.line.ResponseLine;

public class HttpServletResponse {

    private ResponseLine responseLine;
    private final HttpHeaders httpHeaders;
    private final HttpMessageBody httpMessageBody;

    public HttpServletResponse(ResponseLine responseLine, HttpHeaders httpHeaders, HttpMessageBody httpMessageBody) {
        this.responseLine = responseLine;
        this.httpHeaders = httpHeaders;
        this.httpMessageBody = httpMessageBody;
    }

    public void ok(String responseBody, String contentType) {
        responseLine.setHttpStatus(HttpStatus.OK);
        writeBody(responseBody, contentType);
    }

    public void unauthorized(String responseBody, String contentType) {
        responseLine.setHttpStatus(HttpStatus.UNAUTHORIZED);
        writeBody(responseBody, contentType);
    }

    private void writeBody(String bodyMessage, String contentType) {
        httpHeaders.putHeader(HttpHeaderName.CONTENT_TYPE, "text/" + contentType + ";charset=utf-8 ");
        httpHeaders.putHeader(HttpHeaderName.CONTENT_LENGTH, bodyMessage.getBytes().length + " ");
        httpMessageBody.write(bodyMessage);
    }

    public static HttpServletResponse createEmptyResponse() {
        ResponseLine emptyLine = ResponseLine.createEmptyResponseLine();
        HttpHeaders emptyHeaders = new HttpHeaders();
        HttpMessageBody emptyBody = HttpMessageBody.createEmptyBody();

        return new HttpServletResponse(emptyLine, emptyHeaders, emptyBody);
    }

    public void redirect(String uri) {
        responseLine.setHttpStatus(HttpStatus.FOUND);
        httpHeaders.putHeader(HttpHeaderName.LOCATION, uri);
    }

    public String resolveHttpMessage() {
        String lineMessage = responseLine.resolveLineMessage();
        String headersMessage = httpHeaders.resolveHeadersMessage();
        String bodyMessage = httpMessageBody.resolveBodyMessage();

        return String.join("\r\n", lineMessage, headersMessage, bodyMessage);
    }

    public void setJsessionCookie(UUID uuid) {
        httpHeaders.putHeader(HttpHeaderName.SET_COOKIE, "JSESSIONID=" + uuid);
    }
}
