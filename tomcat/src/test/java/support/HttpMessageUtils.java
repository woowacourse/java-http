package support;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import org.apache.catalina.session.Session;
import org.apache.coyote.request.HttpRequest;

public class HttpMessageUtils {

    public static String extractSetCookieSessionId(String socketOutput) {
        final var right = socketOutput.split("Set-Cookie: JSESSIONID=")[1];
        return right.split("; Max-Age=600")[0];
    }

    public static HttpRequest toRequest(String httpRequest) throws IOException {
        try (final var inputStream = new ByteArrayInputStream(httpRequest.getBytes());
             final var reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
             final var bufferedReader = new BufferedReader(reader)) {
            final var request = HttpRequest.of(bufferedReader);
            updateSession(request);
            return request;
        }
    }

    private static void updateSession(HttpRequest request) {
        final var sessionCookie = request.findCookie(Session.JSESSIONID);
        if (sessionCookie != null) {
            request.setSession(new Session(sessionCookie.getValue()));
            return;
        }
        final var session = Session.of();
        request.setSession(session);
    }

    public static String getResponseBody(String resourcePath) throws IOException {
        final var resource = HttpMessageUtils.class.getClassLoader().getResource("static/" + resourcePath);
        return new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
    }
}
