package org.apache.catalina.handler;

import static org.apache.catalina.StaticResourceUtils.getContentType;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import org.apache.catalina.StaticResourceUtils;
import org.apache.coyote.util.HttpRequest;
import org.apache.coyote.util.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginHandler implements HttpHandler {

    private static final Logger log = LoggerFactory.getLogger(LoginHandler.class);

    private final Map<String, BiConsumer<HttpRequest, HttpResponse>> PATH_HANDLER = Map.of(
            "POST/login", this::login,
            "GET/login", this::getLoginPage
    );
    private final List<String> PATHS = List.of("/login");

    @Override
    public void handle(final HttpRequest request, final HttpResponse response) {
        BiConsumer<HttpRequest, HttpResponse> handler = PATH_HANDLER.get(request.extractMethodPath());
        if (handler != null) {
            handler.accept(request, response);
        }
    }

    @Override
    public List<String> getAllPath() {
        return new ArrayList<>(PATHS);
    }

    private void getLoginPage(HttpRequest httpRequest, HttpResponse httpResponse) {
        String httpVersion = httpRequest.getHttpVersion();
        String responseBody = StaticResourceUtils.readResourceContent(httpRequest.getRequestPath());
        int statusCode = 200;
        String statusMessage = "OK";
        httpResponse.putHeader("Content-Type", StaticResourceUtils.getContentType(httpRequest.getRequestPath()) + ";charset=utf-8");
        httpResponse.putHeader("Content-Length", String.valueOf(responseBody.getBytes(StandardCharsets.UTF_8).length));
        httpResponse.update(httpVersion, statusCode, statusMessage, responseBody);
    }

    private void login(HttpRequest httpRequest, HttpResponse httpResponse) {
        String httpVersion = httpRequest.getHttpVersion();
        int statusCode = 401;
        String statusMessage = "Unauthorized";
        String responseBody = StaticResourceUtils.readResourceContent("/401.html");
        Optional<User> user = InMemoryUserRepository.findByAccount(httpRequest.getRequestValue("account"));
        if (user.isPresent() && user.get().checkPassword(httpRequest.getRequestValue("password"))) {
            log.info(user.toString());
            statusCode = 302;
            statusMessage = "Found";
            responseBody = StaticResourceUtils.readResourceContent("/index.html");
        }
        httpResponse.putHeader("Content-Type", getContentType(httpRequest.getRequestPath()) + ";charset=utf-8");
        httpResponse.putHeader("Content-Length", String.valueOf(responseBody.getBytes(StandardCharsets.UTF_8).length));
        httpResponse.update(httpVersion, statusCode, statusMessage, responseBody);
    }
}
