package nextstep.jwp.service;

import java.util.NoSuchElementException;
import nextstep.jwp.service.dto.UserResponseDto;
import nextstep.jwp.infra.InMemoryUserRepository;
import nextstep.jwp.domain.User;
import nextstep.jwp.domain.UserRepository;
import org.apache.coyote.http.HttpRequest;
import org.apache.coyote.http.Session;
import org.apache.coyote.http.SessionManager;

public class AuthService {

    private final UserRepository userRepository = InMemoryUserRepository.getInstance();

    public void login(final HttpRequest httpRequest) {
        final String account = httpRequest.findQueryByKey("account")
                .orElseThrow(() -> new NoSuchElementException("아이디를 입력해주세요."));
        final String password = httpRequest.findQueryByKey("password")
                .orElseThrow(() -> new NoSuchElementException("비밀번호를 입력해주세요."));

        final User user = findUser(account);
        user.checkPassword(password);
        registerUserInSession(httpRequest, user);
    }

    private User findUser(String account) {
        return userRepository.findByAccount(account)
                        .orElseThrow(() -> new NoSuchElementException("회원가입이 되어있지 않은 유저입니다."));
    }

    private void registerUserInSession(HttpRequest httpRequest, User user) {
        final Session session = SessionManager.findSession(httpRequest.getSession().getId())
                .orElseThrow(() -> new NoSuchElementException("세션이 존재하지 않습니다."));
        session.setAttribute("user", user.getId());
    }

    public UserResponseDto register(HttpRequest httpRequest) {
        final String account = httpRequest.findQueryByKey("account")
                .orElseThrow(() -> new NoSuchElementException("아이디를 입력해주세요."));
        final String password = httpRequest.findQueryByKey("password")
                .orElseThrow(() -> new NoSuchElementException("비밀번호를 입력해주세요."));
        final String email = httpRequest.findQueryByKey("email")
                .orElseThrow(() -> new NoSuchElementException("이메일을 입력해주세요."));

        User savedUser = userRepository.save(new User(account, password, email));

        return UserResponseDto.from(savedUser);
    }
}
