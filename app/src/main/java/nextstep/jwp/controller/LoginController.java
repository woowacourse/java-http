package nextstep.jwp.controller;

import java.io.FileNotFoundException;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.LoginException;
import nextstep.jwp.handler.HttpBody;
import nextstep.jwp.handler.HttpRequest;
import nextstep.jwp.handler.HttpResponse;
import nextstep.jwp.model.User;
import nextstep.jwp.util.File;
import nextstep.jwp.util.FileReader;

public class LoginController extends AbstractController {

    @Override
    protected void doGet(HttpRequest httpRequest, HttpResponse httpResponse) throws FileNotFoundException {
        if (httpRequest.isUriContainsQuery()) {
            doGetWithQuery(httpRequest, httpResponse);
            return;
        }
        File file = FileReader.readHtmlFile(httpRequest.getRequestUrl());
        httpResponse.ok(file);
    }

    private void doGetWithQuery(HttpRequest httpRequest, HttpResponse httpResponse) {}

    @Override
    protected void doPost(HttpRequest httpRequest, HttpResponse httpResponse) throws FileNotFoundException {
        HttpBody httpBody = httpRequest.getHttpBody();
        String account = httpBody.getBodyParams("account");
        String password = httpBody.getBodyParams("password");

        try {
            User user = findUser(account);
            if (!user.checkPassword(password)) {
                throw new LoginException("User의 정보와 입력한 정보가 일치하지 않습니다.");
            }

            File file = FileReader.readFile("/index.html");
            httpResponse.redirect("/index.html", file);
        } catch (LoginException e) {
            File file = FileReader.readErrorFile("/401.html");
            httpResponse.unauthorized("/401.html", file);
        }
    }

    private User findUser(String account) {
        return InMemoryUserRepository.findByAccount(account)
                                     .orElseThrow(() -> { throw new LoginException("해당 User가 존재하지 않습니다."); });
    }
}
