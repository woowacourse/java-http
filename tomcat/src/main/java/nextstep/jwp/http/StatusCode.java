package nextstep.jwp.http;

import java.net.URL;
import nextstep.jwp.utils.FileUtils;

public enum StatusCode {

    OK(200, "OK"),
    CREATED(201, "Created"),
    FOUND(302, "Found"),
    UNAUTHORIZED(401, "Unauthorized"),
    NOT_FOUND(404, "Not Found");

    private static final String STATUS_FORMAT = "%d %s";
    private final int statusCode;
    private final String reason;

    StatusCode(int statusCode, String reason) {
        this.statusCode = statusCode;
        this.reason = reason;
    }

    public static StatusCode matchStatusCode(String uri) {
        URL resource = FileUtils.getResource(uri);
        if (resource == null) {
            return StatusCode.NOT_FOUND;
        }
        return StatusCode.OK;
    }

    public String writeStatus() {
        return String.format(STATUS_FORMAT, this.statusCode, this.reason);
    }
}
