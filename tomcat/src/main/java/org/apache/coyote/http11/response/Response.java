package org.apache.coyote.http11.response;

import nextstep.jwp.model.User;
import nextstep.jwp.service.UserService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;
import static org.apache.coyote.http11.response.Method.POST;

public class Response {

    private static final int LOCATION_INDEX = 1;
    private static final String HTTP_REQUEST_DELIMITER = " ";
    private static final String ROOT_RESPONSE = "Hello world!";
    private static final String COOKIE_KEY = "Cookie";

    private final Location location;
    private final Method method;
    private final Parameters parameters;
    private final Cookies cookies;

    public Response(final Location location, final Method method, final Parameters parameters, final Cookies cookies) {
        this.location = location;
        this.method = method;
        this.parameters = parameters;
        this.cookies = cookies;
    }

    public static Response from(final InputStream inputStream) throws IOException {
        final BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));

        final List<String> headers = readHeaders(br);
        final String locationHeader = headers.get(0).split(HTTP_REQUEST_DELIMITER)[LOCATION_INDEX];

        final Method method = Method.from(headers.get(0));
        final Location location = Location.from(locationHeader);
        final Cookies cookies = Cookies.from(findHeader(headers, COOKIE_KEY));

        if (POST.equals(method)) {
            final char[] body = readBody(br, headers);
            return new Response(location, method, Parameters.from(body), cookies);
        }

        final Parameters parameters = Parameters.from(locationHeader);
        return new Response(location, method, parameters, cookies);
    }

    private static List<String> readHeaders(final BufferedReader br) throws IOException {
        final List<String> headers = new ArrayList<>();
        while (!headers.contains("")) {
            headers.add(br.readLine());
        }

        return headers;
    }

    private static char[] readBody(final BufferedReader br, final List<String> headers) throws IOException {
        final int contentLength = getContentLength(headers);

        final char[] buffer = new char[contentLength];
        br.read(buffer, 0, contentLength);

        return buffer;
    }

    private static String findHeader(final List<String> headers, final String key) {
        return headers.stream()
                .filter(header -> header.contains(key))
                .findAny()
                .orElse(null);
    }

    private static int getContentLength(final List<String> headers) {
        final String contentLengthKey = "Content-Length: ";

        final String contentLength = findHeader(headers, contentLengthKey);
        if (isNull(contentLength)) {
            return 0;
        }

        return Integer.parseInt(contentLength.replace(contentLengthKey, ""));
    }

    public String handle() throws IOException {
        if (method.equals(POST)) {
            return post();
        }

        return get();
    }

    private String post() {
        if (location.is("register.html")) {
            UserService.register(parameters);
            return makeResponseForRedirect("/index");
        }

        if (location.is("login.html")) {
            try {
                final User user = UserService.login(parameters);
                UserSession.login(cookies.getCookieValue(), user);
                return makeResponseForRedirect("/index");
            } catch (Exception e) {
                return makeResponseForRedirect("/401");
            }
        }

        throw new IllegalArgumentException("지원하지 않는 메서드입니다.");
    }

    private String get() throws IOException {
        if (location.is("login.html") && UserSession.exist(cookies.getCookieValue())) {
            return makeResponseForRedirect("/index");
        }

        return makeResponseForOk();
    }

    private String makeResponseForRedirect(final String location) {
        final List<String> headers = new ArrayList<>();
        headers.add("HTTP/1.1 302 Found ");
        headers.add("Location: " + location);
        headers.add("");

        return String.join("\r\n", headers);
    }

    private String makeResponseForOk() throws IOException {
        final String body = makeResponseBody();

        final List<String> responses = new ArrayList<>();
        responses.add("HTTP/1.1 200 OK ");
        responses.add(location.getContentTypeHeader());
        responses.add("Content-Length: " + body.getBytes().length + " ");
        if (location.is("login.html") && cookies.notExist()) {
            responses.add(cookies.createNewCookieHeader());
        }

        responses.add("");
        responses.add(body);

        return String.join("\r\n", responses);
    }

    private String makeResponseBody() throws IOException {
        if (location.isRoot()) {
            return ROOT_RESPONSE;
        }

        return String.join("\n", Files.readAllLines(location.getPath())) + "\n";
    }
}
