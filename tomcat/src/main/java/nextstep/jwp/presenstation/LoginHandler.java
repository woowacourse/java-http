package nextstep.jwp.presenstation;

import java.util.Map;
import java.util.Optional;
import nextstep.jwp.application.LoginService;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.handler.AbstractRequestHandler;
import org.apache.coyote.http11.handler.ResponseEntity;
import org.apache.coyote.http11.http.ContentType;
import org.apache.coyote.http11.http.HttpHeaders;
import org.apache.coyote.http11.http.HttpRequest;
import org.apache.coyote.http11.http.HttpStatus;
import org.apache.coyote.http11.http.RequestBody;
import org.apache.coyote.http11.session.SessionManager;
import org.apache.coyote.util.FileUtil;

public class LoginHandler extends AbstractRequestHandler {

    private final LoginService loginService = new LoginService();

    @Override
    public ResponseEntity handle(HttpRequest httpRequest) {
        if (httpRequest.getHttpMethod().isGet()) {
            return doGet(httpRequest);
        }
        if (httpRequest.getHttpMethod().isPost()) {
            return doPost(httpRequest);
        }
        return new ResponseEntity(HttpStatus.NOTFOUND, FileUtil.readAllBytes("/404.html"), ContentType.HTML);
    }

    private ResponseEntity doGet(HttpRequest httpRequest) {
        if (SessionManager.contains(httpRequest.getJSessionId())) {
            return new ResponseEntity(HttpStatus.FOUND, ContentType.HTML,
                    new HttpHeaders(Map.of("Location", "/index.html")));
        }
        return new ResponseEntity(HttpStatus.OK, FileUtil.readAllBytes(httpRequest.getRequestUri() + ".html"),
                ContentType.HTML);
    }

    private ResponseEntity doPost(HttpRequest httpRequest) {
        final RequestBody requestBody = httpRequest.getRequestBody();
        Optional<User> foundUser = loginService.findUser(requestBody.get("account"));

        if (foundUser.isPresent() && foundUser.get().checkPassword(requestBody.get("password"))) {
            User user = foundUser.get();
            final var session = httpRequest.getSession();
            session.setAttribute("user", user);
            Map<String, String> headers = Map.of("Location", "/index.html", "Set-Cookie",
                    "JSESSIONID=" + session.getId());
            return new ResponseEntity(HttpStatus.FOUND, ContentType.HTML, new HttpHeaders(headers));
        }

        return new ResponseEntity(HttpStatus.UNAUTHORIZED, FileUtil.readAllBytes("/401.html"), ContentType.HTML);
    }
}
