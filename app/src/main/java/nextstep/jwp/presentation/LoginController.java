package nextstep.jwp.presentation;

import java.io.IOException;
import java.util.HashMap;
import nextstep.jwp.FileAccess;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UnauthorizedException;
import nextstep.jwp.http.HttpSession;
import nextstep.jwp.http.HttpSessions;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.http.response.status.HttpStatus;
import nextstep.jwp.model.User;

public class LoginController extends AbstractController {

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws IOException {
        String resource = new FileAccess(request.getPath() + ".html").getFile();

        try {
            String sessionId = request.getCookie("JSESSIONID");
            HttpSession session = HttpSessions.getSession(sessionId);
            User user = (User) session.getAttribute("user");

            checkExistUser(request, response, user);

        } catch (IllegalArgumentException e) {
            renderPage(request, response, resource);
        }
    }

    private void checkExistUser(HttpRequest request, HttpResponse response, User user)
            throws IOException {
        if (InMemoryUserRepository.existAccount(user.getAccount())) {
            String resource = new FileAccess("/index.html").getFile();
            renderPage(request, response, resource);
            return;
        }

        throw new IllegalArgumentException();
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        HashMap<String, String> parseBody = parseBody(request.getBody());

        String account = parseBody.get("account");
        String password = parseBody.get("password");

        User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(
                        () -> new UnauthorizedException(HttpStatus.UNAUTHORIZED, "존재하지 않는 아이디입니다."));

        if (!user.checkPassword(password)) {
            throw new UnauthorizedException(HttpStatus.UNAUTHORIZED, "올바르지 않은 패스워드입니다.");
        }

        setCookie(user, response);

        redirectPage(request, response, HttpStatus.FOUND, "/index.html");
    }

    private void setCookie(User user, HttpResponse response) {
        HttpSession session = HttpSessions.createSession();
        session.setAttribute("user", user);

        response.setCookie("JSESSIONID", session.getId());
    }

    private HashMap<String, String> parseBody(String requestBody) {

        HashMap<String, String> result = new HashMap<>();

        String[] splitBody = requestBody.split("&");

        for (String body : splitBody) {
            String[] keyValue = body.split("=");
            result.put(keyValue[0], keyValue[1]);
        }

        return result;
    }
}
