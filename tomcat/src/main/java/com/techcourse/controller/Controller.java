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
import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Controller {

    private static final Logger log = LoggerFactory.getLogger(Controller.class);

    private static final String DELIMITER = "\r\n";
    private static final String BASIC_RESPONSE_BODY = "Hello world!";

    private static boolean isRegisterRequest(HttpRequest request) {
        return request.getHttpMethod() == HttpMethod.POST
                && request.containsBody()
                && request.targetStartsWith("/register");
    }

    public HttpResponse service(HttpRequest request) {
        if (request.isTargetBlank()) {
            return HttpResponse.generate(request, HttpStatus.OK, "text/html", BASIC_RESPONSE_BODY);
        }
        if (request.isTargetStatic()) {
            return createStaticResponse(request, HttpStatus.OK, request.getPath(), request.getTargetExtension());
        }
        return createDynamicResponse(request);
    }

    private HttpResponse createDynamicResponse(HttpRequest request) {
        if (isLoginRequest(request)) {
            return createLoginResponse(request);
        }
        if (isRegisterRequest(request)) {
            return createRegisterResponse(request);
        }
        return null;
    }

    private boolean isLoginRequest(HttpRequest request) {
        return request.getHttpMethod() == HttpMethod.GET
                && request.containsQueryParameter()
                && request.targetStartsWith("/login");
    }

    public HttpResponse createLoginResponse(HttpRequest request) {
        Map<String, String> parameters = request.parseQueryString();
        Optional<User> nullableUser = InMemoryUserRepository.findByAccount(parameters.get("account"));
        return nullableUser
                .filter(user -> user.checkPassword(parameters.get("password")))
                .map(user -> redirectToIndex(request))
                .orElseGet(() -> redirectTo401(request));
    }

    private HttpResponse createRegisterResponse(HttpRequest request) {
        User user = new User(
                request.getFromBody("account"),
                request.getFromBody("password"),
                request.getFromBody("email"));
        InMemoryUserRepository.save(user);
        return redirectToIndex(request);
    }

    private HttpResponse redirectToIndex(HttpRequest request) {
        try {
            Path path = Path.of(getClass().getClassLoader().getResource("static/index.html").toURI());
            return createStaticResponse(request, HttpStatus.FOUND, path, "html");
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("uri syntax error: " + e.getMessage());
        }
    }

    private HttpResponse redirectTo401(HttpRequest request) {
        try {
            Path path = Path.of(getClass().getClassLoader().getResource("static/401.html").toURI());
            return createStaticResponse(request, HttpStatus.UNAUTHORIZED, path, "html");
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("uri syntax error: " + e.getMessage());
        }
    }

    public HttpResponse createStaticResponse(HttpRequest request, HttpStatus httpStatus,
                                             Path path, String targetExtension) {
        try {
            if (Files.exists(path)) {
                String contentType = getContentType(targetExtension);
                String responseBody = readFile(path);
                return HttpResponse.generate(request, httpStatus, contentType, responseBody);
            }
            throw new FileNotFoundException(path.toString());
        } catch (NullPointerException | IOException e) {
            log.error(e.getMessage());
            throw new IllegalArgumentException("invalid path: " + path.toString());
        }
    }

    private String getContentType(String targetExtension) {
        if (targetExtension.equals("ico") || targetExtension.equals("png") || targetExtension.equals("jpg")) {
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


}
