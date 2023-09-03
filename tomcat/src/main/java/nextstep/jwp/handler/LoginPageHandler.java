package nextstep.jwp.handler;

import static org.apache.catalina.servlet.response.HttpStatus.FOUND;
import static org.apache.catalina.servlet.session.SessionConstant.JSESSIONID;

import nextstep.jwp.util.ResourceFileUtil;
import org.apache.catalina.servlet.handler.RequestHandler;
import org.apache.catalina.servlet.request.HttpRequest;
import org.apache.catalina.servlet.response.HttpResponse;
import org.apache.catalina.servlet.response.HttpStatus;
import org.apache.catalina.servlet.response.ResponseHeaders;
import org.apache.catalina.servlet.response.StatusLine;
import org.apache.catalina.servlet.session.SessionManager;

public class LoginPageHandler implements RequestHandler {

    @Override
    public boolean canHandle(HttpRequest request) {
        return request.startLine().uri().path().equals("/login")
                && request.startLine().method().equals("GET");
    }

    @Override
    public HttpResponse handle(HttpRequest request) {
        if (isAuthenticated(request)) {
            StatusLine statusLine = new StatusLine(FOUND);
            HttpResponse response = new HttpResponse(statusLine, null);
            response.addHeader("Location", "/index.html");
            return response;
        } else {
            StatusLine statusLine = new StatusLine(HttpStatus.OK);
            ResponseHeaders responseHeaders = new ResponseHeaders();
            responseHeaders.put("Content-Type", "text/html;charset=utf-8");
            String resource = ResourceFileUtil.readAll("static" + "/login.html");
            return new HttpResponse(statusLine, responseHeaders, resource);
        }
    }

    private boolean isAuthenticated(HttpRequest request) {
        String id = request.cookies().get(JSESSIONID);
        return SessionManager.findSession(id) != null;
    }
}
