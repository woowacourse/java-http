package org.apache.coyote.http11.http;

import java.io.BufferedWriter;
import org.apache.catalina.Session;
import org.apache.coyote.http11.http.domain.ContentType;
import org.apache.coyote.http11.http.domain.Headers;
import org.apache.coyote.http11.http.domain.HttpConstants;
import org.apache.coyote.http11.http.domain.MessageBody;
import org.apache.coyote.http11.http.domain.StatusCode;
import org.apache.coyote.http11.http.domain.StatusLine;
import org.apache.coyote.http11.util.FileReader;

public class HttpResponse {

    private final BufferedWriter bufferedWriter;
    private StatusLine statusLine;
    private final Headers headers;
    private MessageBody messageBody;

    private HttpResponse(final BufferedWriter bufferedWriter, final StatusLine statusLine, final Headers headers,
                         final MessageBody messageBody) {
        this.bufferedWriter = bufferedWriter;
        this.statusLine = statusLine;
        this.headers = headers;
        this.messageBody = messageBody;
    }

    public static HttpResponse from(final BufferedWriter bufferedWriter) {
        return new HttpResponse(bufferedWriter, null, Headers.emptyHeaders(), MessageBody.emptyBody());
    }

    public HttpResponse ok() {
        this.statusLine = StatusLine.http11(StatusCode.OK);
        return this;
    }

    public HttpResponse found() {
        this.statusLine = StatusLine.http11(StatusCode.FOUND);
        return this;
    }

    public HttpResponse methodNotAllowed() {
        MessageBody messageBody = new MessageBody(FileReader.read("405.html"));
        this.statusLine = StatusLine.http11(StatusCode.METHOD_NOT_ALLOWED);
        this.headers.contentType(ContentType.TEXT_HTML)
                .contentLength(messageBody.length());
        this.messageBody = messageBody;
        return this;
    }

    public HttpResponse setCookie(final Session session) {
        this.headers.setCookie(session.getId());
        return this;
    }

    public HttpResponse contentType(final ContentType contentType) {
        this.headers.contentType(contentType);
        return this;
    }

    public HttpResponse location(final String value) {
        this.headers.location(value);
        return this;
    }

    public HttpResponse body(final String body) {
        MessageBody messageBody = new MessageBody(body);
        this.headers.contentLength(messageBody.length());
        this.messageBody = messageBody;
        return this;
    }

    public void flushBuffer() {
        char[] httpMessage = String.join(HttpConstants.CRLF,
                statusLine.getStatusLine(),
                headers.getHeaders(),
                HttpConstants.BLANK,
                messageBody.getValue()).toCharArray();
        try {
            bufferedWriter.write(httpMessage);
            bufferedWriter.flush();
        } catch (Exception e) {
            throw new IllegalArgumentException("HttpResponse failed.");
        }
    }

    public StatusLine getStatusLine() {
        return statusLine;
    }

    public Headers getHeaders() {
        return headers;
    }

    public MessageBody getMessageBody() {
        return messageBody;
    }
}
