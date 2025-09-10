package org.apache.catalina.container.http.request;

import java.util.List;
import org.apache.catalina.container.exception.InvalidRequestException;
import org.apache.catalina.container.http.value.HttpMethod;
import org.apache.catalina.container.http.value.HttpVersion;

public class RequestStartLine {

    private final HttpMethod method;
    private final String uri;
    private final HttpVersion version;

    public RequestStartLine(String startLine) {
        validateEmptyStartLine(startLine);
        validateStartLineFormat(startLine);

        List<String> startLinePart = List.of(startLine.split("\\s+"));
        this.method = HttpMethod.valueOf(startLinePart.get(0));
        this.uri = parseUri(startLinePart.get(1));
        this.version = HttpVersion.parse(startLinePart.get(2));
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getUri() {
        return uri;
    }

    public HttpVersion getVersion() {
        return version;
    }

    private void validateEmptyStartLine(String startLine) {
        if (startLine == null || startLine.isEmpty()) {
            throw new InvalidRequestException("요청 메세지의 시작라인 형식이 올바르지 않습니다.");
        }
    }

    private void validateStartLineFormat(String startLine) {
        List<String> startLinePart = List.of(startLine.split(" "));
        if (startLinePart.size() < 3) {
            throw new InvalidRequestException("요청 메세지의 시작라인 형식이 올바르지 않습니다.");
        }
    }

    private String parseUri(String uriLine) {
        List<String> startLinePart = List.of(uriLine.split("\\?"));
        return startLinePart.getFirst();
    }
}
