package nextstep.jwp.handler;

import static org.apache.catalina.servlet.response.HttpStatus.FOUND;
import static org.apache.catalina.servlet.response.HttpStatus.OK;
import static org.apache.catalina.servlet.session.SessionConstant.JSESSIONID;

import java.util.Map;
import java.util.UUID;
import nextstep.jwp.exception.UnAuthenticatedException;
import nextstep.jwp.model.User;
import nextstep.jwp.service.AuthService;
import nextstep.jwp.util.ResourceFileUtil;
import org.apache.catalina.servlet.AbstractController;
import org.apache.catalina.servlet.request.HttpRequest;
import org.apache.catalina.servlet.response.HttpResponse;
import org.apache.catalina.servlet.response.StatusLine;
import org.apache.catalina.servlet.session.Session;
import org.apache.catalina.servlet.session.SessionManager;
import org.apache.catalina.servlet.util.RequestParamUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginHandler extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(LoginHandler.class);
    private final AuthService authService = new AuthService();

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        Map<String, String> formData = RequestParamUtil.parse(request.body());
        response.setStatusLine(new StatusLine(FOUND));
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
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        if (isAuthenticated(request)) {
            response.setStatusLine(new StatusLine(FOUND));
            response.addHeader("Location", "/index.html");
        } else {
            response.setStatusLine(new StatusLine(OK));
            response.addHeader("Content-Type", "text/html;charset=utf-8");
            String resource = ResourceFileUtil.readAll("static" + "/login.html");
            response.setMessageBody(resource);
        }
    }

    private boolean isAuthenticated(HttpRequest request) {
        String id = request.cookies().get(JSESSIONID);
        return SessionManager.findSession(id) != null;
    }
}
