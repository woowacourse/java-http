package nextstep.jwp.handler;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.http.QueryParams;
import nextstep.jwp.model.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LoginHandlerTest {

    @Test
    @DisplayName("회원이 존재하고 비밀번호가 일치하면 로그인을 성공한다.")
    void login_success() {
        QueryParams queryParams = QueryParams.from("account=leo&password=password");
        User user = new User(1L, queryParams.get("account"), queryParams.get("password"), "leo@woowahan.com");
        InMemoryUserRepository.save(user);

        Assertions.assertDoesNotThrow(() -> LoginHandler.login(queryParams));
    }
}
