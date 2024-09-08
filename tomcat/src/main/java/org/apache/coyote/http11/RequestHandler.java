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

    public String generateResponseBody() throws IOException {
        if (Objects.equals(requestUri, "/")) {
            return "Hello world!";
        }
        if (requestUri.startsWith("/login?")) {
            return login();
        }
        final URL resource = getClass().getClassLoader().getResource("static" + requestUri);
        return new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
    }

    public String login() throws IOException {
        int index = requestUri.indexOf("?");
        String path = requestUri.substring(0, index);

        Optional<Map<String, String>> parsed = parseQueryString(requestUri, index);
        Map<String, String> queryPairs = parsed.orElseThrow(() -> new NoSuchElementException("invalid query string"));

        User account = InMemoryUserRepository.findByAccount(queryPairs.get("account"))
                .orElseThrow(() -> new NoSuchElementException("account not found"));

        if (account.checkPassword(queryPairs.get("password"))) {
            log.info("user : {}", account);
        }
        final URL resource = getClass().getClassLoader().getResource("static" + path + ".html");
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
            String[] pair = queryParameter.split("=");
            keyValue.put(pair[0], pair[1]);
        }
        return Optional.of(keyValue);
    }
}
