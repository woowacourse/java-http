package nextstep.jwp.application.controller;

import nextstep.jwp.application.db.InMemoryUserRepository;
import nextstep.jwp.application.exception.UnauthorizedException;
import nextstep.jwp.application.model.User;
import nextstep.jwp.framework.controller.AbstractController;
import nextstep.jwp.framework.message.MessageBody;
import nextstep.jwp.framework.message.builder.HttpResponseBuilder;
import nextstep.jwp.framework.message.request.FormData;
import nextstep.jwp.framework.message.request.HttpRequestMessage;
import nextstep.jwp.framework.message.response.HttpResponseMessage;
import nextstep.jwp.framework.session.HttpSession;

import java.util.Objects;

public class LoginController extends AbstractController {

    private static final String USER_SESSION_NAME = "user";

    @Override
    protected HttpResponseMessage doGet(HttpRequestMessage httpRequestMessage) {
        HttpSession httpSession = httpRequestMessage.takeSession();
        if (httpSession.isValid() && httpSession.contains(USER_SESSION_NAME)) {
            return HttpResponseBuilder.redirectTemporarily("/index.html")
                    .build();
        }
        return HttpResponseBuilder.staticResource("/login.html")
                .build();
    }

    @Override
    protected HttpResponseMessage doPost(HttpRequestMessage httpRequestMessage) {
        User user = loginWithFormBody(httpRequestMessage.getBody());

        HttpSession httpSession = httpRequestMessage.takeSession();
        if (httpSession.isInvalid()) {
            httpSession = httpRequestMessage.takeNewSession();
        }
        httpSession.put(USER_SESSION_NAME, user);
        return HttpResponseBuilder.redirectTemporarily("/index.html")
                .setSessionCookie(httpSession.getId())
                .build();
    }

    private User loginWithFormBody(MessageBody messageBody) {
        FormData formData = messageBody.toFormData();
        return login(
                formData.take("account"),
                formData.take("password")
        );
    }

    private User login(String account, String password) {
        validateRequiredValue(account, password);
        User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(() -> new UnauthorizedException(String.format("해당 아이디가 없습니다.(%s)", account)));
        if (!user.checkPassword(password)) {
            throw new UnauthorizedException("로그인에 싪패했습니다.");
        }
        return user;
    }

    void validateRequiredValue(String account, String password) {
        if (Objects.isNull(account) || Objects.isNull(password)) {
            throw new UnauthorizedException("아이디 또는 비밀번호를 입력하지 않았습니다.");
        }
    }
}
