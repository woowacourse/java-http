package nextstep.jwp.http;

import nextstep.jwp.http.exception.HttpVersionException;

public class HttpVersion {
    public static final String HTTP_VERSION_1_1 = "HTTP/1.1";

    private HttpVersion() {
    }

    public static void checkHttpVersion(String version) {
        if (!HTTP_VERSION_1_1.equals(version)) {
            throw new HttpVersionException();
        }
    }
}
