package nextstep.jwp.service;

import java.util.NoSuchElementException;
import java.util.Optional;
import nextstep.jwp.infra.InMemoryUserRepository;
import nextstep.jwp.model.User;
import nextstep.jwp.model.UserRepository;
import org.apache.coyote.http.HttpRequest;

public class AuthService {

    private final UserRepository userRepository = InMemoryUserRepository.getInstance();

    public void login(final HttpRequest httpRequest) {
        final String account = httpRequest.findQueryByKey("account")
                .orElseThrow(() -> new NoSuchElementException("아이디를 입력해주세요."));
        final String password = httpRequest.findQueryByKey("password")
                .orElseThrow(() -> new NoSuchElementException("비밀번호를 입력해주세요."));

        final User user = userRepository.findByAccount(account)
                .orElseThrow(() -> new NoSuchElementException("회원가입이 되어있지 않은 유저입니다."));

        user.checkPassword(password);
    }
}
