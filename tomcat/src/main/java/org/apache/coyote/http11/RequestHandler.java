package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestHandler {

    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private final String requestUri;

    public RequestHandler(String requestUri) {
        this.requestUri = requestUri;
    }

    public String getResponse() throws IOException {
        if (Objects.equals(requestUri, "/")) {
            return generate200Response("/", "Hello world!");
        }
        if (requestUri.startsWith("/login")) {
            return generateLoginResponse();
        }
        String responseBody = generateResponseBody("static" + requestUri);
        return generate200Response(requestUri, responseBody);
    }

    private String generateLoginResponse() throws IOException {
        int index = requestUri.indexOf("?");
        if (index == -1) {
            String responseBody = generateResponseBody("static" + requestUri);
            return generate200Response(requestUri, responseBody);
        }

        if (login(index)) {
            return generate302Response("/index.html");
        }
        return generate302Response("/401.html");
    }

    private boolean login(int index) {
        Optional<Map<String, String>> parsed = parseQueryString(requestUri, index);
        Map<String, String> queryPairs = parsed.orElseThrow(() -> new NoSuchElementException("invalid query string"));

        Optional<User> account = InMemoryUserRepository.findByAccount(queryPairs.get("account"));
        if (account.isPresent() && account.get().checkPassword(queryPairs.get("password"))) {
            log.info("user : {}", account);
            return true;
        }
        return false;
    }

    private String generateResponseBody(String path) throws IOException {
        if (!path.contains(".")) {
            final URL resource = getClass().getClassLoader().getResource(path + ".html");
            return new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        }
        final URL resource = getClass().getClassLoader().getResource(path);
        return new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
    }

    private Optional<Map<String, String>> parseQueryString(String requestUri, int index) {
        String queryString = requestUri.substring(index + 1);
        String[] queryParameters = queryString.split("&");

        Map<String, String> keyValue = new HashMap<>();
        for (String queryParameter : queryParameters) {
            if (!queryParameter.contains("=")) {
                return Optional.empty();
            }
            String[] pair = queryParameter.split("=", -1);
            keyValue.put(pair[0], pair[1]);
        }
        return Optional.of(keyValue);
    }

    private String generate200Response(String requestUri, String responseBody) {
        var response = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
        if (requestUri.startsWith("/css")) {
            response = response.replace("text/html", "text/css");
        }
        return response;
    }

    private String generate302Response(String location) {
        var response = String.join("\r\n",
                "HTTP/1.1 302 FOUND ",
                "Location: " + location,
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + 0 + " "
        );
        return response;
    }
}
