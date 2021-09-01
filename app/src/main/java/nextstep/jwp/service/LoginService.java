package nextstep.jwp.service;

import java.util.Objects;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.http.HttpSession;
import nextstep.jwp.http.HttpSessions;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.request.RequestBody;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginService {

    private final Logger log = LoggerFactory.getLogger(LoginService.class);

    public User findUser(RequestBody requestBody) {
        String account = requestBody.getParam("account");
        return InMemoryUserRepository.findByAccount(account)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다. 회원가입을 해주세요"));
    }

    public boolean isLoginUser(String jSessionId) {
        HttpSession httpSession = HttpSessions.getSession(jSessionId);
        User user = getUser(httpSession);

        if (Objects.nonNull(user)) {
            log.debug("user 정보 : {}", user.getAccount());
            return true;
        }
        log.debug("false 입니다");
        return false;
    }

    private User getUser(HttpSession httpSession) {
        return (User) httpSession.getAttribute("user");
    }
}
