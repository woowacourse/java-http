package nextstep.jwp.controller;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UnauthorizedException;
import nextstep.jwp.http.controller.AbstractController;
import nextstep.jwp.http.message.MessageBody;
import nextstep.jwp.http.message.request.HttpRequestMessage;
import nextstep.jwp.http.message.response.HttpResponseMessage;
import nextstep.jwp.http.utils.HttpParseUtils;
import nextstep.jwp.model.User;

import java.util.Map;
import java.util.Objects;

public class LoginController extends AbstractController {

    @Override
    protected void doGet(HttpRequestMessage httpRequestMessage, HttpResponseMessage httpResponseMessage) {
        httpRequestMessage.changeRequestUri("/login.html");
    }

    @Override
    protected void doPost(HttpRequestMessage httpRequestMessage, HttpResponseMessage httpResponseMessage) {
        MessageBody messageBody = httpRequestMessage.getBody();
        Map<String, String> formData = HttpParseUtils.extractFormData(messageBody);
        String account = formData.get("account");
        String password = formData.get("password");
        login(account, password);

        httpRequestMessage.changeRequestUri("redirect:/index.html");
    }

    private void login(String account, String password) {
        validateRequiredValue(account, password);
        User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(() -> new UnauthorizedException(String.format("해당 아이디가 없습니다.(%s)", account)));
        if (!user.checkPassword(password)) {
            throw new UnauthorizedException("로그인에 싪패했습니다.");
        }
    }

    void validateRequiredValue(String account, String password) {
        if (Objects.isNull(account) || Objects.isNull(password)) {
            throw new UnauthorizedException("아이디 또는 비밀번호를 입력하지 않았습니다.");
        }
    }
}
