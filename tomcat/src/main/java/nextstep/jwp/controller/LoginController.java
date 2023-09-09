package nextstep.jwp.controller;

import java.util.Optional;
import java.util.UUID;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.common.HttpCookie;
import org.apache.coyote.http11.controller.AbstractController;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.RequestBody;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;
import org.apache.coyote.http11.response.ResponseBody;
import org.apache.coyote.http11.response.StatusLine;
import org.apache.coyote.http11.session.Session;
import org.apache.coyote.http11.session.SessionManager;
import org.apache.coyote.http11.util.FileReader;

import static org.apache.coyote.http11.request.ContentType.HTML;

public class LoginController extends AbstractController {

    private static final String URI = "/login.html";
    private static final String UNAUTHORIZED = "/401.html";
    private static final String REDIRECT_HOME_URI = "/index.html";

    public static final SessionManager SESSION_MANAGER = new SessionManager();

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        if (hasCookie(request)) {
            redirectHome(request, response);
            return;
        }
        showLoginPage(request, response);
    }

    private boolean hasCookie(HttpRequest httpRequest) {
        HttpCookie cookie = HttpCookie.from(httpRequest.getHeaderValue("Cookie"));
        return SESSION_MANAGER.findSession(cookie.getValue("JSESSIONID")) != null;
    }

    private void redirectHome(HttpRequest request, HttpResponse response) {
        StatusLine statusLine = new StatusLine(request.getRequestLine().getVersion(), HttpStatus.FOUND);
        ResponseBody responseBody = new ResponseBody(FileReader.read(REDIRECT_HOME_URI));
        response
                .statusLine(statusLine)
                .contentType(HTML.getValue())
                .contentLength(responseBody.getValue().getBytes().length)
                .redirect(REDIRECT_HOME_URI)
                .responseBody(responseBody);
    }

    private void showLoginPage(HttpRequest request, HttpResponse response) {
        ResponseBody responseBody = new ResponseBody(FileReader.read(URI));
        StatusLine statusLine = new StatusLine(request.getRequestLine().getVersion(), HttpStatus.OK);
        response
                .statusLine(statusLine)
                .contentType(HTML.getValue())
                .contentLength(responseBody.getValue().length())
                .responseBody(responseBody);
    }

    @Override
    protected void doPost(HttpRequest httpRequest, HttpResponse httpResponse) {
        Optional<User> optionalUser = findUser(httpRequest);
        if (optionalUser.isEmpty()) {
            redirectUnauthorized(httpRequest, httpResponse);
            return;
        }

        successLogin(httpRequest, httpResponse);
    }

    private Optional<User> findUser(HttpRequest request) {
        RequestBody requestBody = request.getRequestBody();
        String account = requestBody.getValueOf("account");
        String password = requestBody.getValueOf("password");
        return InMemoryUserRepository.findByAccount(account)
                .stream()
                .filter(it -> it.checkPassword(password))
                .findFirst();
    }

    private void redirectUnauthorized(HttpRequest httpRequest, HttpResponse httpResponse) {
        ResponseBody responseBody = new ResponseBody(FileReader.read(UNAUTHORIZED));
        httpResponse
                .statusLine(new StatusLine(httpRequest.getRequestLine().getVersion(), HttpStatus.UNAUTHORIZED))
                .contentType(HTML.getValue())
                .contentLength(responseBody.getValue().getBytes().length)
                .redirect(UNAUTHORIZED)
                .responseBody(responseBody);
    }

    private void successLogin(HttpRequest httpRequest, HttpResponse httpResponse) {
        String sessionId = addSession();

        ResponseBody responseBody = new ResponseBody(FileReader.read(REDIRECT_HOME_URI));
        httpResponse
                .statusLine(new StatusLine(httpRequest.getRequestLine().getVersion(), HttpStatus.FOUND))
                .contentType(HTML.getValue())
                .contentLength(responseBody.getValue().getBytes().length)
                .setCookie(HttpCookie.jSessionId(sessionId))
                .redirect(REDIRECT_HOME_URI)
                .responseBody(responseBody);
    }

    private String addSession() {
        String uuid = UUID.randomUUID().toString();
        SESSION_MANAGER.add(new Session(uuid));
        return uuid;
    }

}
