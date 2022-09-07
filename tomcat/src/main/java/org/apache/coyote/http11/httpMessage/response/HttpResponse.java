package org.apache.coyote.http11.httpmessage.response;

import java.io.IOException;
import java.io.OutputStream;
import org.apache.coyote.http11.httpmessage.request.Headers;

public class HttpResponse {

    private final OutputStream outputStream;
    private StatusLine statusLine;
    private Headers headers;
    private String responseBody;

    public HttpResponse(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public HttpResponse setStatusLine(StatusLine statusLine) {
        this.statusLine = statusLine;
        return this;
    }

    public HttpResponse setHeaders(Headers headers) {
        this.headers = headers;
        return this;
    }

    public HttpResponse setResponseBody(String responseBody) {
        this.responseBody = responseBody;
        return this;
    }

    public void write() throws IOException {
        String result = this.toString();

        outputStream.write(result.getBytes());
        outputStream.flush();
    }

    @Override
    public String toString() {
        return String.join("\r\n",
                statusLine.toString(),
                headers.toString(),
                "",
                responseBody
        );
    }
}
