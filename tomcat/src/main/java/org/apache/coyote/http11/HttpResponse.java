package org.apache.coyote.http11;

import java.io.IOException;
import java.io.OutputStream;

public class HttpResponse {
    private static final String CONTENT_LENGTH = "Content-Length";
    private static final String CRLF = "\r\n";

    private final OutputStream outputStream;
    private ResponseLine responseLine = new ResponseLine(HttpVersion.HTTP_1_1, HttpStatusCode.HTTP_STATUS_200,
            new ReasonPhrase("OK"));
    private HttpHeaders httpHeaders = new HttpHeaders();
    private HttpBody httpBody = new HttpBody("");

    public HttpResponse(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public void write() throws IOException {
        setContentLength();
        outputStream.write(buildResponse().getBytes());
        outputStream.flush();
    }

    public void putHeader(String key, String value) {
        httpHeaders.put(key, value);
    }

    public void setResponseLine(HttpVersion httpVersion, HttpStatusCode httpStatus, ReasonPhrase reasonPhrase) {
        this.responseLine = new ResponseLine(httpVersion, httpStatus, reasonPhrase);
    }

    public void setHttpBody(HttpBody body) {
        this.httpBody = body;
    }

    private void setContentLength() {
        httpHeaders.put(CONTENT_LENGTH, String.valueOf(httpBody.getLength()));
    }

    private String buildResponse() {
        StringBuilder response = new StringBuilder();

        response.append(responseLine.getHttpVersion().getValue()).append(' ')
                .append(responseLine.getHttpStatusCode().getValue()).append(' ')
                .append(responseLine.getReasonPhrase().getValue())
                .append(CRLF);

        for (String name : httpHeaders.getHeaders().keySet()) {
            response.append(name).append(": ")
                    .append(httpHeaders.getHeaders().get(name))
                    .append(CRLF);
        }

        response.append(CRLF);
        response.append(httpBody.getValue());

        return response.toString();
    }
}
