package org.apache.coyote.http.response;

import org.apache.coyote.http.HttpHeaderName;
import org.apache.coyote.http.HttpHeaders;
import org.apache.coyote.http.HttpMessageBody;
import org.apache.coyote.http.MimeType;
import org.apache.coyote.http.response.line.HttpStatus;
import org.apache.coyote.http.response.line.ResponseLine;
import org.apache.util.FileUtils;

public class HttpResponse {

    public static final String JSESSION_COOKIE_PREFIX = "JSESSIONID=";

    private final ResponseLine responseLine;
    private final HttpHeaders httpHeaders;
    private final HttpMessageBody httpMessageBody;

    public HttpResponse(ResponseLine responseLine, HttpHeaders httpHeaders, HttpMessageBody httpMessageBody) {
        this.responseLine = responseLine;
        this.httpHeaders = httpHeaders;
        this.httpMessageBody = httpMessageBody;
    }

    public static HttpResponse createEmptyResponse() {
        ResponseLine emptyLine = ResponseLine.createEmptyResponseLine();
        HttpHeaders emptyHeaders = new HttpHeaders();
        HttpMessageBody emptyBody = HttpMessageBody.createEmptyBody();

        return new HttpResponse(emptyLine, emptyHeaders, emptyBody);
    }

    public void ok(String fileName) {
        responseLine.setHttpStatus(HttpStatus.OK);
        writeBody(FileUtils.readFile(fileName), MimeType.from(fileName).getContentType());
    }

    public void sendRedirect(String location) {
        responseLine.setHttpStatus(HttpStatus.FOUND);
        httpHeaders.putHeader(HttpHeaderName.LOCATION, location);
    }

    public void sendError(HttpStatus statusCode, String fileName) {
        responseLine.setHttpStatus(statusCode);
        writeBody(FileUtils.readFile(fileName), MimeType.from(fileName).getContentType());
    }

    private void writeBody(String bodyMessage, String contentType) {
        httpHeaders.putHeader(HttpHeaderName.CONTENT_TYPE, contentType);
        httpHeaders.putHeader(HttpHeaderName.CONTENT_LENGTH, String.valueOf(bodyMessage.getBytes().length));
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

    public HttpStatus getHttpStatus() {
        return responseLine.getHttpStatus();
    }
}
