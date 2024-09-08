package org.apache.coyote.http11.request.domain;

public class RequestPath {

    private static final String DEFAULT_RESOURCE_PATH = "static";

    private final String path;

    public RequestPath(String path) {
        if (path == null) {
            throw new NullPointerException("경로가 존재하지 않습니다.");
        }
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public String getQueryString() {
        int index = path.indexOf("?");

        return path.substring(index + 1);
    }

    public boolean isDefaultPath() {
        return "/".equals(path);
    }

    public String findExtension() {
        if (!path.contains(".")) {
            throw new IllegalArgumentException("확장자를 찾을 수 없습니다.");
        }
        return path.split("\\.")[1];
    }

    public String toResourcePath() {
        return DEFAULT_RESOURCE_PATH + path;
    }
}
