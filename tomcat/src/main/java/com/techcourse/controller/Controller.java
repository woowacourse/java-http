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
import org.apache.coyote.http11.HttpHeader;
import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatusCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Controller {

    private static final Logger log = LoggerFactory.getLogger(Controller.class);

    private static final String DELIMITER = "\r\n";
    private static final String BASIC_RESPONSE_BODY = "Hello world!";

    public HttpResponse service(HttpRequest request) {
        if(request.isTargetBlank()) {
            return getResponse(HttpStatusCode.OK, "text/html", BASIC_RESPONSE_BODY);
        }
        if(request.isTargetStatic()) {
            return createStaticResponse(HttpStatusCode.OK, request.getPath(), request.getTargetExtension());
        }
        return createDynamicResponse(request);
    }

    private HttpResponse createDynamicResponse(HttpRequest request) {
        if(isLoginRequest(request)) {
            return createLoginResponse(request);
        }
        if(isRegisterRequest(request)) {
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
                .map(user -> redirectToIndex())
                .orElseGet(this::redirectTo401);

    }

    private static boolean isRegisterRequest(HttpRequest request) {
        return request.getHttpMethod() == HttpMethod.POST
                && request.containsBody()
                && request.targetStartsWith("/register");
    }

    private HttpResponse createRegisterResponse(HttpRequest request) {
        User user = new User(
                request.getFromBody("account"),
                request.getFromBody("password"),
                request.getFromBody("email"));
        InMemoryUserRepository.save(user);
        return redirectToIndex();
    }

    private HttpResponse redirectToIndex() {
        try {
            Path path = Path.of(getClass().getClassLoader().getResource("static/index.html").toURI());
            return createStaticResponse(HttpStatusCode.FOUND, path, "html");
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("uri syntax error: " + e.getMessage());
        }
    }

    private HttpResponse redirectTo401() {
        try {
            Path path = Path.of(getClass().getClassLoader().getResource("static/401.html").toURI());
            return createStaticResponse(HttpStatusCode.UNAUTHORIZED, path, "html");
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("uri syntax error: " + e.getMessage());
        }
    }

    public HttpResponse createStaticResponse(HttpStatusCode httpStatusCode, Path path, String targetExtension) {
        try {
            if (Files.exists(path)) {
                String contentType = getContentType(targetExtension);
                String responseBody = readFile(path);
                return getResponse(httpStatusCode, contentType, responseBody);
            }
            throw new FileNotFoundException(path.toString());
        } catch (NullPointerException | IOException e) {
            log.error(e.getMessage());
            return getResponse(HttpStatusCode.OK, "text/html", BASIC_RESPONSE_BODY);
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

    private HttpResponse getResponse(HttpStatusCode httpStatusCode, String contentType, String responseBody) {
        return new HttpResponse(String.join(DELIMITER,
                "HTTP/1.1 " + httpStatusCode.getValue() + " ",
                HttpHeader.CONTENT_TYPE.getValue() + ": " + contentType + ";charset=utf-8 ",
                HttpHeader.CONTENT_LENGTH.getValue() + ": " + responseBody.getBytes().length + " ",
                "",
                responseBody));
    }
}
