package org.apache.coyote;

import java.io.IOException;
import java.net.URL;
import java.net.http.HttpRequest;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;

public class RequestHandler {
    private static final Set<String> STATIC_RESOURCE_EXTENSIONS = Set.of("css", "js");
    private static final String STATIC_RESOURCE_ROOT_PATH = "static/";
    private static final String PATH_DELIMITER = "/";
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    // mapping handler method
    public String handle(final HttpRequest httpRequest) throws IOException {
        final String endPoint = httpRequest.uri().getPath();
        final String[] paths = endPoint.split(PATH_DELIMITER);

        if (paths.length == 0) {
            return handleRoot();
        }

        final String resourceName = paths[paths.length - 1];
        if (resourceName.contains(".")) {
            return handleSimpleResource(resourceName);
        }

        return handleURL(httpRequest);
    }

    private String handleRoot() {
        final String responseBody = "Hello world!";
        return getResponse("text/html", responseBody);
    }

    private String handleSimpleResource(final String resourceName) throws IOException {
        final URL resourceURL = getClass().getClassLoader().getResource(findResourcePath(resourceName));
        final Path resourcePath = Path.of(resourceURL.getPath());
        final String responseBody = Files.readString(resourcePath);
        final String mimeType = Files.probeContentType(resourcePath);

        return getResponse(mimeType, responseBody);
    }

    // TOOD: change naming
    private String handleURL(final HttpRequest httpRequest) throws IOException {
        String uri = httpRequest.uri().getPath();
        if (uri.contains("login")) {
            return handleLogin(httpRequest);
        }

        throw new IllegalCallerException("유효하지 않은 기능입니다.");
    }

    private String handleLogin(final HttpRequest httpRequest) throws IOException {
        String query = httpRequest.uri().getQuery();
        if (query == null) {
            return handleSimpleResource("login.html");
        }

        String[] params = query.split("&");
        String account = params[0].split("=")[1];
        String password = params[1].split("=")[1];

        Optional<User> userOptional = InMemoryUserRepository.findByAccount(account);
        if (userOptional.isEmpty()) {
            return handleSimpleResource("login.html");
        }

        User user = userOptional.get();
        if (user.checkPassword(password)) {
            log.info(user.toString());
            return handleSimpleResource("login.html");
        }

        return handleSimpleResource("401.html");
    }

    private String getResponse(String mimeType, String responseBody) {
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: " + mimeType + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }

    private String findResourcePath(final String resourcePath) {
        final String[] fileNames = resourcePath.split("\\.");
        final String extension = fileNames[1];

        if (STATIC_RESOURCE_EXTENSIONS.contains(extension)) {
            return STATIC_RESOURCE_ROOT_PATH.concat(extension).concat(PATH_DELIMITER).concat(resourcePath);
        }

        return STATIC_RESOURCE_ROOT_PATH.concat(resourcePath);
    }
}
