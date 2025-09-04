package org.apache.http;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpResponseMessage {

    private final OutputStream outputStream;
    private HttpVersion httpVersion;
    private StatusCode statusCode;
    private Map<String, String> header = new HashMap<>();
    private String body;

    public HttpResponseMessage(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public void setHttpVersion(HttpVersion httpVersion) {
        this.httpVersion = httpVersion;
    }

    public void setStatusCode(StatusCode statusCode) {
        this.statusCode = statusCode;
    }

    public void setHeader(String key, String value) {
        header.put(key, value);
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void writeMessage() {
        //TODO: Response에 넣을 초기값 세팅  (2025-09-4, 목, 18:9)
        String responseMessage = String.join(
                "\r\n",
                makeStartLine(),
                makeHeaderLines(),
                "",
                body
        );
        try {
            outputStream.write(responseMessage.getBytes());
            outputStream.flush();
        } catch (IOException e) {
            //TODO: 소켓 쓰기 오류 발생시 예외응답 처리  (2025-09-4, 목, 17:46)
        }
    }

    private String makeStartLine() {
        return String.join(" ",
                httpVersion.getValue(),
                String.valueOf(statusCode.getCode()),
                statusCode.getMessage());
    }

    private String makeHeaderLines() {
        List<String> headerLines = new ArrayList<>();
        List<String> keys = header.values().stream().toList();
        for (String key : keys) {
            String value = header.get(key);
            headerLines.add(key + ": " + value + " ");
        }
        headerLines.add("Content-Length: " + body.getBytes().length + " ");
        return String.join("\r\n", headerLines);
    }
}
