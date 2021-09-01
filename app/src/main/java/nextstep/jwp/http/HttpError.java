package nextstep.jwp.http;

import java.io.IOException;

public enum HttpError {

    UNAUTHORIZED("Unauthorized", 401, "/401.html"),
    FORBIDDEN("Forbidden", 403, "/403.html"),
    NOT_FOUND("Not Found", 404, "/404.html"),
    METHOD_NOT_ALLOWED("Method Not Allowed", 405, "/405.html");

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
