package nextstep.jwp.controller;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.http11.request.HttpRequest;

import nextstep.jwp.model.User;

public class HttpRequestGenerator {

    public static HttpRequest generate(String method, String uri) throws IOException {
        String httpRequest = String.join("\r\n",
            method + " " + uri + " HTTP/1.1 ",
            "Host: localhost:8080 ",
            "Connection: keep-alive ",
            "",
            "");

        return stringToHttpRequest(httpRequest);
    }

    public static HttpRequest generateWithCookie(String method, String uri) throws IOException {
        Session session = new Session();
        session.setAttribute("user", new User("account", "password", "email"));

        SessionManager sessionManager = new SessionManager();
        sessionManager.add(session);

        String httpRequest = String.join("\r\n",
            method + " " + uri + " HTTP/1.1 ",
            "Host: localhost:8080 ",
            "Connection: keep-alive ",
            "Cookie: JSESSIONID=" + session.getId(),
            "",
            "");

        return stringToHttpRequest(httpRequest);
    }

    public static HttpRequest generateWithBody(String method, String uri, String body) throws IOException {
        String httpRequest = String.join("\r\n",
            method + " " + uri + " HTTP/1.1 ",
            "Host: localhost:8080 ",
            "Connection: keep-alive ",
            "Content-Length: " + body.length(),
            "",
            body);

        return stringToHttpRequest(httpRequest);
    }

    private static HttpRequest stringToHttpRequest(String httpRequest) throws IOException {
        try (
            InputStream inputStream = new ByteArrayInputStream(httpRequest.getBytes());
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        ) {
            return HttpRequest.from(bufferedReader);
        }

    }
}
