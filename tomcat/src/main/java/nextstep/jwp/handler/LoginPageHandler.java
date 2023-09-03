package nextstep.jwp.handler;

import static org.apache.catalina.servlet.response.HttpStatus.FOUND;
import static org.apache.catalina.servlet.response.HttpStatus.OK;
import static org.apache.catalina.servlet.session.SessionConstant.JSESSIONID;

import nextstep.jwp.util.ResourceFileUtil;
import org.apache.catalina.servlet.request.HttpRequest;
import org.apache.catalina.servlet.response.HttpResponse;
import org.apache.catalina.servlet.response.StatusLine;
import org.apache.catalina.servlet.session.SessionManager;

public class LoginPageHandler implements RequestHandler {

    @Override
    public boolean canHandle(HttpRequest request) {
        return request.startLine().uri().path().equals("/login")
                && request.startLine().method().equals("GET");
    }

    @Override
    public void handle(HttpRequest request, HttpResponse response) {
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
