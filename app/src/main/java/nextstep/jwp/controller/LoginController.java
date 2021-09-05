package nextstep.jwp.controller;

import java.io.IOException;
import java.util.Map;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UnauthorizedException;
import nextstep.jwp.http.HttpRequest;
import nextstep.jwp.http.HttpResponse;
import nextstep.jwp.http.HttpSession;
import nextstep.jwp.http.HttpStatus;
import nextstep.jwp.model.User;
import nextstep.jwp.utils.ContentType;
import nextstep.jwp.utils.FileReader;
import nextstep.jwp.utils.RequestParams;

public class LoginController extends AbstractController {

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        final HttpSession httpSession = request.getSession();
        if (isLogin(httpSession)) {
            redirect(response, FileReader.file("/index.html"));

        }
        redirect(response, FileReader.file("/login.html"));
    }

    private boolean isLogin(HttpSession httpSession) {
        final Object user = (User) httpSession.getAttribute("user");
        return user != null;
    }

    private void redirect(HttpResponse response, String content) throws IOException {
        response.addHeaders("Content-Type", ContentType.HTML.getContentType());
        response.addHeaders("Content-Length", String.valueOf(content.getBytes().length));

        response.writeStatusLine(HttpStatus.OK);
        response.writeHeaders();
        response.writeBody(content);
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
        final String[] body = request.getBody().split("&");
        final Map<String, String> loginInfo = RequestParams.requestParams(body);

        try {
            final User user = login(loginInfo);
            HttpSession httpSession = request.getSession();
            httpSession.setAttribute("user", user);

            response.writeStatusLine(HttpStatus.FOUND);
            response.writeRedirect("/index.html");
        } catch (UnauthorizedException e) {
            ExceptionHandler.unauthorized(response);
        }
    }

    private User login(Map<String, String> loginInfo) {
        final User user = InMemoryUserRepository
                .findByAccount(loginInfo.get("account"))
                .orElseThrow(() -> new UnauthorizedException("존재하지 않는 사용자입니다."));

        user.checkPassword(loginInfo.get("password"));
        return user;
    }
}
