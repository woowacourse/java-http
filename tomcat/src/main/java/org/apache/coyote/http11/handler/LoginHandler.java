package org.apache.coyote.http11.handler;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.coyote.HttpStatus;
import org.apache.coyote.http11.MimeType;
import org.apache.coyote.http11.Resource;
import org.apache.coyote.http11.response.HttpResponse;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LoginHandler extends HttpRequestHandler {

    @Override
    String getSupportedUrl() {
        return "/login";
    }

    @Override
    protected HttpResponse handleGet(String request) {
        HttpResponse httpResponse = service(request);
        return httpResponse;
    }

    private HttpResponse service(String request) {
        Map<String, String> parameterMap = getParameters(request);
        String account = parameterMap.get("account");
        String password = parameterMap.get("password");
        if (account == null | password == null) {
            Resource responseBody = getResource("/login.html");
            MimeType mimeType = MimeType.fromResource(responseBody);
            return new HttpResponse(HttpStatus.OK, responseBody.content(), mimeType, Map.of());
        }

        Optional<User> user = InMemoryUserRepository.findByAccountAndPassword(account, password);
        if (user.isPresent()) {
            return new HttpResponse(HttpStatus.FOUND, "", MimeType.ANY, Map.of("Location", "/index.html"));
        }

        Resource responseBody = getResource("/401.html");
        MimeType mimeType = MimeType.fromResource(responseBody);
        return new HttpResponse(HttpStatus.OK, responseBody.content(), mimeType, Map.of());
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
