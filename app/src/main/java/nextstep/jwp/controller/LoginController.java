package nextstep.jwp.controller;

import java.io.IOException;
import java.util.Map;
import nextstep.jwp.exception.UnauthorizedException;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.http.ContentType;
import nextstep.jwp.http.FileReader;
import nextstep.jwp.http.HttpError;
import nextstep.jwp.http.HttpRequest;
import nextstep.jwp.http.HttpResponse;
import nextstep.jwp.utils.RequestParams;
import nextstep.jwp.model.User;

public class LoginController extends AbstractController {

    public LoginController(HttpRequest httpRequest) {
        super(httpRequest);
    }

    @Override
    public byte[] get(HttpRequest httpRequest) throws IOException {
        return HttpResponse.ok(
                FileReader.file(httpRequest.uri()),
                ContentType.findBy(httpRequest.uri())
        );
    }

    @Override
    public byte[] post(HttpRequest httpRequest) throws IOException {
        final String[] body = httpRequest.body().split("&");
        final Map<String, String> loginInfo = RequestParams.requestParams(body);

        try {
            login(loginInfo);
            return HttpResponse.found(Controller.INDEX_PAGE);
        } catch (UnauthorizedException e) {
            return HttpResponse.error(HttpError.UNAUTHORIZED);
        }
    }

    private void login(Map<String, String> loginInfo) {
        final User user = InMemoryUserRepository
                .findByAccount(loginInfo.get("account"))
                .orElseThrow(() -> new UnauthorizedException("존재하지 않는 사용자입니다."));

        user.checkPassword(loginInfo.get("password"));
    }

    @Override
    public byte[] error(HttpError httpError) throws IOException {
        return HttpResponse.error(httpError);
    }
}
