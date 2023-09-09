package nextstep.jwp.presentation;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.request.FileReader;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.SessionManager;

import static org.apache.coyote.http11.response.StatusCode.UNAUTHORIZED;
import static org.reflections.Reflections.log;

public class LoginController extends AbstractController {

    private static final LoginController INSTANCE = new LoginController();

    private LoginController() {
    }

    public static LoginController getInstance() {
        return INSTANCE;
    }

    @Override
    public HttpResponse doGet(HttpRequest httpRequest, HttpResponse httpResponse) {
        if (httpRequest.hasSessionId()) {
            return httpResponse
                    .addBaseHeader(httpRequest.getContentType())
                    .redirect("/index.html");
        }
        String body = FileReader.read(httpRequest.getUri());
        return httpResponse
                .addBaseHeader(httpRequest.getContentType())
                .addBody(body);
    }

    @Override
    public HttpResponse doPost(HttpRequest httpRequest, HttpResponse httpResponse) {
        try {
            login(httpRequest, httpResponse);
            return httpResponse
                    .addBaseHeader(httpRequest.getContentType())
                    .redirect("/index.html");

        } catch (IllegalArgumentException e) {
            String body = FileReader.read("/401.html");
            return httpResponse
                    .setStatusCode(UNAUTHORIZED)
                    .addBaseHeader(httpRequest.getContentType())
                    .addBody(body);
        }
    }

    private void login(HttpRequest httpRequest, HttpResponse httpResponse) {
        User user = InMemoryUserRepository.findByAccount(httpRequest.getBodyValue("account"))
                .orElseThrow(() -> new IllegalArgumentException("해당 아이디의 사용자가 존재하지 않습니다"));

        if (!user.checkPassword(httpRequest.getBodyValue("password"))) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다");
        }

        String sessionId = SessionManager.getSessionId(user);
        httpResponse.setCookie(sessionId);
        log.info("로그인 성공: {}", SessionManager.getSession(sessionId).getAttribute("user"));
    }
}
