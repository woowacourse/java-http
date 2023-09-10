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

            response = loginSuccess(user);

        } else if (user.isPresent()) {
            log.warn("비밀번호가 틀렸습니다");

            response = loginFail();

        } else {
            log.warn("미가입회원입니다");

            response = loginFail();
        }
    }

    private HttpResponse loginSuccess(Optional<User> user) {
        Session session = new Session();
        session.setAttribute("user", user);
        SessionManager.add(session);

        HttpCookie cookie = HttpCookie.of("JSESSIONID=" + session.getId());

        return new HttpResponse.Builder()
                .status(FOUND)
                .contentType(HTML)
                .header("Location", "/index.html")
                .setCookie(cookie)
                .build();
    }

    private HttpResponse loginFail() {
        return new HttpResponse.Builder()
                .status(FOUND)
                .contentType(HTML)
                .header("Location", "/401.html")
                .build();
    }


    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        HttpCookie cookie = request.getRequestHeader().getCookie();

        String jsessionid = cookie.getValue("JSESSIONID");
        Session session = SessionManager.findSession(jsessionid);

        if (session != null) {
            response = new HttpResponse.Builder()
                    .status(FOUND)
                    .header("Location", "/index.html")
                    .contentType(HTML)
                    .build();
        }

        response = new HttpResponse.Builder()
                .status(OK)
                .contentType(HTML)
                .body(FileUtils.readFile("/login.html"))
                .build();
    }
}
