package org.apache.coyote.http11;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

public class HttpResponse {
    private static final String CONTENT_LENGTH = "Content-Length";

    private final OutputStream outputStream;
    private ResponseLine responseLine;
    private HttpHeaders httpHeaders = new HttpHeaders();
    private HttpBody httpBody = new HttpBody("");

    public HttpResponse(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public void write() throws IOException {
        setContentLength();
        outputStream.write(buildToResponse().getBytes());
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

    private String buildToResponse() {
        if (httpBody == null) {
            return String.join("\r\n",
                    responseLine.getHttpVersion().getValue() + " " + responseLine.getHttpStatusCode().getValue() + " "
                            + responseLine.getReasonPhrase().getValue() + " ",
                    buildHeaderToResponse(),
                    "");
        }
        return String.join("\r\n",
                responseLine.getHttpVersion().getValue() + " " + responseLine.getHttpStatusCode().getValue() + " "
                        + responseLine.getReasonPhrase().getValue() + " ",
                buildHeaderToResponse(),
                httpBody.getValue(),
                "");
    }

    private String buildHeaderToResponse() {
        Map<String, String> headers = httpHeaders.getHeaders();
        StringBuilder sb = new StringBuilder();
        for (String key : headers.keySet()) {
            sb.append(key + ": " + headers.get(key) + " \r\n");
        }
        return sb.toString();
    }
}
