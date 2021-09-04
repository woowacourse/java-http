package nextstep.jwp.http;

public class HttpVersion {
    public static final String HTTP_VERSION_1_1 = "HTTP/1.1";

    public static void checkHttpVersion(String version) {
        if (!HTTP_VERSION_1_1.equals(version)) {
            throw new RuntimeException();
        }
    }
}
