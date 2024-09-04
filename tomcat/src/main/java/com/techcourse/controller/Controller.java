package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.StringJoiner;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Controller {

    private static final Logger log = LoggerFactory.getLogger(Controller.class);

    private static final String DELIMITER = "\r\n";
    private static final String BASIC_RESPONSE_BODY = "Hello world!";

    public HttpResponse service(HttpRequest request) {
        if (request.pathStartsWith("/login")) {
            return createLoginResponse(request);
        }
        return createStaticResponse(request.getPath(), request.getTargetExtension());
    }

    public HttpResponse createLoginResponse(HttpRequest request) {
        Map<String, String> parameters = request.parseQueryString();
        Optional<User> nullableUser = InMemoryUserRepository.findByAccount(parameters.get("account"));
        nullableUser.ifPresent(user -> logUserInfo(user, parameters.get("password")));
        try {
            Path path = Path.of(getClass().getClassLoader().getResource("static/login.html").toURI());
            return createStaticResponse(path, "html");
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("");
        }
    }

    private static void logUserInfo(User user, String password) {
        if (user.checkPassword(password)) {
            log.info("user: {}", user);
        }
    }

    public HttpResponse createStaticResponse(Path path, String targetExtension) {
        try {
            if (Files.exists(path)) {
                String contentType = getContentType(targetExtension);
                String responseBody = readFile(path);
                return getResponse(responseBody, contentType);
            }
            throw new FileNotFoundException(path.toString());
        } catch (NullPointerException | IOException e) {
            log.error(e.getMessage());
            return getResponse(BASIC_RESPONSE_BODY, "text/html");
        }
    }

    private String getContentType(String targetExtension) {
        if(targetExtension.equals("ico") || targetExtension.equals("png") || targetExtension.equals("jpg")) {
            return "image/" + targetExtension;
        }
        return "text/" + targetExtension;
    }

    public String readFile(Path path) throws IOException {
        List<String> fileLines = Files.readAllLines(path);

        StringJoiner joiner = new StringJoiner(DELIMITER);
        for (String fileLine : fileLines) {
            joiner.add(fileLine);
        }
        joiner.add("");
        return joiner.toString();
    }

    private HttpResponse getResponse(String responseBody, String contentType) {
        return new HttpResponse(String.join(DELIMITER,
                "HTTP/1.1 200 OK ",
                "Content-Type: " + contentType + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody));
    }
}
