package org.apache.coyote.http11;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

public class HttpResponse {
    private static final String CONTENT_LENGTH = "Content-Length";
    private OutputStream outputStream;
    private ResponseLine responseLine;
    private HttpHeaders httpHeaders = new HttpHeaders();
    private HttpBody httpBody;

    public HttpResponse(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public ResponseLine getResponseLine() {
        return responseLine;
    }

    public void setResponseLine(HttpVersion httpVersion, HttpStatusCode httpStatus, ReasonPhrase reasonPhrase) {
        this.responseLine = new ResponseLine(httpVersion, httpStatus, reasonPhrase);
    }

    public HttpHeaders getHttpHeaders() {
        return httpHeaders;
    }

    public void setHttpHeaders(HttpHeaders httpHeaders) {
        this.httpHeaders = httpHeaders;
    }

    public HttpBody getHttpBody() {
        return httpBody;
    }

    public void setHttpBody(HttpBody body) {
        this.httpBody = body;
    }

    public void putHeader(String key, String value) {
        httpHeaders.put(key, value);
    }

    public void write() throws IOException {
        setContentLength();
        outputStream.write(buildToResponse().getBytes());
        outputStream.flush();
    }

    private void setContentLength() {
        if (httpBody == null) {
            return;
        }
        httpHeaders.put(CONTENT_LENGTH, String.valueOf(httpBody.getLength()));
    }

    private String buildToResponse() {
        Map<String, String> headers = httpHeaders.getHeaders();
        StringBuilder sb = new StringBuilder();
        for (String key : headers.keySet()) {
            sb.append(key + ": " + headers.get(key) + " \r\n");
        }

        if (httpBody == null) {
            return String.join("\r\n",
                    responseLine.getHttpVersion().getValue() + " " + responseLine.getHttpStatusCode().getValue() + " "
                            + responseLine.getReasonPhrase().getValue() + " ",
                    sb.toString(),
                    "");
        }
        return String.join("\r\n",
                responseLine.getHttpVersion().getValue() + " " + responseLine.getHttpStatusCode().getValue() + " "
                        + responseLine.getReasonPhrase().getValue() + " ",
                sb.toString(),
                // httpBody는 디코딩이 필요할수도? 일단 애매함
                httpBody.getValue(),
                "");
    }
}
