package org.apache.coyote.http11.response;

import org.apache.coyote.http11.request.Cookies;
import org.apache.coyote.http11.request.Location;

import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class OK implements Response {

    private static final String JAVA_SESSION_ID = "JSESSIONID";
    private static final String ROOT_RESPONSE = "Hello world!";
    private static final String HEADER_AND_BODY_SEPARATOR = "";
    private static final String LOGIN_HTML = "login.html";

    private final Location location;
    private final Cookies cookies;

    public OK(final Location location, final Cookies cookies) {
        this.location = location;
        this.cookies = cookies;
    }

    @Override
    public String get() throws IOException {
        final String body = makeResponseBody();

        final List<String> responses = new ArrayList<>();
        responses.add("HTTP/1.1 200 OK ");
        responses.add(location.contentTypeHeader());
        responses.add(contentLength(body));
        addSessionCookieIfLoginPage(responses);

        responses.add(HEADER_AND_BODY_SEPARATOR);
        responses.add(body);

        return String.join("\r\n", responses);
    }

    private String contentLength(final String body) {
        return "Content-Length: " + body.getBytes().length + " ";
    }

    private void addSessionCookieIfLoginPage(final List<String> responses) {
        if (location.is(LOGIN_HTML) && cookies.notExist(JAVA_SESSION_ID)) {
            responses.add(cookies.createNewJSessionIdHeader());
        }
    }

    private String makeResponseBody() throws IOException {
        if (location.isRoot()) {
            return ROOT_RESPONSE;
        }

        final List<String> fileContents = Files.readAllLines(location.getPath());
        return String.join("\n", fileContents) + "\n";
    }
}
