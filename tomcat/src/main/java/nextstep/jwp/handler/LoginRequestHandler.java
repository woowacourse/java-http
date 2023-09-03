package nextstep.jwp.handler;

import static org.apache.coyote.http11.response.HttpStatus.FOUND;
import static org.apache.coyote.http11.session.SessionConstant.JSESSIONID;

import java.util.Map;
import java.util.UUID;
import nextstep.jwp.exception.UnAuthenticatedException;
import nextstep.jwp.model.User;
import nextstep.jwp.service.AuthService;
import org.apache.coyote.http11.Http11Processor;
import org.apache.coyote.http11.handler.RequestHandler;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.StatusLine;
import org.apache.coyote.http11.session.Session;
import org.apache.coyote.http11.session.SessionManager;
import org.apache.coyote.http11.util.RequestParamUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginRequestHandler implements RequestHandler {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private final AuthService authService = new AuthService();

    @Override
    public boolean canHandle(HttpRequest request) {
        return request.startLine().uri().path().equals("/login")
                && request.startLine().method().equals("POST");
    }

    @Override
    public HttpResponse handle(HttpRequest request) {
        Map<String, String> formData = RequestParamUtil.parse(request.body());
        StatusLine statusLine = new StatusLine(FOUND);
        HttpResponse response = new HttpResponse(statusLine, null);
        try {
            User user = authService.login(formData.get("account"), formData.get("password"));
            log.info("User={}", user);
            response.addHeader("Location", "/index.html");
            Session session = new Session(UUID.randomUUID().toString());
            SessionManager.add(session);
            response.addCookie(JSESSIONID, session.id());
        } catch (UnAuthenticatedException e) {
            response.addHeader("Location", "/401.html");
        }
        return response;
    }
}
