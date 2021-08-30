package nextstep.jwp.controller;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import nextstep.jwp.exception.UnauthorizedException;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.http.ContentType;
import nextstep.jwp.http.FileReader;
import nextstep.jwp.http.HttpError;
import nextstep.jwp.http.HttpRequest;
import nextstep.jwp.http.HttpResponse;
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
        final Map<String, String> loginInfo = getRequestBody(body);

        try {
            login(loginInfo);
            return HttpResponse.ok(
                    FileReader.file(httpRequest.uri()),
                    ContentType.findBy(httpRequest.uri())
            );
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

    private Map<String, String> getRequestBody(String[] body) {
        final Map<String, String> registerInfo = new LinkedHashMap<>();
        for (String b : body) {
            final String[] split = b.split("=");
            registerInfo.put(split[0].trim(), split[1].trim());
        }
        return registerInfo;
    }

    @Override
    public byte[] error(HttpError httpError) throws IOException {
        return HttpResponse.error(httpError);
    }
}
