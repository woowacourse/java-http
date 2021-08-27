package nextstep.jwp.controller;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UnauthorizedException;
import nextstep.jwp.http.HttpPath;
import nextstep.jwp.http.HttpStatusCode;
import nextstep.jwp.http.message.request.HttpRequestMessage;
import nextstep.jwp.http.message.response.HttpResponseMessage;
import nextstep.jwp.model.User;

import java.util.Map;

public class LoginController extends AbstractController {

    @Override
    protected void doGet(HttpRequestMessage httpRequestMessage, HttpResponseMessage httpResponseMessage) {
        HttpPath httpPath = httpRequestMessage.requestPath();
        if (!httpPath.hasQueryString()) {
            httpRequestMessage.changeRequestUri("/login.html");
            return;
        }

        Map<String, String> queryParams = httpPath.extractQueryParams();
        String account = queryParams.get("account");
        String password = queryParams.get("password");
        login(account, password);

        httpRequestMessage.changeRequestUri("redirect:/index.html");
    }

    private User login(String account, String password) {
        User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(() -> new UnauthorizedException(String.format("해당 아이디가 없습니다.(%s)", account)));
        if (!user.checkPassword(password)) {
            throw new UnauthorizedException("로그인에 싪패했습니다.");
        }
        return user;
    }
}
