package org.apache.coyote.http11;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

public class HttpResponse {
    // ResponseLine: Protocol Version, Status Code, Status Message(반드시 Status Code와 일치해야 하는 건 아니다)
    // HTTP 버전에 따라 분기하는 방법을 고민했으나.. 너무 어려워 보여서 일단 1.x 버전만 구현하기로 했어요.
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

    public void setResponseLine(HttpVersion http11, HttpStatusCode httpStatus200, ReasonPhrase found) {
        this.responseLine = new ResponseLine(http11, httpStatus200, found);
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

    public void setHttpBody(HttpBody httpBody) {
        this.httpBody = httpBody;
    }

    public void putHeader(String key, String value) {
        httpHeaders.put(key, value);
    }

    public void write() throws IOException {
        outputStream.write(buildToResponse().getBytes());
        outputStream.flush();
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
