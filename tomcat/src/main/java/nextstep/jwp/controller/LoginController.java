package nextstep.jwp.controller;

import static org.apache.coyote.http11.HeaderField.CONTENT_LENGTH;
import static org.apache.coyote.http11.HeaderField.CONTENT_TYPE;
import static org.apache.coyote.http11.HeaderField.LOCATION;
import static org.apache.coyote.http11.HeaderField.SET_COOKIE;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;
import nextstep.jwp.LoginFailureException;
import nextstep.jwp.model.User;
import nextstep.jwp.service.LoginService;
import org.apache.coyote.http11.HttpCookie;
import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.http11.Session;
import org.apache.coyote.http11.SessionManager;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.ContentType;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.ResponseBody;
import org.apache.coyote.http11.response.ResponseHeaders;

public class LoginController extends AbstractController {

    private final LoginService loginService;

    public LoginController() {
        this.loginService = new LoginService();
    }

    @Override
    protected HttpResponse doGet(HttpRequest request) throws IOException {
        if (isExistSession(request)) {

            ResponseBody body = new ResponseBody();
            final ResponseHeaders headers = ResponseHeaders.create()
                    .addHeader(LOCATION, "/index");
            return HttpResponse.create(HttpStatus.FOUND, headers, body);
        }

        String requestUri = request.getRequestUri();
        ResponseBody body = ResponseBody.from(requestUri);
        final ResponseHeaders headers = ResponseHeaders.create()
                .addHeader(CONTENT_TYPE, ContentType.find(requestUri) + ";charset=utf-8 ")
                .addHeader(CONTENT_LENGTH, body.getBytesLength());
        return HttpResponse.create(HttpStatus.OK, headers, body);
    }

    @Override
    protected HttpResponse doPost(HttpRequest request) {
        final String account = request.getBodyValue("account");
        final String password = request.getBodyValue("password");

        try {
            final User user = loginService.login(account, password);
            final UUID uuid = UUID.randomUUID();
            final Session session = new Session(uuid.toString());
            session.setAttribute("user", user);
            SessionManager.add(session);

            ResponseBody body = new ResponseBody();
            final ResponseHeaders headers = ResponseHeaders.create()
                    .addHeader(LOCATION, "/index")
                    .addHeader(SET_COOKIE, "JSESSIONID=" + uuid);
            return HttpResponse.create(HttpStatus.FOUND, headers, body);

        } catch (LoginFailureException exception) {
            return ControllerAdvice.handleUnauthorized();
        }
    }

    private boolean isExistSession(HttpRequest request) {
        final HttpCookie cookie = request.getCookie();
        final String jSessionId = cookie.getJSESSIONID();
        final Optional<Session> session = SessionManager.findSession(jSessionId);
        return session.isPresent();
    }
}
