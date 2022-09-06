package org.apache.coyote.http11.request;

import nextstep.jwp.exception.UncheckedServletException;

public class StartLine {

    private final HttpMethod httpMethod;
    private final Path path;
    private final String httpVersion;

    private StartLine(HttpMethod httpMethod, Path path, String httpVersion) {
        this.httpMethod = httpMethod;
        this.path = path;
        this.httpVersion = httpVersion;
    }

    public static StartLine from(String requestLine){
        if (requestLine == null || requestLine.isEmpty()) {
            throw new UncheckedServletException("유효하지 않은 요청입니다.");
        }
        final String[] split = requestLine.split(" ");
        return new StartLine(HttpMethod.from(split[0]), Path.from(split[1]), split[2]);
    }

    public Path getPath() {
        return path;
    }

    public boolean isGet() {
        return HttpMethod.GET == httpMethod;
    }
}
