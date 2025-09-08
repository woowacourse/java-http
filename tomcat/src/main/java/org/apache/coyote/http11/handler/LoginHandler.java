package org.apache.coyote.http11.handler;

import com.techcourse.db.InMemoryUserRepository;
import org.apache.coyote.http11.MimeType;
import org.apache.coyote.http11.Resource;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LoginHandler extends HttpRequestHandler {

    @Override
    String getSupportedUrl() {
        return "/login";
    }

    @Override
    protected String handleGet(String request) {
        service(request);
        Resource responseBody = getResource("/login.html");
        MimeType mimeType = MimeType.fromResource(responseBody);
        return String.join(
                "\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: " + mimeType.getMimeType(),
                "Content-Length: " + responseBody.content().getBytes(StandardCharsets.UTF_8).length,
                "",
                responseBody.content()
        );
    }

    private void service(String request) {
        Map<String, String> parameterMap = getParameters(request);
        String account = parameterMap.get("account");
        String password = parameterMap.get("password");
        if (account == null | password == null) {
            return;
        }
        InMemoryUserRepository.findByAccountAndPassword(account, password)
                .ifPresentOrElse(
                        user -> System.out.println("user = " + user),
                        () -> System.out.println("User account " + account + " not found.")
                );
    }

    private Map<String, String> getParameters(String request) {
        String url = request.split(System.lineSeparator())[0].split(" ")[1];

        int questionMarkIndex = url.indexOf('?');
        if (questionMarkIndex == -1) {
            return Map.of();
        }

        String queryString = url.substring(questionMarkIndex + 1);
        return Stream.of(queryString.split("&"))
                .map(pair -> pair.split("=", 2))
                .filter(keyValue -> keyValue.length == 2)
                .collect(Collectors.toMap(
                        keyValue -> keyValue[0],
                        keyValue -> keyValue[1],
                        (oldValue, newValue) -> oldValue,
                        HashMap::new
                ));
    }

    private Resource getResource(String path) {
        try {
            return Resource.fromPath("static" + path);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
