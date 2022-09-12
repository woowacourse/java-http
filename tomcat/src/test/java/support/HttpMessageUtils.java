package support;

public class HttpMessageUtils {

    public static String extractSetCookieSessionId(String socketOutput) {
        final var right = socketOutput.split("Set-Cookie: JSESSIONID=")[1];
        return right.split("; Max-Age=600")[0];
    }
}
