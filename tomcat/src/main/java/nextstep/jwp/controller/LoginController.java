package nextstep.jwp.controller;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.catalina.manager.SessionManager;
import org.apache.coyote.common.HttpCookie;
import org.apache.coyote.common.Session;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.request.HttpRequestBody;
import org.apache.coyote.response.HttpResponse;
import org.apache.coyote.utils.FileUtils;

import java.util.Optional;

import static org.apache.coyote.common.ContentType.HTML;
import static org.apache.coyote.response.HttpStatus.FOUND;
import static org.apache.coyote.response.HttpStatus.OK;

public class LoginController extends AbstractController {

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
        HttpRequestBody httpRequestBody = request.getRequestBody();
        String account = httpRequestBody.getValue("account");
        String password = httpRequestBody.getValue("password");

        Optional<User> user = InMemoryUserRepository.findByAccount(account);

        if (user.isPresent() && user.get().checkPassword(password)) {
            log.info(user.toString());

            loginSuccess(user, response);

        } else if (user.isPresent()) {
            log.warn("비밀번호가 틀렸습니다");

            loginFail(response);

        } else {
            log.warn("미가입회원입니다");

            loginFail(response);
        }
    }

    private void loginSuccess(Optional<User> user, HttpResponse response) {
        Session session = new Session();
        session.setAttribute("user", user);
        SessionManager.add(session);

        HttpCookie cookie = HttpCookie.of("JSESSIONID=" + session.getId());

        response.setStatus(FOUND);
        response.setContentType(HTML);
        response.addHeader("Location", "/index.html");
        response.setCookie(cookie);
    }

    private void loginFail(HttpResponse response) {
        response.setStatus(FOUND);
        response.setContentType(HTML);
        response.addHeader("Location", "/401.html");
    }


    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        HttpCookie cookie = request.getRequestHeader().getCookie();

        String jsessionid = cookie.getValue("JSESSIONID");
        Session session = SessionManager.findSession(jsessionid);

        if (session != null) {
            response.setStatus(FOUND);
            response.addHeader("Location", "/index.html");
            response.setContentType(HTML);
            return;
        }

        response.setStatus(OK);
        response.setContentType(HTML);
        response.setBody(FileUtils.readFile("/login.html"));
    }
}
