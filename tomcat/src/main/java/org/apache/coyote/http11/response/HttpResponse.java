package org.apache.coyote.http11.response;

import java.io.IOException;
import java.io.OutputStream;
import org.apache.coyote.http11.HttpHeaders;
import org.apache.coyote.http11.HttpMessageBody;
import org.apache.coyote.http11.response.line.ResponseLine;

public class HttpResponse {

    private final ResponseLine responseLine;
    private final HttpHeaders httpHeaders;
    private final HttpMessageBody httpMessageBody;

    public HttpResponse(
            final ResponseLine responseLine,
            final HttpHeaders httpHeaders,
            final HttpMessageBody httpMessageBody
    ) {
        this.responseLine = responseLine;
        this.httpHeaders = httpHeaders;
        this.httpMessageBody = httpMessageBody;
    }

    public static HttpResponse ok(String responseBody, String contentType) {
        ResponseLine line = ResponseLine.createOkResponseLine();
        HttpMessageBody body = new HttpMessageBody(responseBody);
        HttpHeaders headers = new HttpHeaders();
        headers.putHeader("Content-Type", "text/" + contentType + ";charset=utf-8 ");
        headers.putHeader("Content-Length", body.getBytes().length + " ");

        return new HttpResponse(line, headers, body);
    }

    public static HttpResponse redirect(String uri) {
        ResponseLine line = ResponseLine.createFoundLine();
        HttpMessageBody body = HttpMessageBody.createEmptyBody();
        HttpHeaders headers = new HttpHeaders();
        headers.putHeader("Location", uri);
        return new HttpResponse(line, headers, body);
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
}
