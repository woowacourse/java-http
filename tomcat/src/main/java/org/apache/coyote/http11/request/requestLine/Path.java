package org.apache.coyote.http11.request.requestLine;

public class Path {

    private final String DEFAULT_PATH = "/index.html";

    private final String value;

    public Path(String value) {
        if (value.replaceAll(" ", "").equals("/")) {
            value = DEFAULT_PATH;
        }
        if (!value.startsWith("/")) {
            throw new IllegalArgumentException("잘못된 HTTP/1.1 요청 형식입니다.");
        }
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
