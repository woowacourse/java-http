package org.apache.coyote.http11.http;

import java.io.BufferedWriter;
import org.apache.coyote.http11.http.domain.ContentType;
import org.apache.coyote.http11.http.domain.Headers;
import org.apache.coyote.http11.http.domain.HttpConstants;
import org.apache.coyote.http11.http.domain.HttpVersion;
import org.apache.coyote.http11.http.domain.MessageBody;
import org.apache.coyote.http11.http.domain.StatusCode;
import org.apache.coyote.http11.http.domain.StatusLine;
import org.apache.coyote.http11.util.FileReader;

public class HttpResponse {

    private final BufferedWriter bufferedWriter;
    private StatusLine statusLine;
    private Headers headers;
    private MessageBody messageBody;

    private HttpResponse(final BufferedWriter bufferedWriter) {
        this.bufferedWriter = bufferedWriter;
    }

    public static HttpResponse from(final BufferedWriter bufferedWriter) {
        return new HttpResponse(bufferedWriter);
    }

    public void ok(final ContentType contentType, final MessageBody messageBody) {
        this.statusLine = new StatusLine(HttpVersion.HTTP_1_1, StatusCode.OK);
        this.headers = Headers.builder()
                .contentType(contentType)
                .contentLength(messageBody.length());
        this.messageBody = messageBody;
        writeAndFlush();
    }

    public void methodNotAllowed() {
        MessageBody messageBody = new MessageBody(FileReader.read("405.html"));
        this.statusLine = new StatusLine(HttpVersion.HTTP_1_1, StatusCode.METHOD_NOT_ALLOWED);
        this.headers = Headers.builder()
                .contentType(ContentType.TEXT_HTML)
                .contentLength(messageBody.length());
        this.messageBody = messageBody;
        writeAndFlush();
    }

    public void found(final Headers headers, final MessageBody messageBody) {
        this.statusLine = new StatusLine(HttpVersion.HTTP_1_1, StatusCode.FOUND);
        this.headers = headers;
        this.messageBody = messageBody;
        writeAndFlush();
    }

    private void writeAndFlush() {
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
