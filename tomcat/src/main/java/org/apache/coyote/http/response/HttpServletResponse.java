package org.apache.coyote.http.response;

import org.apache.coyote.http.response.line.HttpStatus;
import org.apache.coyote.http.HttpHeaderName;
import org.apache.coyote.http.HttpHeaders;
import org.apache.coyote.http.HttpMessageBody;
import org.apache.coyote.http.response.line.ResponseLine;

public class HttpServletResponse {

    public static final String JSESSION_COOKIE_PREFIX = "JSESSIONID=";
    private ResponseLine responseLine;
    private final HttpHeaders httpHeaders;
    private final HttpMessageBody httpMessageBody;

    public HttpServletResponse(ResponseLine responseLine, HttpHeaders httpHeaders, HttpMessageBody httpMessageBody) {
        this.responseLine = responseLine;
        this.httpHeaders = httpHeaders;
        this.httpMessageBody = httpMessageBody;
    }

    public static HttpServletResponse createEmptyResponse() {
        ResponseLine emptyLine = ResponseLine.createEmptyResponseLine();
        HttpHeaders emptyHeaders = new HttpHeaders();
        HttpMessageBody emptyBody = HttpMessageBody.createEmptyBody();

        return new HttpServletResponse(emptyLine, emptyHeaders, emptyBody);
    }

    public void ok(String responseBody, String contentType) {
        responseLine.setHttpStatus(HttpStatus.OK);
        writeBody(responseBody, contentType);
    }

    public void unauthorized(String responseBody, String contentType) {
        responseLine.setHttpStatus(HttpStatus.UNAUTHORIZED);
        writeBody(responseBody, contentType);
    }

    public void redirect(String uri) {
        responseLine.setHttpStatus(HttpStatus.FOUND);
        httpHeaders.putHeader(HttpHeaderName.LOCATION, uri);
    }

    private void writeBody(String bodyMessage, String contentType) {
        httpHeaders.putHeader(HttpHeaderName.CONTENT_TYPE, "text/" + contentType + ";charset=utf-8 ");
        httpHeaders.putHeader(HttpHeaderName.CONTENT_LENGTH, bodyMessage.getBytes().length + " ");
        httpMessageBody.write(bodyMessage);
    }

    public String resolveHttpMessage() {
        String lineMessage = responseLine.resolveLineMessage();
        String headersMessage = httpHeaders.resolveHeadersMessage();
        String bodyMessage = httpMessageBody.resolveBodyMessage();

        return String.join("\r\n", lineMessage, headersMessage, bodyMessage);
    }

    public void setJsession(String uuid) {
        httpHeaders.putHeader(HttpHeaderName.SET_COOKIE, JSESSION_COOKIE_PREFIX + uuid);
    }
}
