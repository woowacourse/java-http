package nextstep.jwp.application.controller;

import nextstep.jwp.application.db.InMemoryUserRepository;
import nextstep.jwp.application.exception.UnauthorizedException;
import nextstep.jwp.framework.controller.AbstractController;
import nextstep.jwp.framework.message.MessageBody;
import nextstep.jwp.framework.message.builder.HttpResponseBuilder;
import nextstep.jwp.framework.message.request.FormData;
import nextstep.jwp.framework.message.request.HttpRequestMessage;
import nextstep.jwp.framework.message.response.HttpResponseMessage;
import nextstep.jwp.application.model.User;

import java.util.Objects;

public class LoginController extends AbstractController {

    @Override
    protected HttpResponseMessage doGet(HttpRequestMessage httpRequestMessage) {
        return HttpResponseBuilder.staticResource("/login.html")
                .build();
    }

    @Override
    protected HttpResponseMessage doPost(HttpRequestMessage httpRequestMessage) {
        MessageBody messageBody = httpRequestMessage.getBody();
        FormData formData = messageBody.toFormData();
        login(
                formData.take("account"),
                formData.take("password")
        );
        return HttpResponseBuilder.redirectTemporarily("/index.html")
                .build();
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
