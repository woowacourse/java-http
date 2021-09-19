package nextstep.jwp.controller;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UnauthorizedException;
import nextstep.jwp.http.HttpSession;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.http.response.HttpStatus;
import nextstep.jwp.model.User;
import nextstep.jwp.utils.ContentType;
import nextstep.jwp.utils.FileReader;
import nextstep.jwp.utils.RequestParams;
import nextstep.jwp.utils.Resources;

public class LoginController extends AbstractController {

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        final HttpSession httpSession = request.getSession();
        if (isLogin(httpSession)) {
            redirect(response, FileReader.file(Resources.INDEX.getResource()));
            return;
        }
        redirect(response, FileReader.file(Resources.LOGIN.getResource()));
    }

    private boolean isLogin(HttpSession httpSession) {
        return httpSession.hasAttribute("user");
    }

    private void redirect(HttpResponse response, String content) throws IOException {
        response.setHttpStatus(HttpStatus.OK);

        response.addHeaders("Content-Type", ContentType.HTML.getType());
        response.addHeaders("Content-Length", String.valueOf(content.getBytes().length));

        response.setBody(content);
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
        final String[] body = request.getBody().split("&");
        final Map<String, String> loginInfo = RequestParams.requestParams(body);

        try {
            final User user = login(loginInfo);

            if (request.getCookie().getCookies("JSESSIONID") == null) {
                response.addHeaders("Set-Cookie",
                        String.format("JSESSIONID=%s", UUID.randomUUID()));
            }

            HttpSession httpSession = request.getSession();
            httpSession.setAttribute("user", user);

            response.setHttpStatus(HttpStatus.FOUND);
            response.setBody(Resources.INDEX.getResource());
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
