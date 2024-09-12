package org.apache.coyote.http11.request.requestLine;

public class Path {

    public static final String ROOT_PATH= "/";
    private final String DEFAULT_PATH = "/index.html";

    private final String value;

    public Path(String value) {
        if (value.replaceAll(" ", "").equals(ROOT_PATH)) {
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
