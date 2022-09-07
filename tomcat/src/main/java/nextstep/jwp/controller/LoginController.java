package nextstep.jwp.controller;

import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.UUID;
import nextstep.jwp.model.User;
import nextstep.jwp.service.LoginRequest;
import nextstep.jwp.service.UserService;
import nextstep.jwp.util.MessageConverter;
import nextstep.jwp.util.ResourceLoader;
import org.apache.coyote.http11.model.Header;
import org.apache.coyote.http11.model.Session;
import org.apache.coyote.http11.model.Sessions;
import org.apache.coyote.http11.model.request.HttpRequest;
import org.apache.coyote.http11.model.response.HttpResponse;
import org.apache.coyote.http11.model.response.Status;

public class LoginController implements Controller {

    private static final String URL = "/login";
    private static final String SESSION_ID = "JSESSIONID";

    private static final UserService userService = new UserService();

    @Override
    public boolean isUrlMatches(final String url) {
        return URL.matches(url);
    }

    @Override
    public HttpResponse doGet(final HttpRequest request) throws IOException {
        if (loginAlready(request)) {
            String sessionId = request.getCookie().getValue(SESSION_ID);
            if (Sessions.find(sessionId).isPresent()) {
                HttpResponse response = HttpResponse.of(Status.FOUND);
                response.addHeader(Header.LOCATION, "/index.html");
                return response;
            }
        }
        HttpResponse response = HttpResponse.of(Status.OK);
        response.addResource(ResourceLoader.load("/login.html"));
        return response;
    }

    private boolean loginAlready(final HttpRequest request) {
        return request.getCookie().hasKey(SESSION_ID);
    }

    @Override
    public HttpResponse doPost(final HttpRequest request) throws IOException {
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
            response.addResource(ResourceLoader.load("/401.html"));
            return response;
        }
    }
}
