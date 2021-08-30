package nextstep.jwp.controller;

import java.io.IOException;
import nextstep.jwp.FileReader;

public enum HttpError {

    UNAUTHORIZED("Unauthorized", 401, "/401.html"),
    FORBIDDEN("Forbidden", 403, "/403.html"),
    NOTFOUND("Not Found", 404, "/404.html");

    private final String name;
    private final int code;
    private final String resource;

    HttpError(String name, int code, String resource) {
        this.name = name;
        this.code = code;
        this.resource = resource;
    }

    public String getName() {
        return name;
    }

    public int getCode() {
        return code;
    }

    public String getResource() throws IOException {
        return FileReader.file(resource);
    }
}
