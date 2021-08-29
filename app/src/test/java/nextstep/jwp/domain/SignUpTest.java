package nextstep.jwp.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.junit.jupiter.api.Test;

class SignUpTest {

    @Test
    void overlapped() {
        //given
        Map<String, String> body = new HashMap<>();
        body.put("account", "gugu");
        body.put("password", "password");
        body.put("email", "hkkang@woowahan.com");
        SignUp signUp = new SignUp(body);

        //when
        boolean isAble = signUp.isAble();

        //then
        assertThat(isAble).isFalse();
    }

    @Test
    void able() {
        //given
        Map<String, String> body = new HashMap<>();
        body.put("account", "whybe2");
        body.put("password", "password");
        body.put("email", "hybeom@gmail.com");
        SignUp signUp = new SignUp(body);

        //when
        boolean isAble = signUp.isAble();

        //then
        assertThat(isAble).isTrue();
    }

    @Test
    void process() {
        //given
        Map<String, String> body = new HashMap<>();
        body.put("account", "whybe");
        body.put("password", "password");
        body.put("email", "hybeom@gmail.com");
        SignUp signUp = new SignUp(body);
        signUp.process();

        //when
        Optional<User> user = InMemoryUserRepository.findByAccount("whybe");

        //then
        assertThat(user).isPresent();
    }
}