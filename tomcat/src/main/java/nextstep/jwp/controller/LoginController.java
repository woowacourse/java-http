package nextstep.jwp.controller;

import java.util.Optional;
import java.util.UUID;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.controller.Controller;
import org.apache.coyote.http11.cookie.HttpCookie;
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
import static org.apache.coyote.http11.request.HttpMethod.GET;

public class LoginController implements Controller {

    private static final String URI = "/login.html";
    private static final String UNAUTHORIZED = "/401.html";
    private static final String REDIRECT_HOME_URI = "/index.html";

    public static final SessionManager SESSION_MANAGER = new SessionManager();

    @Override
    public HttpResponse service(HttpRequest request) {
        if (request.getRequestLine().getHttpMethod().is(GET)) {
            return doGet(request);
        }
        return doPost(request);
    }

    private HttpResponse doGet(HttpRequest request) {
        if (hasCookie(request)) {
            return redirectHome(request);
        }
        return showLoginPage(request);
    }

    private boolean hasCookie(HttpRequest httpRequest) {
        HttpCookie cookie = HttpCookie.from(httpRequest.getHeaderValue("Cookie"));
        return SESSION_MANAGER.findSession(cookie.getValue("JSESSIONID")) != null;
    }

    private HttpResponse redirectHome(HttpRequest request) {
        StatusLine statusLine = new StatusLine(request.getRequestLine().getVersion(), HttpStatus.FOUND);
        ResponseBody responseBody = new ResponseBody(FileReader.read(REDIRECT_HOME_URI));
        return HttpResponse.builder()
                .statusLine(statusLine)
                .contentType(HTML.getValue())
                .contentLength(responseBody.getValue().getBytes().length)
                .redirect(REDIRECT_HOME_URI)
                .responseBody(responseBody)
                .build();
    }

    private HttpResponse showLoginPage(HttpRequest request) {
        ResponseBody responseBody = new ResponseBody(FileReader.read(URI));
        StatusLine statusLine = new StatusLine(request.getRequestLine().getVersion(), HttpStatus.OK);
        return HttpResponse.builder()
                .statusLine(statusLine)
                .contentType(HTML.getValue())
                .contentLength(responseBody.getValue().length())
                .responseBody(responseBody)
                .build();
    }

    private HttpResponse doPost(HttpRequest httpRequest) {
        Optional<User> optionalUser = findUser(httpRequest);
        if (optionalUser.isEmpty()) {
            return redirectUnauthorized(httpRequest);
        }

        return successLogin(httpRequest);
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

    private HttpResponse redirectUnauthorized(HttpRequest httpRequest) {
        ResponseBody responseBody = new ResponseBody(FileReader.read(UNAUTHORIZED));
        return HttpResponse.builder()
                .statusLine(new StatusLine(httpRequest.getRequestLine().getVersion(), HttpStatus.UNAUTHORIZED))
                .contentType(HTML.getValue())
                .contentLength(responseBody.getValue().getBytes().length)
                .redirect(UNAUTHORIZED)
                .responseBody(responseBody)
                .build();
    }

    private HttpResponse successLogin(HttpRequest httpRequest) {
        String uuid = UUID.randomUUID().toString();
        SESSION_MANAGER.add(new Session(uuid));

        ResponseBody responseBody = new ResponseBody(FileReader.read(REDIRECT_HOME_URI));
        return HttpResponse.builder()
                .statusLine(new StatusLine(httpRequest.getRequestLine().getVersion(), HttpStatus.FOUND))
                .contentType(HTML.getValue())
                .contentLength(responseBody.getValue().getBytes().length)
                .setCookie(HttpCookie.jSessionId(uuid))
                .redirect(REDIRECT_HOME_URI)
                .responseBody(responseBody)
                .build();
    }

}
