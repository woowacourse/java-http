package support;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.StringJoiner;

import org.apache.coyote.http11.request.HttpRequest;

public class TestRequest {

    public static HttpRequest generateWithUri(final String uri) {
        return generateWithSession("GET", uri, "HTTP/1.1", "");
    }

    public static HttpRequest generateWithUriAndUserInfo(final String uri, final String account,
        final String password, final String email) {
        return generate("GET", uri, "HTTP/1.1", "", account, password, email);
    }

    public static HttpRequest generateWithRequestLine(final String method, final String uri,
        final String version) {
        return generateWithSession(method, uri, version, "");
    }

    public static HttpRequest generateWithSession(final String method, final String uri, final String version,
        final String session) {
        return generate(method, uri, version, session, "", "", "");
    }

    public static HttpRequest generate(final String method, final String uri, final String version,
        final String session, final String account, final String password, final String email) {
        final StringJoiner joiner = new StringJoiner(" ", "", " ");
        final String messageBody = "account=" + account + "&password=" + password + "&email=" + email;
        String requestLine = joiner.add(method)
            .add(uri)
            .add(version).toString();
        final String cookie = "Cookie: " + session;
        final String httpRequest = String.join("\r\n",
            requestLine,
            "Host: localhost:8080 ",
            "Connection: keep-alive ",
            "Content-Length: " + messageBody.length(),
            "Content-Type: application/x-www-form-urlencoded",
            cookie,
            "",
            messageBody);
        final InputStream inputStream = new ByteArrayInputStream(httpRequest.getBytes());

        try {
            return new HttpRequest(inputStream);
        } catch (IOException | URISyntaxException e) {
            throw new IllegalArgumentException("Invalid byte requested");
        }
    }
}
