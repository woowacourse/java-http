package nextstep.jwp.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.NoSuchElementException;
import java.util.UUID;
import nextstep.jwp.MessageConverter;
import nextstep.jwp.model.User;
import nextstep.jwp.service.LoginRequest;
import nextstep.jwp.service.RegisterRequest;
import nextstep.jwp.service.UserService;
import org.apache.coyote.http11.model.ContentType;
import org.apache.coyote.http11.model.Header;
import org.apache.coyote.http11.model.Session;
import org.apache.coyote.http11.model.Sessions;
import org.apache.coyote.http11.model.request.HttpRequest;
import org.apache.coyote.http11.model.request.Method;
import org.apache.coyote.http11.model.response.HttpResponse;
import org.apache.coyote.http11.model.response.Resource;
import org.apache.coyote.http11.model.response.Status;

public class UserController {

    private static final UserService userService = new UserService();
    public static final String SESSION_ID = "JSESSIONID";

    public static HttpResponse login(final HttpRequest request) throws IOException {
        if (request.getMethod() == Method.POST) {
            return postLogin(request);
        }
        return getLogin(request);
    }

    private static HttpResponse getLogin(final HttpRequest request) throws IOException {
        if (loginAlready(request)) {
            String sessionId = request.getCookie().getValue(SESSION_ID);
            if (Sessions.find(sessionId).isPresent()) {
                HttpResponse response = HttpResponse.of(Status.FOUND);
                response.addHeader(Header.LOCATION, "/index.html");
                return response;
            }
        }
        HttpResponse response = HttpResponse.of(Status.OK);
        response.addResource(findResource("/login.html"));
        return response;
    }

    private static boolean loginAlready(final HttpRequest request) {
        return request.getCookie().hasKey(SESSION_ID);
    }

    private static HttpResponse postLogin(final HttpRequest request) throws IOException {
        try {
            LoginRequest loginRequest = LoginRequest.of(MessageConverter.convert(request.getBody()));
            User user = userService.login(loginRequest);
            HttpResponse response = HttpResponse.of(Status.FOUND);
            response.addHeader(Header.LOCATION, "/index.html");

            UUID uuid = UUID.randomUUID();
            response.addHeader(Header.SET_COOKIE, SESSION_ID + "=" + uuid);
            Sessions.addNew(uuid.toString(), new Session("user", user));

            return response;
        } catch (IllegalArgumentException | NoSuchElementException e) {
            HttpResponse response = HttpResponse.of(Status.UNAUTHORIZED);
            response.addResource(findResource("/401.html"));
            return response;
        }
    }

    public static HttpResponse register(final HttpRequest request) throws IOException {
        if (request.getMethod() == Method.POST) {
            return postRegister(request);
        }
        return getRegister();
    }

    private static HttpResponse getRegister() throws IOException {
        HttpResponse response = HttpResponse.of(Status.OK);
        response.addResource(findResource("/register.html"));
        return response;
    }

    private static HttpResponse postRegister(final HttpRequest request) {
        RegisterRequest registerRequest = RegisterRequest.of(MessageConverter.convert(request.getBody()));
        userService.register(registerRequest);
        HttpResponse response = HttpResponse.of(Status.FOUND);
        response.addHeader(Header.LOCATION, "/index.html");
        return response;
    }

    private static Resource findResource(String url) throws IOException {
        Path path = Path.of(MainController.class.getResource("/static" + url).getPath());
        String body = Files.readString(path);

        ContentType contentType = ContentType.findByExtension(url);

        return new Resource(body, contentType);
    }
}
