package org.apache.http;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.exception.SocketWriteException;

public class HttpResponseMessage {

    private final OutputStream outputStream;
    private HttpVersion httpVersion;
    private StatusCode statusCode;
    private Map<String, String> header = new HashMap<>();
    private String body;

    public HttpResponseMessage(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public void writeMessage() {
        String responseMessage = String.join(
                "\r\n",
                makeStartLine(),
                makeHeaderLines(),
                "",
                body
        );
        try {
            outputStream.write(responseMessage.getBytes(StandardCharsets.UTF_8));
            outputStream.flush();
        } catch (IOException e) {
            throw new SocketWriteException("소켓에 데이터를 쓰는중 오류가 발생했습니다.");
        }
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

    public HttpVersion getHttpVersion() {
        return httpVersion;
    }

    public StatusCode getStatusCode() {
        return statusCode;
    }

    public Map<String, String> getHeader() {
        return header;
    }

    public String getBody() {
        return body;
    }

    private String makeStartLine() {
        return String.join(" ",
                httpVersion.getValue(),
                String.valueOf(statusCode.getCode()),
                statusCode.getMessage(),
                "");
    }

    private String makeHeaderLines() {
        List<String> headerLines = new ArrayList<>();
        List<String> keys = header.keySet().stream().toList();
        for (String key : keys) {
            String value = header.get(key);
            headerLines.add(key + ": " + value + " ");
        }
        headerLines.add("Content-Length: " + body.getBytes().length + " ");
        return String.join("\r\n", headerLines);
    }
}
