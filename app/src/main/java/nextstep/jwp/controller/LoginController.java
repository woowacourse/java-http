package nextstep.jwp.controller;

import java.util.Map;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UnauthorizedException;
import nextstep.jwp.utils.ContentType;
import nextstep.jwp.utils.FileReader;
import nextstep.jwp.http.HttpRequest;
import nextstep.jwp.http.HttpResponse;
import nextstep.jwp.http.HttpStatus;
import nextstep.jwp.model.User;
import nextstep.jwp.utils.RequestParams;

public class LoginController extends AbstractController {

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        String content = FileReader.file("/login.html");

        response.writeStatusLine(HttpStatus.OK);
        response.writeHeaders(content, ContentType.HTML);
        response.writeBody(content);
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
        final String[] body = request.getBody().split("&");
        final Map<String, String> loginInfo = RequestParams.requestParams(body);

        try {
            login(loginInfo);
            response.writeStatusLine(HttpStatus.FOUND);
            response.writeRedirect("/index.html");
        } catch (UnauthorizedException e) {
            ExceptionHandler.unauthorized(response);
        }
    }

    private void login(Map<String, String> loginInfo) {
        final User user = InMemoryUserRepository
                .findByAccount(loginInfo.get("account"))
                .orElseThrow(() -> new UnauthorizedException("존재하지 않는 사용자입니다."));

        user.checkPassword(loginInfo.get("password"));
    }
}
